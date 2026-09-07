package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.ChargePeriod
import com.apyar.app.domain.model.Permission
import com.apyar.app.domain.repository.ChargePeriodRepository
import kotlinx.coroutines.flow.Flow

class GetChargePeriodUseCase(
    private val chargePeriodRepository: ChargePeriodRepository,
    private val checkPermissionUseCase: CheckPermissionUseCase? = null
) {
    suspend operator fun invoke(
        buildingId: String,
        periodId: String,
        userId: String? = null
    ): Result<ChargePeriod> {
        if (buildingId.isBlank() || periodId.isBlank()) {
            return Result.failure(IllegalArgumentException("شناسه ساختمان یا دوره شارژ نامعتبر است"))
        }

        if (userId != null && checkPermissionUseCase != null) {
            val perm = checkPermissionUseCase(userId, buildingId, Permission.VIEW_FINANCIAL_DATA)
            if (perm.isFailure) {
                return Result.failure(perm.exceptionOrNull()!!)
            }
        }

        val period = chargePeriodRepository.getChargePeriodById(periodId)
            ?: return Result.failure(IllegalArgumentException("دوره شارژ با شناسه مورد نظر یافت نشد"))

        if (period.buildingId != buildingId) {
            return Result.failure(IllegalArgumentException("دوره شارژ به ساختمان دیگری تعلق دارد"))
        }

        return Result.success(period)
    }

    suspend fun getPeriods(buildingId: String, userId: String? = null): Result<List<ChargePeriod>> {
        if (buildingId.isBlank()) {
            return Result.failure(IllegalArgumentException("شناسه ساختمان نامعتبر است"))
        }

        if (userId != null && checkPermissionUseCase != null) {
            val perm = checkPermissionUseCase(userId, buildingId, Permission.VIEW_FINANCIAL_DATA)
            if (perm.isFailure) {
                return Result.failure(perm.exceptionOrNull()!!)
            }
        }

        val list = chargePeriodRepository.getChargePeriodsByBuildingId(buildingId)
        return Result.success(list)
    }

    fun observePeriods(buildingId: String): Flow<List<ChargePeriod>> {
        return chargePeriodRepository.observeChargePeriodsByBuildingId(buildingId)
    }
}
