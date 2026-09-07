package com.apyar.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.apyar.app.data.local.entity.BuildingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BuildingDao {
    @Query("SELECT * FROM buildings WHERE isActive = :onlyActive OR :onlyActive = 0 ORDER BY createdAt DESC")
    fun getBuildings(onlyActive: Boolean = true): Flow<List<BuildingEntity>>

    @Query("SELECT * FROM buildings WHERE id = :id LIMIT 1")
    suspend fun getBuildingById(id: String): BuildingEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertBuilding(building: BuildingEntity): Long

    @Update
    suspend fun updateBuilding(building: BuildingEntity)

    @Query("UPDATE buildings SET isActive = :isActive, updatedAt = :updatedAt WHERE id = :id")
    suspend fun setBuildingActive(id: String, isActive: Boolean, updatedAt: Long = System.currentTimeMillis())
}
