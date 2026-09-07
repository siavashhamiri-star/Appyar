package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.BuildingBill
import com.apyar.app.domain.model.Permission
import com.apyar.app.domain.repository.BuildingBillRepository
import kotlinx.coroutines.flow.Flow

class GetBuildingBillsUseCase(
    private val buildingBillRepository: BuildingBillRepository,
    private val checkPermissionUseCase: CheckPermissionUseCase
) {
    suspend fun getList(userId: String, buildingId: String): Result<List<BuildingBill>> {
        val allowed = checkPermissionUseCase.hasPermission(buildingId, userId, Permission.VIEW_BUILDING_BILLS)
        if (!allowed) {
            checkPermissionUseCase.enforce(buildingId, userId, Permission.VIEW_BUILDING_BILLS)
        }
        val list = buildingBillRepository.getBuildingBillsByBuilding(buildingId)
        return Result.success(list)
    }

    fun observe(userId: String, buildingId: String): Flow<List<BuildingBill>> {
        return buildingBillRepository.observeBuildingBillsByBuilding(buildingId)
    }
}
