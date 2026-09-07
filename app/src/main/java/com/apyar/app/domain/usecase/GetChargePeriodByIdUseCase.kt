package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.ChargePeriod
import com.apyar.app.domain.model.Permission
import com.apyar.app.domain.repository.ChargePeriodRepository

class GetChargePeriodByIdUseCase(
    private val chargePeriodRepository: ChargePeriodRepository,
    private val checkPermissionUseCase: CheckPermissionUseCase
) {
    suspend operator fun invoke(
        userId: String,
        buildingId: String,
        periodId: String
    ): Result<ChargePeriod> {
        val permissionResult = checkPermissionUseCase(userId, buildingId, Permission.VIEW_FINANCIAL_DATA)
        if (permissionResult.isFailure) {
            return Result.failure(permissionResult.exceptionOrNull()!!)
        }

        val period = chargePeriodRepository.getChargePeriodById(periodId)
            ?: return Result.failure(IllegalArgumentException("دوره شارژ با شناسه مورد نظر یافت نشد"))

        if (period.buildingId != buildingId) {
            return Result.failure(IllegalArgumentException("عدم تطابق شناسه ساختمان"))
        }

        return Result.success(period)
    }
}
