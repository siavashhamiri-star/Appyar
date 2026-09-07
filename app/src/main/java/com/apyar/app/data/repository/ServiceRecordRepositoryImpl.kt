package com.apyar.app.data.repository

import com.apyar.app.data.local.dao.BuildingServiceProviderDao
import com.apyar.app.data.local.dao.ServiceProviderDao
import com.apyar.app.data.local.dao.ServiceRecordDao
import com.apyar.app.data.mapper.toDomain
import com.apyar.app.data.mapper.toEntity
import com.apyar.app.domain.model.ServiceRecord
import com.apyar.app.domain.model.ServiceRecordStatus
import com.apyar.app.domain.model.ServiceRecordWithDetails
import com.apyar.app.domain.repository.ServiceRecordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ServiceRecordRepositoryImpl(
    private val serviceRecordDao: ServiceRecordDao,
    private val serviceProviderDao: ServiceProviderDao
) : ServiceRecordRepository {

    override suspend fun createServiceRecord(record: ServiceRecord): ServiceRecord {
        serviceRecordDao.insertServiceRecord(record.toEntity())
        return record
    }

    override suspend fun updateServiceRecord(record: ServiceRecord) {
        serviceRecordDao.updateServiceRecord(record.toEntity())
    }

    override suspend fun getServiceRecordById(id: String): ServiceRecord? {
        return serviceRecordDao.getServiceRecordById(id)?.toDomain()
    }

    override suspend fun getServiceRecordWithDetailsById(id: String): ServiceRecordWithDetails? {
        val entity = serviceRecordDao.getServiceRecordById(id) ?: return null
        val provider = serviceProviderDao.getServiceProviderById(entity.serviceProviderId)?.toDomain()
        return ServiceRecordWithDetails(
            record = entity.toDomain(),
            provider = provider
        )
    }

    override fun observeServiceRecordsByBuilding(buildingId: String): Flow<List<ServiceRecordWithDetails>> {
        return serviceRecordDao.observeServiceRecordsByBuildingId(buildingId).map { list ->
            list.map { recordEntity ->
                val provider = serviceProviderDao.getServiceProviderById(recordEntity.serviceProviderId)?.toDomain()
                ServiceRecordWithDetails(
                    record = recordEntity.toDomain(),
                    provider = provider
                )
            }
        }
    }

    override suspend fun getServiceRecordsByBuilding(buildingId: String): List<ServiceRecordWithDetails> {
        val list = serviceRecordDao.getServiceRecordsByBuildingId(buildingId)
        return list.map { recordEntity ->
            val provider = serviceProviderDao.getServiceProviderById(recordEntity.serviceProviderId)?.toDomain()
            ServiceRecordWithDetails(
                record = recordEntity.toDomain(),
                provider = provider
            )
        }
    }

    override suspend fun getServiceRecordsByProvider(
        buildingId: String,
        providerId: String
    ): List<ServiceRecordWithDetails> {
        val list = serviceRecordDao.getServiceRecordsByProviderId(buildingId, providerId)
        val provider = serviceProviderDao.getServiceProviderById(providerId)?.toDomain()
        return list.map {
            ServiceRecordWithDetails(
                record = it.toDomain(),
                provider = provider
            )
        }
    }

    override suspend fun getServiceRecordsByStatus(
        buildingId: String,
        status: ServiceRecordStatus
    ): List<ServiceRecordWithDetails> {
        val list = serviceRecordDao.getServiceRecordsByStatus(buildingId, status.name)
        return list.map { recordEntity ->
            val provider = serviceProviderDao.getServiceProviderById(recordEntity.serviceProviderId)?.toDomain()
            ServiceRecordWithDetails(
                record = recordEntity.toDomain(),
                provider = provider
            )
        }
    }

    override suspend fun getActiveServiceRecords(buildingId: String): List<ServiceRecordWithDetails> {
        val list = serviceRecordDao.getActiveServiceRecords(buildingId)
        return list.map { recordEntity ->
            val provider = serviceProviderDao.getServiceProviderById(recordEntity.serviceProviderId)?.toDomain()
            ServiceRecordWithDetails(
                record = recordEntity.toDomain(),
                provider = provider
            )
        }
    }
}
