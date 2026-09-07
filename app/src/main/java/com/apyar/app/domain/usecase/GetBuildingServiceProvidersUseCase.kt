package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.BuildingServiceProviderWithDetails
import com.apyar.app.domain.model.Permission
import com.apyar.app.domain.repository.ServiceProviderRepository
import kotlinx.coroutines.flow.Flow

class GetBuildingServiceProvidersUseCase(
    private val serviceProviderRepository: ServiceProviderRepository,
    private val checkPermissionUseCase: CheckPermissionUseCase
) {
    suspend fun getList(userId: String, buildingId: String): Result<List<BuildingServiceProviderWithDetails>> {
        val allowed = checkPermissionUseCase.hasPermission(buildingId, userId, Permission.VIEW_SERVICE_PROVIDERS)
        if (!allowed) {
            checkPermissionUseCase.enforce(buildingId, userId, Permission.VIEW_SERVICE_PROVIDERS)
        }
        val list = serviceProviderRepository.getBuildingServiceProviders(buildingId)
        return Result.success(list)
    }

    fun observe(userId: String, buildingId: String): Flow<List<BuildingServiceProviderWithDetails>> {
        return serviceProviderRepository.observeBuildingServiceProviders(buildingId)
    }
}
