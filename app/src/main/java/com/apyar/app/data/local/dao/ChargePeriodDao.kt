package com.apyar.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.apyar.app.data.local.entity.ChargePeriodEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChargePeriodDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChargePeriod(period: ChargePeriodEntity)

    @Update
    suspend fun updateChargePeriod(period: ChargePeriodEntity)

    @Query("SELECT * FROM charge_periods WHERE id = :id LIMIT 1")
    suspend fun getChargePeriodById(id: String): ChargePeriodEntity?

    @Query("SELECT * FROM charge_periods WHERE buildingId = :buildingId ORDER BY createdAt DESC")
    suspend fun getChargePeriodsByBuildingId(buildingId: String): List<ChargePeriodEntity>

    @Query("SELECT * FROM charge_periods WHERE buildingId = :buildingId ORDER BY createdAt DESC")
    fun observeChargePeriodsByBuildingId(buildingId: String): Flow<List<ChargePeriodEntity>>

    @Query("UPDATE charge_periods SET status = :status, finalizedAt = :finalizedAt WHERE id = :id")
    suspend fun updateStatus(id: String, status: String, finalizedAt: Long?)
}
