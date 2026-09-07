package com.apyar.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.apyar.app.data.local.entity.BuildingBillEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BuildingBillDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBuildingBill(bill: BuildingBillEntity)

    @Update
    suspend fun updateBuildingBill(bill: BuildingBillEntity)

    @Query("SELECT * FROM building_bills WHERE id = :id LIMIT 1")
    suspend fun getBuildingBillById(id: String): BuildingBillEntity?

    @Query("SELECT * FROM building_bills WHERE buildingId = :buildingId ORDER BY issueDate DESC, dueDate DESC")
    fun observeBuildingBillsByBuildingId(buildingId: String): Flow<List<BuildingBillEntity>>

    @Query("SELECT * FROM building_bills WHERE buildingId = :buildingId ORDER BY issueDate DESC, dueDate DESC")
    suspend fun getBuildingBillsByBuildingId(buildingId: String): List<BuildingBillEntity>

    @Query("SELECT * FROM building_bills WHERE buildingId = :buildingId AND billType = :billType ORDER BY issueDate DESC")
    suspend fun getBillsByType(buildingId: String, billType: String): List<BuildingBillEntity>

    @Query("SELECT * FROM building_bills WHERE buildingId = :buildingId AND paymentStatus = :status ORDER BY dueDate ASC")
    suspend fun getBillsByPaymentStatus(buildingId: String, status: String): List<BuildingBillEntity>
}
