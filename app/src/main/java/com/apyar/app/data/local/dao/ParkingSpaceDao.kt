package com.apyar.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.apyar.app.data.local.entity.ParkingSpaceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ParkingSpaceDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertParkingSpace(space: ParkingSpaceEntity)

    @Update
    suspend fun updateParkingSpace(space: ParkingSpaceEntity)

    @Query("SELECT * FROM parking_spaces WHERE id = :id LIMIT 1")
    suspend fun getParkingSpaceById(id: String): ParkingSpaceEntity?

    @Query("SELECT * FROM parking_spaces WHERE buildingId = :buildingId AND code = :code LIMIT 1")
    suspend fun getParkingSpaceByCode(buildingId: String, code: String): ParkingSpaceEntity?

    @Query("SELECT * FROM parking_spaces WHERE buildingId = :buildingId ORDER BY floor ASC, code ASC")
    fun observeParkingSpacesByBuildingId(buildingId: String): Flow<List<ParkingSpaceEntity>>

    @Query("SELECT * FROM parking_spaces WHERE buildingId = :buildingId ORDER BY floor ASC, code ASC")
    suspend fun getParkingSpacesByBuildingId(buildingId: String): List<ParkingSpaceEntity>
}
