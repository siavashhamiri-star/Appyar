package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.AuditEvent
import com.apyar.app.domain.model.ChargePeriod
import com.apyar.app.domain.model.ChargePeriodStatus
import com.apyar.app.domain.model.FinancialTransaction
import com.apyar.app.domain.model.Permission
import com.apyar.app.domain.model.TransactionType
import com.apyar.app.domain.repository.AuditRepository
import com.apyar.app.domain.repository.ChargePeriodRepository
import com.apyar.app.domain.repository.FinancialAccountRepository
import com.apyar.app.domain.repository.FinancialTransactionRepository
import com.apyar.app.domain.repository.UnitChargeRepository
import java.util.UUID

class FinalizeChargePeriodUseCase(
    private val chargePeriodRepository: ChargePeriodRepository,
    private val unitChargeRepository: UnitChargeRepository,
    private val financialAccountRepository: FinancialAccountRepository,
    private val financialTransactionRepository: FinancialTransactionRepository,
    private val checkPermissionUseCase: CheckPermissionUseCase,
    private val auditRepository: AuditRepository
) {
    suspend operator fun invoke(
        userId: String,
        buildingId: String,
        periodId: String
    ): Result<ChargePeriod> {
        val permissionResult = checkPermissionUseCase(userId, buildingId, Permission.MANAGE_FINANCIAL_DATA)
        if (permissionResult.isFailure) {
            return Result.failure(permissionResult.exceptionOrNull()!!)
        }

        val period = chargePeriodRepository.getChargePeriodById(periodId)
            ?: return Result.failure(IllegalArgumentException("دوره شارژ مورد نظر یافت نشد"))

        if (period.buildingId != buildingId) {
            return Result.failure(IllegalArgumentException("عدم تطابق شناسه ساختمان"))
        }

        if (period.status == ChargePeriodStatus.FINALIZED) {
            return Result.failure(IllegalStateException("این دوره قبلاً قطعی و نهایی شده است"))
        }

        val charges = unitChargeRepository.getUnitChargesByPeriodId(periodId)
        if (charges.isEmpty()) {
            return Result.failure(IllegalStateException("قبل از نهایی‌سازی، باید محاسبه شارژ انجام شود"))
        }

        val finalizedAt = System.currentTimeMillis()
        chargePeriodRepository.updateStatus(periodId, ChargePeriodStatus.FINALIZED, finalizedAt)

        // Record a summary ledger transaction for this finalized period
        val account = financialAccountRepository.getAccountByBuildingId(buildingId)
        if (account != null) {
            val totalFinalized = charges.sumOf { it.finalAmount }
            val transaction = FinancialTransaction(
                id = UUID.randomUUID().toString(),
                buildingId = buildingId,
                accountId = account.id,
                unitId = null,
                type = TransactionType.CHARGE,
                amount = totalFinalized,
                description = "ثبت قطعی شارژ دوره: ${period.title} برای ${charges.size} واحد",
                reference = period.id,
                createdAt = finalizedAt,
                createdBy = userId
            )
            financialTransactionRepository.recordTransaction(transaction)
        }

        auditRepository.logEvent(
            AuditEvent(
                id = UUID.randomUUID().toString(),
                buildingId = buildingId,
                actorUserId = userId,
                action = "FINALIZE_CHARGE_PERIOD",
                details = "نهایی‌سازی و قطعیت دوره شارژ: ${period.title} (مبلغ کل: ${period.totalAmount})",
                targetEntity = "ChargePeriod",
                targetEntityId = period.id
            )
        )

        val updatedPeriod = period.copy(
            status = ChargePeriodStatus.FINALIZED,
            finalizedAt = finalizedAt
        )
        return Result.success(updatedPeriod)
    }
}
