package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.BuildingFinancialAccount
import com.apyar.app.domain.model.Permission
import com.apyar.app.domain.repository.FinancialAccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetFinancialAccountUseCase(
    private val financialAccountRepository: FinancialAccountRepository,
    private val checkPermissionUseCase: CheckPermissionUseCase
) {
    suspend operator fun invoke(
        userId: String,
        buildingId: String
    ): Result<BuildingFinancialAccount?> {
        val permissionResult = checkPermissionUseCase(userId, buildingId, Permission.VIEW_FINANCIAL_DATA)
        if (permissionResult.isFailure) {
            return Result.failure(permissionResult.exceptionOrNull()!!)
        }

        val account = financialAccountRepository.getAccountByBuildingId(buildingId)
        return Result.success(account)
    }

    fun observe(
        userId: String,
        buildingId: String
    ): Flow<BuildingFinancialAccount?> {
        return financialAccountRepository.observeAccountByBuildingId(buildingId)
    }
}
