package com.apyar.app.test

import com.apyar.app.domain.model.ServiceRecord
import com.apyar.app.domain.model.ServiceRecordStatus
import com.apyar.app.domain.repository.ServiceRecordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class FakeServiceRecordRepository : ServiceRecordRepository {
    private val records = MutableStateFlow<Map<String, ServiceRecord>>(emptyMap())

    override suspend fun createServiceRecord(record: ServiceRecord): Result<ServiceRecord> {
        records.value = records.value + (record.id to record)
        return Result.success(record)
    }

    override suspend fun getServiceRecordById(recordId: String): ServiceRecord? {
        return records.value[recordId]
    }

    override fun getBuildingServiceRecords(buildingId: String): Flow<List<ServiceRecord>> {
        return records.asStateFlow().map { map -> map.values.filter { it.buildingId == buildingId } }
    }

    override fun getServiceRecordsByStatus(buildingId: String, status: ServiceRecordStatus): Flow<List<ServiceRecord>> {
        return records.asStateFlow().map { map -> map.values.filter { it.buildingId == buildingId && it.status == status } }
    }

    override suspend fun updateServiceRecordStatus(
        recordId: String,
        status: ServiceRecordStatus,
        costEstimate: Long?,
        actualCost: Long?,
        resolutionNotes: String?,
        completedDate: Long?
    ): Result<Unit> {
        val existing = records.value[recordId] ?: return Result.failure(IllegalArgumentException("Record not found"))
        val updated = existing.copy(
            status = status,
            costEstimate = costEstimate ?: existing.costEstimate,
            actualCost = actualCost ?: existing.actualCost,
            resolutionNotes = resolutionNotes ?: existing.resolutionNotes,
            completedDate = completedDate ?: existing.completedDate,
            updatedAt = System.currentTimeMillis()
        )
        records.value = records.value + (recordId to updated)
        return Result.success(Unit)
    }

    override suspend fun getActiveServicesCount(buildingId: String): Int {
        return records.value.values.count {
            it.buildingId == buildingId && it.status in listOf(ServiceRecordStatus.REQUESTED, ServiceRecordStatus.SCHEDULED, ServiceRecordStatus.IN_PROGRESS)
        }
    }
}
