package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.AuditEvent
import com.apyar.app.domain.model.FinancialTransaction
import com.apyar.app.domain.model.Permission
import com.apyar.app.domain.model.TransactionType
import com.apyar.app.domain.model.UnitCharge
import com.apyar.app.domain.repository.AuditRepository
import com.apyar.app.domain.repository.FinancialAccountRepository
import com.apyar.app.domain.repository.FinancialTransactionRepository
import com.apyar.app.domain.repository.UnitChargeRepository
import java.util.UUID

class CreateAdjustmentUseCase(
    private val unitChargeRepository: UnitChargeRepository,
    private val financialAccountRepository: FinancialAccountRepository,
    private val financialTransactionRepository: FinancialTransactionRepository,
    private val checkPermissionUseCase: CheckPermissionUseCase,
    private val auditRepository: AuditRepository
) {
    suspend operator fun invoke(
        userId: String,
        buildingId: String,
        unitChargeId: String,
        adjustmentAmount: Long, // positive (increase) or negative (discount/reduction)
        reason: String
    ): Result<UnitCharge> {
        val permissionResult = checkPermissionUseCase(userId, buildingId, Permission.MANAGE_FINANCIAL_DATA)
        if (permissionResult.isFailure) {
            return Result.failure(permissionResult.exceptionOrNull()!!)
        }

        val charge = unitChargeRepository.getUnitChargeById(unitChargeId)
            ?: return Result.failure(IllegalArgumentException("صورت‌حساب شارژ واحد یافت نشد"))

        val newAdjustment = charge.adjustmentAmount + adjustmentAmount
        val newFinalAmount = (charge.calculatedAmount + newAdjustment).coerceAtLeast(0L)
        val newRemaining = (newFinalAmount - charge.paidAmount).coerceAtLeast(0L)
        val notes = "${charge.calculationNotes ?: ""}\n[تعدیل]: $reason (${if (adjustmentAmount >= 0) "+$adjustmentAmount" else "$adjustmentAmount"})"

        unitChargeRepository.updateAdjustment(
            id = unitChargeId,
            adjustmentAmount = newAdjustment,
            finalAmount = newFinalAmount,
            remainingAmount = newRemaining,
            notes = notes.trim()
        )

        val account = financialAccountRepository.getAccountByBuildingId(buildingId)
        if (account != null) {
            val transaction = FinancialTransaction(
                id = UUID.randomUUID().toString(),
                buildingId = buildingId,
                accountId = account.id,
                unitId = charge.unitId,
                type = TransactionType.ADJUSTMENT,
                amount = adjustmentAmount,
                description = "تعدیل شارژ: $reason",
                reference = charge.id,
                createdAt = System.currentTimeMillis(),
                createdBy = userId
            )
            financialTransactionRepository.recordTransaction(transaction)
        }

        auditRepository.logEvent(
            AuditEvent(
                id = UUID.randomUUID().toString(),
                buildingId = buildingId,
                actorUserId = userId,
                action = "CREATE_ADJUSTMENT",
                details = "ثبت تعدیل شارژ واحد به مبلغ $adjustmentAmount (دلیل: $reason)",
                targetEntity = "UnitCharge",
                targetEntityId = charge.id
            )
        )

        val updated = charge.copy(
            adjustmentAmount = newAdjustment,
            finalAmount = newFinalAmount,
            remainingAmount = newRemaining,
            calculationNotes = notes.trim()
        )
        return Result.success(updated)
    }
}
