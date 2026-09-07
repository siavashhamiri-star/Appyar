package com.apyar.app.test

import com.apyar.app.domain.model.MaintenanceRecord
import com.apyar.app.domain.repository.MaintenanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class FakeMaintenanceRepository : MaintenanceRepository {
    private val records = MutableStateFlow<Map<String, MaintenanceRecord>>(emptyMap())

    override suspend fun createMaintenanceRecord(record: MaintenanceRecord): Result<MaintenanceRecord> {
        records.value = records.value + (record.id to record)
        return Result.success(record)
    }

    override suspend fun getMaintenanceRecordById(recordId: String): MaintenanceRecord? {
        return records.value[recordId]
    }

    override fun getBuildingMaintenanceRecords(buildingId: String): Flow<List<MaintenanceRecord>> {
        return records.asStateFlow().map { map -> map.values.filter { it.buildingId == buildingId } }
    }

    override fun getMaintenanceByEquipment(buildingId: String, equipmentName: String): Flow<List<MaintenanceRecord>> {
        return records.asStateFlow().map { map -> map.values.filter { it.buildingId == buildingId && it.equipmentName == equipmentName } }
    }

    override suspend fun getUpcomingMaintenances(buildingId: String, fromDate: Long): List<MaintenanceRecord> {
        return records.value.values.filter {
            it.buildingId == buildingId && it.nextScheduledDate != null && it.nextScheduledDate >= fromDate
        }
    }
}
