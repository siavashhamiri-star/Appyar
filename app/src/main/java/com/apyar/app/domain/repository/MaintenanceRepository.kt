package com.apyar.app.domain.repository

import com.apyar.app.domain.model.MaintenanceRecord
import kotlinx.coroutines.flow.Flow

interface MaintenanceRepository {
    suspend fun createMaintenanceRecord(record: MaintenanceRecord): MaintenanceRecord
    suspend fun updateMaintenanceRecord(record: MaintenanceRecord)
    suspend fun getMaintenanceRecordById(id: String): MaintenanceRecord?
    fun observeMaintenanceRecordsByBuilding(buildingId: String): Flow<List<MaintenanceRecord>>
    suspend fun getMaintenanceRecordsByBuilding(buildingId: String): List<MaintenanceRecord>
    suspend fun getUpcomingMaintenance(buildingId: String, fromTimestamp: Long): List<MaintenanceRecord>
    suspend fun getMaintenanceHistoryByEquipment(buildingId: String, equipmentType: String): List<MaintenanceRecord>
}
