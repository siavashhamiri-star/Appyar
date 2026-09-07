package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.FinancialTransaction
import com.apyar.app.domain.model.Permission
import com.apyar.app.domain.repository.FinancialTransactionRepository
import kotlinx.coroutines.flow.Flow

class GetTransactionsUseCase(
    private val financialTransactionRepository: FinancialTransactionRepository,
    private val checkPermissionUseCase: CheckPermissionUseCase
) {
    suspend operator fun invoke(
        userId: String,
        buildingId: String
    ): Result<List<FinancialTransaction>> {
        val permissionResult = checkPermissionUseCase(userId, buildingId, Permission.VIEW_FINANCIAL_DATA)
        if (permissionResult.isFailure) {
            return Result.failure(permissionResult.exceptionOrNull()!!)
        }

        val list = financialTransactionRepository.getTransactionsByBuildingId(buildingId)
        return Result.success(list)
    }

    fun observe(
        userId: String,
        buildingId: String
    ): Flow<List<FinancialTransaction>> {
        return financialTransactionRepository.observeTransactionsByBuildingId(buildingId)
    }
}
