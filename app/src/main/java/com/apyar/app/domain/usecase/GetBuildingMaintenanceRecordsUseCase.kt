package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.MaintenanceRecord
import com.apyar.app.domain.model.Permission
import com.apyar.app.domain.repository.MaintenanceRepository
import kotlinx.coroutines.flow.Flow

class GetBuildingMaintenanceRecordsUseCase(
    private val maintenanceRepository: MaintenanceRepository,
    private val checkPermissionUseCase: CheckPermissionUseCase
) {
    suspend fun getList(userId: String, buildingId: String): Result<List<MaintenanceRecord>> {
        val allowed = checkPermissionUseCase.hasPermission(buildingId, userId, Permission.VIEW_MAINTENANCE)
        if (!allowed) {
            checkPermissionUseCase.enforce(buildingId, userId, Permission.VIEW_MAINTENANCE)
        }
        val list = maintenanceRepository.getMaintenanceRecordsByBuilding(buildingId)
        return Result.success(list)
    }

    suspend fun getUpcoming(userId: String, buildingId: String, fromTimestamp: Long = System.currentTimeMillis()): Result<List<MaintenanceRecord>> {
        val allowed = checkPermissionUseCase.hasPermission(buildingId, userId, Permission.VIEW_MAINTENANCE)
        if (!allowed) {
            checkPermissionUseCase.enforce(buildingId, userId, Permission.VIEW_MAINTENANCE)
        }
        val list = maintenanceRepository.getUpcomingMaintenance(buildingId, fromTimestamp)
        return Result.success(list)
    }

    fun observe(userId: String, buildingId: String): Flow<List<MaintenanceRecord>> {
        return maintenanceRepository.observeMaintenanceRecordsByBuilding(buildingId)
    }
}
