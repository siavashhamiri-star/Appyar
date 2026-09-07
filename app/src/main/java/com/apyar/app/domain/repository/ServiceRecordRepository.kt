package com.apyar.app.domain.repository

import com.apyar.app.domain.model.ServiceRecord
import com.apyar.app.domain.model.ServiceRecordStatus
import com.apyar.app.domain.model.ServiceRecordWithDetails
import kotlinx.coroutines.flow.Flow

interface ServiceRecordRepository {
    suspend fun createServiceRecord(record: ServiceRecord): ServiceRecord
    suspend fun updateServiceRecord(record: ServiceRecord)
    suspend fun getServiceRecordById(id: String): ServiceRecord?
    suspend fun getServiceRecordWithDetailsById(id: String): ServiceRecordWithDetails?
    fun observeServiceRecordsByBuilding(buildingId: String): Flow<List<ServiceRecordWithDetails>>
    suspend fun getServiceRecordsByBuilding(buildingId: String): List<ServiceRecordWithDetails>
    suspend fun getServiceRecordsByProvider(buildingId: String, providerId: String): List<ServiceRecordWithDetails>
    suspend fun getServiceRecordsByStatus(buildingId: String, status: ServiceRecordStatus): List<ServiceRecordWithDetails>
    suspend fun getActiveServiceRecords(buildingId: String): List<ServiceRecordWithDetails>
}
