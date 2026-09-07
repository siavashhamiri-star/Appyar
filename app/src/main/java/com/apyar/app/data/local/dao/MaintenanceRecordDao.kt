package com.apyar.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.apyar.app.data.local.entity.MaintenanceRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MaintenanceRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaintenanceRecord(record: MaintenanceRecordEntity)

    @Update
    suspend fun updateMaintenanceRecord(record: MaintenanceRecordEntity)

    @Query("SELECT * FROM maintenance_records WHERE id = :id LIMIT 1")
    suspend fun getMaintenanceRecordById(id: String): MaintenanceRecordEntity?

    @Query("SELECT * FROM maintenance_records WHERE buildingId = :buildingId ORDER BY performedAt DESC")
    fun observeMaintenanceRecordsByBuildingId(buildingId: String): Flow<List<MaintenanceRecordEntity>>

    @Query("SELECT * FROM maintenance_records WHERE buildingId = :buildingId ORDER BY performedAt DESC")
    suspend fun getMaintenanceRecordsByBuildingId(buildingId: String): List<MaintenanceRecordEntity>

    @Query("SELECT * FROM maintenance_records WHERE buildingId = :buildingId AND nextServiceDate IS NOT NULL AND nextServiceDate >= :fromTimestamp ORDER BY nextServiceDate ASC")
    suspend fun getUpcomingMaintenance(buildingId: String, fromTimestamp: Long): List<MaintenanceRecordEntity>

    @Query("SELECT * FROM maintenance_records WHERE buildingId = :buildingId AND equipmentType = :equipmentType ORDER BY performedAt DESC")
    suspend fun getMaintenanceHistoryByEquipment(buildingId: String, equipmentType: String): List<MaintenanceRecordEntity>
}
