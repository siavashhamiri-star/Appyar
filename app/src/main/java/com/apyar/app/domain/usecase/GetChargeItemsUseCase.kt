package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.ChargeItem
import com.apyar.app.domain.model.Permission
import com.apyar.app.domain.repository.ChargeItemRepository
import com.apyar.app.domain.repository.ChargePeriodRepository
import kotlinx.coroutines.flow.Flow

class GetChargeItemsUseCase(
    private val chargeItemRepository: ChargeItemRepository,
    private val chargePeriodRepository: ChargePeriodRepository? = null,
    private val checkPermissionUseCase: CheckPermissionUseCase? = null
) {
    suspend operator fun invoke(
        buildingId: String,
        chargePeriodId: String,
        userId: String? = null
    ): Result<List<ChargeItem>> {
        if (buildingId.isBlank() || chargePeriodId.isBlank()) {
            return Result.failure(IllegalArgumentException("شناسه ساختمان یا دوره نامعتبر است"))
        }

        if (userId != null && checkPermissionUseCase != null) {
            val perm = checkPermissionUseCase(userId, buildingId, Permission.VIEW_FINANCIAL_DATA)
            if (perm.isFailure) {
                return Result.failure(perm.exceptionOrNull()!!)
            }
        }

        if (chargePeriodRepository != null) {
            val period = chargePeriodRepository.getChargePeriodById(chargePeriodId)
            if (period != null && period.buildingId != buildingId) {
                return Result.failure(IllegalArgumentException("دوره شارژ به ساختمان دیگری تعلق دارد"))
            }
        }

        val items = chargeItemRepository.getChargeItemsByPeriodId(chargePeriodId)
        val filtered = items.filter { it.buildingId == buildingId }
        return Result.success(filtered)
    }

    suspend fun getByUnitId(
        buildingId: String,
        unitId: String,
        userId: String? = null
    ): Result<List<ChargeItem>> {
        if (buildingId.isBlank() || unitId.isBlank()) {
            return Result.failure(IllegalArgumentException("شناسه ساختمان یا واحد نامعتبر است"))
        }

        if (userId != null && checkPermissionUseCase != null) {
            val perm = checkPermissionUseCase(userId, buildingId, Permission.VIEW_FINANCIAL_DATA)
            if (perm.isFailure) {
                return Result.failure(perm.exceptionOrNull()!!)
            }
        }

        val items = chargeItemRepository.getChargeItemsByUnitId(unitId)
        return Result.success(items.filter { it.buildingId == buildingId })
    }

    fun observeItems(chargePeriodId: String): Flow<List<ChargeItem>> {
        return chargeItemRepository.observeChargeItemsByPeriodId(chargePeriodId)
    }
}
