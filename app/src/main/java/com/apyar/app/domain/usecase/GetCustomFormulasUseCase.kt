package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.CustomFormula
import com.apyar.app.domain.model.Permission
import com.apyar.app.domain.repository.CustomFormulaRepository
import kotlinx.coroutines.flow.Flow

class GetCustomFormulasUseCase(
    private val customFormulaRepository: CustomFormulaRepository,
    private val checkPermissionUseCase: CheckPermissionUseCase
) {
    suspend operator fun invoke(
        userId: String,
        buildingId: String
    ): Result<List<CustomFormula>> {
        val permissionResult = checkPermissionUseCase(userId, buildingId, Permission.VIEW_FINANCIAL_DATA)
        if (permissionResult.isFailure) {
            return Result.failure(permissionResult.exceptionOrNull()!!)
        }

        val list = customFormulaRepository.getCustomFormulasByBuildingId(buildingId)
        return Result.success(list)
    }

    fun observe(
        userId: String,
        buildingId: String
    ): Flow<List<CustomFormula>> {
        return customFormulaRepository.observeCustomFormulasByBuildingId(buildingId)
    }
}
