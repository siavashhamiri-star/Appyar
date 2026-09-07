package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.Permission
import com.apyar.app.domain.model.UnitChargeWithDetails
import com.apyar.app.domain.repository.ChargePeriodRepository
import com.apyar.app.domain.repository.UnitChargeRepository
import com.apyar.app.domain.repository.UnitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GetUnitChargesUseCase(
    private val unitChargeRepository: UnitChargeRepository,
    private val chargePeriodRepository: ChargePeriodRepository,
    private val unitRepository: UnitRepository,
    private val checkPermissionUseCase: CheckPermissionUseCase
) {
    suspend operator fun invoke(
        userId: String,
        buildingId: String,
        periodId: String
    ): Result<List<UnitChargeWithDetails>> {
        val permissionResult = checkPermissionUseCase(userId, buildingId, Permission.VIEW_FINANCIAL_DATA)
        if (permissionResult.isFailure) {
            return Result.failure(permissionResult.exceptionOrNull()!!)
        }

        val period = chargePeriodRepository.getChargePeriodById(periodId)
            ?: return Result.failure(IllegalArgumentException("دوره شارژ مورد نظر یافت نشد"))

        if (period.buildingId != buildingId) {
            return Result.failure(IllegalArgumentException("عدم دسترسی به ساختمان مورد نظر"))
        }

        val charges = unitChargeRepository.getUnitChargesByPeriodId(periodId)
        val units = unitRepository.getUnitsByBuilding(buildingId)
        val unitMap = units.associateBy { it.id }

        val breakdown = charges.mapNotNull { charge ->
            val unit = unitMap[charge.unitId]
            if (unit != null) {
                UnitChargeWithDetails(unitCharge = charge, unit = unit)
            } else null
        }

        return Result.success(breakdown)
    }

    fun observe(
        userId: String,
        buildingId: String,
        periodId: String
    ): Flow<List<UnitChargeWithDetails>> {
        return combine(
            unitChargeRepository.observeUnitChargesByPeriodId(periodId),
            unitRepository.observeUnitsByBuilding(buildingId)
        ) { charges, units ->
            val unitMap = units.associateBy { it.id }
            charges.mapNotNull { charge ->
                val unit = unitMap[charge.unitId]
                if (unit != null) {
                    UnitChargeWithDetails(unitCharge = charge, unit = unit)
                } else null
            }
        }
    }
}
