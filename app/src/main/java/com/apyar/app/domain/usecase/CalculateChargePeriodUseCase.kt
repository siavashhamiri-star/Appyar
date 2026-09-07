package com.apyar.app.domain.usecase

import com.apyar.app.domain.engine.ChargeCalculationEngine
import com.apyar.app.domain.engine.UnitInclusionSpec
import com.apyar.app.domain.model.AuditEvent
import com.apyar.app.domain.model.CalculationPreview
import com.apyar.app.domain.model.ChargePeriodStatus
import com.apyar.app.domain.model.InclusionStatus
import com.apyar.app.domain.model.Permission
import com.apyar.app.domain.model.UnitChargeWithDetails
import com.apyar.app.domain.repository.AuditRepository
import com.apyar.app.domain.repository.ChargePeriodRepository
import com.apyar.app.domain.repository.ChargeRuleRepository
import com.apyar.app.domain.repository.UnitChargeRepository
import java.util.UUID

class CalculateChargePeriodUseCase(
    private val chargePeriodRepository: ChargePeriodRepository,
    private val chargeRuleRepository: ChargeRuleRepository,
    private val unitChargeRepository: UnitChargeRepository,
    private val checkPermissionUseCase: CheckPermissionUseCase,
    private val auditRepository: AuditRepository,
    private val calculationEngine: ChargeCalculationEngine = ChargeCalculationEngine()
) {
    suspend operator fun invoke(
        userId: String,
        buildingId: String,
        periodId: String,
        unitSpecs: List<UnitInclusionSpec>
    ): Result<CalculationPreview> {
        val permissionResult = checkPermissionUseCase(userId, buildingId, Permission.MANAGE_FINANCIAL_DATA)
        if (permissionResult.isFailure) {
            return Result.failure(permissionResult.exceptionOrNull()!!)
        }

        val period = chargePeriodRepository.getChargePeriodById(periodId)
            ?: return Result.failure(IllegalArgumentException("دوره شارژ مورد نظر یافت نشد"))

        if (period.buildingId != buildingId) {
            return Result.failure(IllegalArgumentException("دوره شارژ به ساختمان مورد نظر تعلق ندارد"))
        }

        if (period.status == ChargePeriodStatus.FINALIZED) {
            return Result.failure(IllegalStateException("این دوره نهایی و قطعی شده است؛ محاسبه مجدد مستقیم مجاز نیست و اصلاحات باید از طریق تعدیل (Adjustment) انجام شود"))
        }

        val rule = chargeRuleRepository.getChargeRuleById(period.chargeRuleId)
            ?: return Result.failure(IllegalArgumentException("قاعده شارژ مرتبط با دوره یافت نشد"))

        // Execute engine calculation
        val computedCharges = calculationEngine.calculate(
            periodId = period.id,
            chargeRule = rule,
            unitSpecs = unitSpecs,
            totalCost = period.totalAmount
        )

        // Save computed unit charges to repository
        unitChargeRepository.deleteUnitChargesByPeriodId(period.id)
        unitChargeRepository.insertUnitCharges(computedCharges)

        // Update period status to CALCULATED
        chargePeriodRepository.updateStatus(period.id, ChargePeriodStatus.CALCULATED)

        val includedUnits = unitSpecs.filter { it.inclusionStatus == InclusionStatus.INCLUDED }.map { it.unit }
        val excludedUnits = unitSpecs.filter { it.inclusionStatus == InclusionStatus.EXCLUDED }.map { it.unit }

        val breakdown = computedCharges.map { charge ->
            val matchingUnit = unitSpecs.first { it.unit.id == charge.unitId }.unit
            UnitChargeWithDetails(unitCharge = charge, unit = matchingUnit)
        }

        val calculatedSum = computedCharges.sumOf { it.calculatedAmount }
        val roundingDiff = period.totalAmount - calculatedSum

        val preview = CalculationPreview(
            chargePeriodId = period.id,
            ruleName = rule.name,
            calculationType = rule.calculationType,
            formulaVersion = period.formulaVersion,
            totalCost = period.totalAmount,
            includedUnitsCount = includedUnits.size,
            excludedUnitsCount = excludedUnits.size,
            totalIncludedArea = includedUnits.sumOf { it.areaSquareMeters },
            totalIncludedResidents = includedUnits.sumOf { it.residentCount },
            unitBreakdown = breakdown,
            calculatedSum = calculatedSum,
            roundingDifference = roundingDiff,
            auditNote = "محاسبه دقیق با تضمین تطابق کامل مجموع سهم واحدها با هزینه کل دوره (${period.totalAmount})"
        )

        auditRepository.logEvent(
            AuditEvent(
                id = UUID.randomUUID().toString(),
                buildingId = buildingId,
                actorUserId = userId,
                action = "CALCULATE_CHARGE_PERIOD",
                details = "اجرای محاسبه شارژ دوره: ${period.title} بر اساس قاعده ${rule.name} (تعداد مشمول: ${includedUnits.size}، مجموع سهم: $calculatedSum)",
                targetEntity = "ChargePeriod",
                targetEntityId = period.id
            )
        )

        return Result.success(preview)
    }
}
