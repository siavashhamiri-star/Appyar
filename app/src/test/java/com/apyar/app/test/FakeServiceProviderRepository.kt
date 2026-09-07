package com.apyar.app.test

import com.apyar.app.domain.model.BuildingServiceProvider
import com.apyar.app.domain.model.ProviderCategory
import com.apyar.app.domain.model.ProviderRoleInBuilding
import com.apyar.app.domain.model.ProviderStatus
import com.apyar.app.domain.model.ServiceProvider
import com.apyar.app.domain.repository.ServiceProviderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class FakeServiceProviderRepository : ServiceProviderRepository {
    private val providers = MutableStateFlow<Map<String, ServiceProvider>>(emptyMap())
    private val buildingProviders = MutableStateFlow<List<BuildingServiceProvider>>(emptyList())

    override suspend fun createServiceProvider(provider: ServiceProvider): Result<ServiceProvider> {
        providers.value = providers.value + (provider.id to provider)
        return Result.success(provider)
    }

    override suspend fun getServiceProviderById(providerId: String): ServiceProvider? {
        return providers.value[providerId]
    }

    override fun getServiceProviders(): Flow<List<ServiceProvider>> {
        return providers.asStateFlow().map { it.values.toList() }
    }

    override suspend fun updateServiceProvider(provider: ServiceProvider): Result<Unit> {
        providers.value = providers.value + (provider.id to provider)
        return Result.success(Unit)
    }

    override suspend fun linkServiceProviderToBuilding(
        buildingId: String,
        providerId: String,
        roleInBuilding: ProviderRoleInBuilding,
        category: ProviderCategory,
        status: ProviderStatus
    ): Result<Unit> {
        val provider = providers.value[providerId] ?: return Result.failure(IllegalArgumentException("Provider not found"))
        val existing = buildingProviders.value.filterNot { it.buildingId == buildingId && it.provider.id == providerId }
        val newEntry = BuildingServiceProvider(
            buildingId = buildingId,
            provider = provider,
            roleInBuilding = roleInBuilding,
            category = category,
            status = status,
            assignedAt = System.currentTimeMillis()
        )
        buildingProviders.value = existing + newEntry
        return Result.success(Unit)
    }

    override suspend fun updateBuildingServiceProviderRole(
        buildingId: String,
        providerId: String,
        roleInBuilding: ProviderRoleInBuilding,
        status: ProviderStatus,
        category: ProviderCategory
    ): Result<Unit> {
        val entry = buildingProviders.value.find { it.buildingId == buildingId && it.provider.id == providerId }
            ?: return Result.failure(IllegalArgumentException("Not linked"))
        val updated = entry.copy(roleInBuilding = roleInBuilding, status = status, category = category)
        buildingProviders.value = buildingProviders.value.filterNot { it.buildingId == buildingId && it.provider.id == providerId } + updated
        return Result.success(Unit)
    }

    override fun getBuildingServiceProviders(buildingId: String): Flow<List<BuildingServiceProvider>> {
        return buildingProviders.asStateFlow().map { list -> list.filter { it.buildingId == buildingId } }
    }

    override suspend fun getBuildingServiceProvider(buildingId: String, providerId: String): BuildingServiceProvider? {
        return buildingProviders.value.find { it.buildingId == buildingId && it.provider.id == providerId }
    }
}
