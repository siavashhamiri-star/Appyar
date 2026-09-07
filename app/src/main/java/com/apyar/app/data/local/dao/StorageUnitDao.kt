package com.apyar.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.apyar.app.data.local.entity.StorageUnitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StorageUnitDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertStorageUnit(unit: StorageUnitEntity)

    @Update
    suspend fun updateStorageUnit(unit: StorageUnitEntity)

    @Query("SELECT * FROM storage_units WHERE id = :id LIMIT 1")
    suspend fun getStorageUnitById(id: String): StorageUnitEntity?

    @Query("SELECT * FROM storage_units WHERE buildingId = :buildingId AND code = :code LIMIT 1")
    suspend fun getStorageUnitByCode(buildingId: String, code: String): StorageUnitEntity?

    @Query("SELECT * FROM storage_units WHERE buildingId = :buildingId ORDER BY code ASC")
    fun observeStorageUnitsByBuildingId(buildingId: String): Flow<List<StorageUnitEntity>>

    @Query("SELECT * FROM storage_units WHERE buildingId = :buildingId ORDER BY code ASC")
    suspend fun getStorageUnitsByBuildingId(buildingId: String): List<StorageUnitEntity>
}
