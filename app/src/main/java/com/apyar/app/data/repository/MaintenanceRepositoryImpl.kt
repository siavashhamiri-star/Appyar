package com.apyar.app.data.repository

import com.apyar.app.data.local.dao.MaintenanceRecordDao
import com.apyar.app.data.mapper.toDomain
import com.apyar.app.data.mapper.toEntity
import com.apyar.app.domain.model.MaintenanceRecord
import com.apyar.app.domain.repository.MaintenanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MaintenanceRepositoryImpl(
    private val maintenanceRecordDao: MaintenanceRecordDao
) : MaintenanceRepository {

    override suspend fun createMaintenanceRecord(record: MaintenanceRecord): MaintenanceRecord {
        maintenanceRecordDao.insertMaintenanceRecord(record.toEntity())
        return record
    }

    override suspend fun updateMaintenanceRecord(record: MaintenanceRecord) {
        maintenanceRecordDao.updateMaintenanceRecord(record.toEntity())
    }

    override suspend fun getMaintenanceRecordById(id: String): MaintenanceRecord? {
        return maintenanceRecordDao.getMaintenanceRecordById(id)?.toDomain()
    }

    override fun observeMaintenanceRecordsByBuilding(buildingId: String): Flow<List<MaintenanceRecord>> {
        return maintenanceRecordDao.observeMaintenanceRecordsByBuildingId(buildingId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getMaintenanceRecordsByBuilding(buildingId: String): List<MaintenanceRecord> {
        return maintenanceRecordDao.getMaintenanceRecordsByBuildingId(buildingId).map { it.toDomain() }
    }

    override suspend fun getUpcomingMaintenance(
        buildingId: String,
        fromTimestamp: Long
    ): List<MaintenanceRecord> {
        return maintenanceRecordDao.getUpcomingMaintenance(buildingId, fromTimestamp).map { it.toDomain() }
    }

    override suspend fun getMaintenanceHistoryByEquipment(
        buildingId: String,
        equipmentType: String
    ): List<MaintenanceRecord> {
        return maintenanceRecordDao.getMaintenanceHistoryByEquipment(buildingId, equipmentType).map { it.toDomain() }
    }
}
