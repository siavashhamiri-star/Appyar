package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.Permission
import com.apyar.app.domain.model.ServiceRecordWithDetails
import com.apyar.app.domain.repository.ServiceRecordRepository
import kotlinx.coroutines.flow.Flow

class GetBuildingServiceRecordsUseCase(
    private val serviceRecordRepository: ServiceRecordRepository,
    private val checkPermissionUseCase: CheckPermissionUseCase
) {
    suspend fun getList(userId: String, buildingId: String): Result<List<ServiceRecordWithDetails>> {
        val allowed = checkPermissionUseCase.hasPermission(buildingId, userId, Permission.VIEW_SERVICE_RECORDS)
        if (!allowed) {
            checkPermissionUseCase.enforce(buildingId, userId, Permission.VIEW_SERVICE_RECORDS)
        }
        val list = serviceRecordRepository.getServiceRecordsByBuilding(buildingId)
        return Result.success(list)
    }

    fun observe(userId: String, buildingId: String): Flow<List<ServiceRecordWithDetails>> {
        return serviceRecordRepository.observeServiceRecordsByBuilding(buildingId)
    }
}
