package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.Agreement
import com.apyar.app.domain.model.AgreementStatus
import com.apyar.app.domain.model.AuditEvent
import com.apyar.app.domain.model.PaymentArrangement
import com.apyar.app.domain.model.Permission
import com.apyar.app.domain.model.UnitCharge
import com.apyar.app.domain.repository.AgreementRepository
import com.apyar.app.domain.repository.AuditRepository
import com.apyar.app.domain.repository.ChargePeriodRepository
import com.apyar.app.domain.repository.UnitChargeRepository
import com.apyar.app.domain.repository.UnitRepository
import kotlinx.coroutines.flow.first

class CalculateParkingAdjustmentUseCase(
    private val unitChargeRepository: UnitChargeRepository,
    private val agreementRepository: AgreementRepository,
    private val unitRepository: UnitRepository,
    private val chargePeriodRepository: ChargePeriodRepository,
    private val checkPermissionUseCase: CheckPermissionUseCase,
    private val auditRepository: AuditRepository
) {
    suspend operator fun invoke(
        userId: String,
        buildingId: String,
        periodId: String
    ): Result<List<UnitCharge>> {
        checkPermissionUseCase.enforce(buildingId, userId, Permission.MANAGE_FINANCIAL_DATA)

        val period = chargePeriodRepository.getChargePeriodById(periodId)
            ?: return Result.failure(IllegalArgumentException("دوره شارژ یافت نشد"))

        if (period.buildingId != buildingId) {
            return Result.failure(IllegalArgumentException("عدم تطابق شناسه ساختمان"))
        }

        val allCharges = unitChargeRepository.getUnitChargesByPeriodId(periodId)
        if (allCharges.isEmpty()) {
            return Result.failure(IllegalStateException("هیچ صورت‌حسابی برای این دوره وجود ندارد"))
        }

        val allAgreements = agreementRepository.getAgreementsByBuilding(buildingId).first()
        val activeParkingAgreements = allAgreements.filter { agreement ->
            agreement.status == AgreementStatus.ACTIVE &&
            (agreement.relatedParkingId != null || agreement.agreementType.name.startsWith("PARKING"))
        }

        val chargesByUnitId = allCharges.associateBy { it.unitId }.toMutableMap()
        val updatedCharges = mutableListOf<UnitCharge>()

        for (agreement in activeParkingAgreements) {
            val sourceCharge = chargesByUnitId[agreement.sourceUnitId]
            val targetCharge = chargesByUnitId[agreement.targetUnitId]

            if (sourceCharge == null || targetCharge == null) continue

            val adjustmentValue = when (agreement.financialArrangement) {
                PaymentArrangement.FULL_CHARGE_OFFSET -> sourceCharge.calculatedAmount
                PaymentArrangement.CHARGE_OFFSET,
                PaymentArrangement.MONTHLY_AMOUNT,
                PaymentArrangement.FIXED_AMOUNT,
                PaymentArrangement.CUSTOM -> agreement.amount ?: 0L
                PaymentArrangement.NO_PAYMENT -> 0L
            }

            if (adjustmentValue <= 0L) continue

            // 1. Source Unit receives discount / negative adjustment
            val newSourceAdj = sourceCharge.adjustmentAmount - adjustmentValue
            val newSourceFinal = (sourceCharge.calculatedAmount + newSourceAdj).coerceAtLeast(0L)
            val newSourceRemaining = (newSourceFinal - sourceCharge.paidAmount).coerceAtLeast(0L)
            val sourceNote = "${sourceCharge.calculationNotes ?: ""}\n[تعدیل توافق پارکینگ]: کسر $adjustmentValue به دلیل واگذاری پارکینگ".trim()

            val updatedSource = sourceCharge.copy(
                adjustmentAmount = newSourceAdj,
                finalAmount = newSourceFinal,
                remainingAmount = newSourceRemaining,
                calculationNotes = sourceNote
            )
            unitChargeRepository.updateAdjustment(
                id = updatedSource.id,
                adjustmentAmount = newSourceAdj,
                finalAmount = newSourceFinal,
                remainingAmount = newSourceRemaining,
                notes = sourceNote
            )
            chargesByUnitId[sourceCharge.unitId] = updatedSource

            // 2. Target Unit receives positive adjustment for parking use
            val newTargetAdj = targetCharge.adjustmentAmount + adjustmentValue
            val newTargetFinal = (targetCharge.calculatedAmount + newTargetAdj).coerceAtLeast(0L)
            val newTargetRemaining = (newTargetFinal - targetCharge.paidAmount).coerceAtLeast(0L)
            val targetNote = "${targetCharge.calculationNotes ?: ""}\n[تعدیل توافق پارکینگ]: اضافه شدن $adjustmentValue بابت استفاده از پارکینگ".trim()

            val updatedTarget = targetCharge.copy(
                adjustmentAmount = newTargetAdj,
                finalAmount = newTargetFinal,
                remainingAmount = newTargetRemaining,
                calculationNotes = targetNote
            )
            unitChargeRepository.updateAdjustment(
                id = updatedTarget.id,
                adjustmentAmount = newTargetAdj,
                finalAmount = newTargetFinal,
                remainingAmount = newTargetRemaining,
                notes = targetNote
            )
            chargesByUnitId[targetCharge.unitId] = updatedTarget

            auditRepository.recordEvent(
                AuditEvent(
                    actor = userId,
                    action = "CALCULATE_PARKING_ADJUSTMENT",
                    entity = "Agreement",
                    entityId = agreement.id,
                    buildingId = buildingId,
                    details = "اعمال اثر مالی توافق پارکینگ به مبلغ $adjustmentValue بر شارژ واحدهای ${sourceCharge.unitId} و ${targetCharge.unitId}"
                )
            )
        }

        return Result.success(chargesByUnitId.values.toList())
    }
}
