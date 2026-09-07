package com.apyar.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.apyar.app.data.local.entity.ServiceRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ServiceRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServiceRecord(record: ServiceRecordEntity)

    @Update
    suspend fun updateServiceRecord(record: ServiceRecordEntity)

    @Query("SELECT * FROM service_records WHERE id = :id LIMIT 1")
    suspend fun getServiceRecordById(id: String): ServiceRecordEntity?

    @Query("SELECT * FROM service_records WHERE buildingId = :buildingId ORDER BY requestedAt DESC")
    fun observeServiceRecordsByBuildingId(buildingId: String): Flow<List<ServiceRecordEntity>>

    @Query("SELECT * FROM service_records WHERE buildingId = :buildingId ORDER BY requestedAt DESC")
    suspend fun getServiceRecordsByBuildingId(buildingId: String): List<ServiceRecordEntity>

    @Query("SELECT * FROM service_records WHERE buildingId = :buildingId AND serviceProviderId = :providerId ORDER BY requestedAt DESC")
    suspend fun getServiceRecordsByProviderId(buildingId: String, providerId: String): List<ServiceRecordEntity>

    @Query("SELECT * FROM service_records WHERE buildingId = :buildingId AND status = :status ORDER BY requestedAt DESC")
    suspend fun getServiceRecordsByStatus(buildingId: String, status: String): List<ServiceRecordEntity>

    @Query("SELECT * FROM service_records WHERE buildingId = :buildingId AND status IN ('REQUESTED', 'SCHEDULED', 'IN_PROGRESS') ORDER BY requestedAt DESC")
    suspend fun getActiveServiceRecords(buildingId: String): List<ServiceRecordEntity>
}
