package com.apyar.app.data.repository

import com.apyar.app.data.local.dao.BuildingServiceProviderDao
import com.apyar.app.data.local.dao.ServiceProviderDao
import com.apyar.app.data.mapper.toDomain
import com.apyar.app.data.mapper.toEntity
import com.apyar.app.domain.model.BuildingServiceProvider
import com.apyar.app.domain.model.BuildingServiceProviderWithDetails
import com.apyar.app.domain.model.ServiceCategory
import com.apyar.app.domain.model.ServiceProvider
import com.apyar.app.domain.repository.ServiceProviderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ServiceProviderRepositoryImpl(
    private val serviceProviderDao: ServiceProviderDao,
    private val buildingServiceProviderDao: BuildingServiceProviderDao
) : ServiceProviderRepository {

    override suspend fun createServiceProvider(provider: ServiceProvider): ServiceProvider {
        serviceProviderDao.insertServiceProvider(provider.toEntity())
        return provider
    }

    override suspend fun updateServiceProvider(provider: ServiceProvider) {
        serviceProviderDao.updateServiceProvider(provider.toEntity())
    }

    override suspend fun getServiceProviderById(id: String): ServiceProvider? {
        return serviceProviderDao.getServiceProviderById(id)?.toDomain()
    }

    override suspend fun getServiceProviderByMobileNumber(mobileNumber: String): ServiceProvider? {
        return serviceProviderDao.getServiceProviderByMobileNumber(mobileNumber)?.toDomain()
    }

    override fun observeAllServiceProviders(): Flow<List<ServiceProvider>> {
        return serviceProviderDao.observeAllServiceProviders().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getAllServiceProviders(): List<ServiceProvider> {
        return serviceProviderDao.getAllServiceProviders().map { it.toDomain() }
    }

    override suspend fun linkProviderToBuilding(link: BuildingServiceProvider): BuildingServiceProvider {
        buildingServiceProviderDao.insertBuildingServiceProvider(link.toEntity())
        return link
    }

    override suspend fun updateBuildingServiceProvider(link: BuildingServiceProvider) {
        buildingServiceProviderDao.updateBuildingServiceProvider(link.toEntity())
    }

    override suspend fun getBuildingServiceProviderById(id: String): BuildingServiceProvider? {
        return buildingServiceProviderDao.getBuildingServiceProviderById(id)?.toDomain()
    }

    override suspend fun getLinkByBuildingAndProvider(
        buildingId: String,
        serviceProviderId: String
    ): BuildingServiceProvider? {
        return buildingServiceProviderDao.getLinkByBuildingAndProvider(buildingId, serviceProviderId)?.toDomain()
    }

    override fun observeBuildingServiceProviders(buildingId: String): Flow<List<BuildingServiceProviderWithDetails>> {
        return buildingServiceProviderDao.observeBuildingServiceProviders(buildingId).map { links ->
            links.mapNotNull { linkEntity ->
                val providerEntity = serviceProviderDao.getServiceProviderById(linkEntity.serviceProviderId)
                providerEntity?.let {
                    BuildingServiceProviderWithDetails(
                        link = linkEntity.toDomain(),
                        provider = it.toDomain()
                    )
                }
            }
        }
    }

    override suspend fun getBuildingServiceProviders(buildingId: String): List<BuildingServiceProviderWithDetails> {
        val links = buildingServiceProviderDao.getBuildingServiceProviders(buildingId)
        return links.mapNotNull { linkEntity ->
            val providerEntity = serviceProviderDao.getServiceProviderById(linkEntity.serviceProviderId)
            providerEntity?.let {
                BuildingServiceProviderWithDetails(
                    link = linkEntity.toDomain(),
                    provider = it.toDomain()
                )
            }
        }
    }

    override suspend fun getProvidersByCategory(
        buildingId: String,
        category: ServiceCategory
    ): List<BuildingServiceProviderWithDetails> {
        val links = buildingServiceProviderDao.getProvidersByCategory(buildingId, category.name)
        return links.mapNotNull { linkEntity ->
            val providerEntity = serviceProviderDao.getServiceProviderById(linkEntity.serviceProviderId)
            providerEntity?.let {
                BuildingServiceProviderWithDetails(
                    link = linkEntity.toDomain(),
                    provider = it.toDomain()
                )
            }
        }
    }

    override suspend fun getPrimaryProviderForCategory(
        buildingId: String,
        category: ServiceCategory
    ): BuildingServiceProviderWithDetails? {
        val linkEntity = buildingServiceProviderDao.getPrimaryProviderForCategory(buildingId, category.name)
            ?: return null
        val providerEntity = serviceProviderDao.getServiceProviderById(linkEntity.serviceProviderId)
            ?: return null
        return BuildingServiceProviderWithDetails(
            link = linkEntity.toDomain(),
            provider = providerEntity.toDomain()
        )
    }
}
