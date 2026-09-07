package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.AuditEvent
import com.apyar.app.domain.model.ExpenseCategory
import com.apyar.app.domain.model.FinancialTransaction
import com.apyar.app.domain.model.Permission
import com.apyar.app.domain.model.TransactionType
import com.apyar.app.domain.repository.AuditRepository
import com.apyar.app.domain.repository.FinancialAccountRepository
import com.apyar.app.domain.repository.FinancialTransactionRepository
import java.util.UUID

class RecordTransactionUseCase(
    private val financialTransactionRepository: FinancialTransactionRepository,
    private val financialAccountRepository: FinancialAccountRepository,
    private val checkPermissionUseCase: CheckPermissionUseCase,
    private val auditRepository: AuditRepository
) {
    suspend operator fun invoke(
        userId: String,
        buildingId: String,
        accountId: String,
        unitId: String? = null,
        type: TransactionType,
        amount: Long,
        description: String,
        reference: String? = null,
        category: ExpenseCategory? = null
    ): Result<FinancialTransaction> {
        val permissionResult = checkPermissionUseCase(userId, buildingId, Permission.MANAGE_FINANCIAL_DATA)
        if (permissionResult.isFailure) {
            return Result.failure(permissionResult.exceptionOrNull()!!)
        }

        if (amount < 0L) {
            return Result.failure(IllegalArgumentException("مبلغ تراکنش نمی‌تواند منفی باشد"))
        }

        val transaction = FinancialTransaction(
            id = UUID.randomUUID().toString(),
            buildingId = buildingId,
            accountId = accountId,
            unitId = unitId,
            type = type,
            amount = amount,
            description = description,
            reference = reference,
            category = category,
            createdAt = System.currentTimeMillis(),
            createdBy = userId
        )

        financialTransactionRepository.recordTransaction(transaction)

        // Update financial account balance
        val account = financialAccountRepository.getAccountById(accountId)
        if (account != null) {
            val newBalance = if (type.isCredit) {
                account.balance + amount
            } else if (type == TransactionType.EXPENSE || type == TransactionType.REFUND) {
                account.balance - amount
            } else {
                account.balance
            }
            financialAccountRepository.updateBalance(accountId, newBalance)
        }

        auditRepository.logEvent(
            AuditEvent(
                id = UUID.randomUUID().toString(),
                buildingId = buildingId,
                actorUserId = userId,
                action = "RECORD_TRANSACTION",
                details = "ثبت تراکنش ${type.titleFa} به مبلغ $amount (توضیح: $description)",
                targetEntity = "FinancialTransaction",
                targetEntityId = transaction.id
            )
        )

        return Result.success(transaction)
    }
}
