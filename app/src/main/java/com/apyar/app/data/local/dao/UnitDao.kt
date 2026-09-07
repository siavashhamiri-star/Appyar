package com.apyar.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.apyar.app.data.local.entity.UnitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UnitDao {
    @Query("SELECT * FROM units WHERE buildingId = :buildingId AND (isActive = :onlyActive OR :onlyActive = 0) ORDER BY floor ASC, unitNumber ASC")
    fun getUnitsByBuilding(buildingId: String, onlyActive: Boolean = true): Flow<List<UnitEntity>>

    @Query("SELECT * FROM units WHERE id = :id LIMIT 1")
    suspend fun getUnitById(id: String): UnitEntity?

    @Query("SELECT * FROM units WHERE buildingId = :buildingId AND unitNumber = :unitNumber LIMIT 1")
    suspend fun getUnitByBuildingAndNumber(buildingId: String, unitNumber: String): UnitEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUnit(unit: UnitEntity): Long

    @Update
    suspend fun updateUnit(unit: UnitEntity)

    @Query("UPDATE units SET isActive = :isActive, updatedAt = :updatedAt WHERE id = :id")
    suspend fun setUnitActive(id: String, isActive: Boolean, updatedAt: Long = System.currentTimeMillis())
}
