package com.apyar.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.apyar.app.data.local.entity.StorageAssignmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StorageAssignmentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignment(assignment: StorageAssignmentEntity)

    @Update
    suspend fun updateAssignment(assignment: StorageAssignmentEntity)

    @Query("SELECT * FROM storage_assignments WHERE id = :id LIMIT 1")
    suspend fun getAssignmentById(id: String): StorageAssignmentEntity?

    @Query("SELECT * FROM storage_assignments WHERE storageUnitId = :storageUnitId AND isActive = 1 LIMIT 1")
    suspend fun getActiveAssignmentByStorageId(storageUnitId: String): StorageAssignmentEntity?

    @Query("SELECT * FROM storage_assignments WHERE storageUnitId = :storageUnitId ORDER BY startDate DESC")
    fun observeAssignmentsByStorageId(storageUnitId: String): Flow<List<StorageAssignmentEntity>>

    @Query("SELECT * FROM storage_assignments WHERE storageUnitId = :storageUnitId ORDER BY startDate DESC")
    suspend fun getAssignmentsByStorageId(storageUnitId: String): List<StorageAssignmentEntity>

    @Query("SELECT * FROM storage_assignments WHERE unitId = :unitId ORDER BY startDate DESC")
    fun observeAssignmentsByUnitId(unitId: String): Flow<List<StorageAssignmentEntity>>

    @Query("UPDATE storage_assignments SET isActive = 0, endDate = :endDate, updatedAt = :updatedAt WHERE storageUnitId = :storageUnitId AND isActive = 1")
    suspend fun deactivateActiveAssignments(storageUnitId: String, endDate: Long, updatedAt: Long)
}
