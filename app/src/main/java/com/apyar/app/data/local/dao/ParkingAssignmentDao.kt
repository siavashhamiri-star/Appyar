package com.apyar.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.apyar.app.data.local.entity.ParkingAssignmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ParkingAssignmentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignment(assignment: ParkingAssignmentEntity)

    @Update
    suspend fun updateAssignment(assignment: ParkingAssignmentEntity)

    @Query("SELECT * FROM parking_assignments WHERE id = :id LIMIT 1")
    suspend fun getAssignmentById(id: String): ParkingAssignmentEntity?

    @Query("SELECT * FROM parking_assignments WHERE parkingSpaceId = :parkingSpaceId AND isActive = 1 LIMIT 1")
    suspend fun getActiveAssignmentByParkingId(parkingSpaceId: String): ParkingAssignmentEntity?

    @Query("SELECT * FROM parking_assignments WHERE parkingSpaceId = :parkingSpaceId ORDER BY startDate DESC")
    fun observeAssignmentsByParkingId(parkingSpaceId: String): Flow<List<ParkingAssignmentEntity>>

    @Query("SELECT * FROM parking_assignments WHERE parkingSpaceId = :parkingSpaceId ORDER BY startDate DESC")
    suspend fun getAssignmentsByParkingId(parkingSpaceId: String): List<ParkingAssignmentEntity>

    @Query("SELECT * FROM parking_assignments WHERE unitId = :unitId ORDER BY startDate DESC")
    fun observeAssignmentsByUnitId(unitId: String): Flow<List<ParkingAssignmentEntity>>

    @Query("UPDATE parking_assignments SET isActive = 0, endDate = :endDate, updatedAt = :updatedAt WHERE parkingSpaceId = :parkingSpaceId AND isActive = 1")
    suspend fun deactivateActiveAssignments(parkingSpaceId: String, endDate: Long, updatedAt: Long)
}
