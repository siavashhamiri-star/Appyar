package com.apyar.app.domain.repository

import com.apyar.app.domain.model.BuildingServiceProvider
import com.apyar.app.domain.model.BuildingServiceProviderWithDetails
import com.apyar.app.domain.model.ServiceCategory
import com.apyar.app.domain.model.ServiceProvider
import kotlinx.coroutines.flow.Flow

interface ServiceProviderRepository {
    suspend fun createServiceProvider(provider: ServiceProvider): ServiceProvider
    suspend fun updateServiceProvider(provider: ServiceProvider)
    suspend fun getServiceProviderById(id: String): ServiceProvider?
    suspend fun getServiceProviderByMobileNumber(mobileNumber: String): ServiceProvider?
    fun observeAllServiceProviders(): Flow<List<ServiceProvider>>
    suspend fun getAllServiceProviders(): List<ServiceProvider>

    suspend fun linkProviderToBuilding(link: BuildingServiceProvider): BuildingServiceProvider
    suspend fun updateBuildingServiceProvider(link: BuildingServiceProvider)
    suspend fun getBuildingServiceProviderById(id: String): BuildingServiceProvider?
    suspend fun getLinkByBuildingAndProvider(buildingId: String, serviceProviderId: String): BuildingServiceProvider?
    fun observeBuildingServiceProviders(buildingId: String): Flow<List<BuildingServiceProviderWithDetails>>
    suspend fun getBuildingServiceProviders(buildingId: String): List<BuildingServiceProviderWithDetails>
    suspend fun getProvidersByCategory(buildingId: String, category: ServiceCategory): List<BuildingServiceProviderWithDetails>
    suspend fun getPrimaryProviderForCategory(buildingId: String, category: ServiceCategory): BuildingServiceProviderWithDetails?
}
