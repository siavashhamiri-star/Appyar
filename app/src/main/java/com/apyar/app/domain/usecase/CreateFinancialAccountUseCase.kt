package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.AuditEvent
import com.apyar.app.domain.model.BuildingFinancialAccount
import com.apyar.app.domain.model.Permission
import com.apyar.app.domain.repository.AuditRepository
import com.apyar.app.domain.repository.FinancialAccountRepository
import java.util.UUID

class CreateFinancialAccountUseCase(
    private val financialAccountRepository: FinancialAccountRepository,
    private val checkPermissionUseCase: CheckPermissionUseCase,
    private val auditRepository: AuditRepository
) {
    suspend operator fun invoke(
        userId: String,
        buildingId: String,
        accountName: String,
        currency: String = "TOMAN",
        initialBalance: Long = 0L
    ): Result<BuildingFinancialAccount> {
        val permissionResult = checkPermissionUseCase(userId, buildingId, Permission.MANAGE_FINANCIAL_DATA)
        if (permissionResult.isFailure) {
            return Result.failure(permissionResult.exceptionOrNull()!!)
        }

        val existing = financialAccountRepository.getAccountByBuildingId(buildingId)
        if (existing != null) {
            return Result.success(existing)
        }

        val account = BuildingFinancialAccount(
            id = UUID.randomUUID().toString(),
            buildingId = buildingId,
            accountName = accountName,
            currency = currency,
            balance = initialBalance,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            isActive = true
        )

        financialAccountRepository.createAccount(account)

        auditRepository.logEvent(
            AuditEvent(
                id = UUID.randomUUID().toString(),
                buildingId = buildingId,
                actorUserId = userId,
                action = "CREATE_FINANCIAL_ACCOUNT",
                details = "افتتاح دفتر مالی ساختمان: $accountName با موجودی اولیه: $initialBalance $currency",
                targetEntity = "BuildingFinancialAccount",
                targetEntityId = account.id
            )
        )

        return Result.success(account)
    }
}
