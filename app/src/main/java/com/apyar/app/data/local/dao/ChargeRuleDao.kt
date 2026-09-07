package com.apyar.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.apyar.app.data.local.entity.ChargeRuleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChargeRuleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChargeRule(rule: ChargeRuleEntity)

    @Update
    suspend fun updateChargeRule(rule: ChargeRuleEntity)

    @Query("SELECT * FROM charge_rules WHERE id = :id LIMIT 1")
    suspend fun getChargeRuleById(id: String): ChargeRuleEntity?

    @Query("SELECT * FROM charge_rules WHERE buildingId = :buildingId ORDER BY version DESC, createdAt DESC")
    suspend fun getChargeRulesByBuildingId(buildingId: String): List<ChargeRuleEntity>

    @Query("SELECT * FROM charge_rules WHERE buildingId = :buildingId ORDER BY version DESC, createdAt DESC")
    fun observeChargeRulesByBuildingId(buildingId: String): Flow<List<ChargeRuleEntity>>

    @Query("SELECT * FROM charge_rules WHERE buildingId = :buildingId AND isActive = 1 ORDER BY version DESC LIMIT 1")
    suspend fun getActiveRuleForBuilding(buildingId: String): ChargeRuleEntity?
}
