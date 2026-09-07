package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.ChargePeriod
import com.apyar.app.domain.model.Permission
import com.apyar.app.domain.repository.ChargePeriodRepository
import kotlinx.coroutines.flow.Flow

class GetChargePeriodsUseCase(
    private val chargePeriodRepository: ChargePeriodRepository,
    private val checkPermissionUseCase: CheckPermissionUseCase
) {
    suspend operator fun invoke(
        userId: String,
        buildingId: String
    ): Result<List<ChargePeriod>> {
        val permissionResult = checkPermissionUseCase(userId, buildingId, Permission.VIEW_FINANCIAL_DATA)
        if (permissionResult.isFailure) {
            return Result.failure(permissionResult.exceptionOrNull()!!)
        }

        val list = chargePeriodRepository.getChargePeriodsByBuildingId(buildingId)
        return Result.success(list)
    }

    fun observe(
        userId: String,
        buildingId: String
    ): Flow<List<ChargePeriod>> {
        return chargePeriodRepository.observeChargePeriodsByBuildingId(buildingId)
    }
}
