package com.apyar.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.apyar.app.data.local.entity.CustomFormulaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomFormulaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomFormula(formula: CustomFormulaEntity)

    @Query("SELECT * FROM custom_formulas WHERE id = :id LIMIT 1")
    suspend fun getCustomFormulaById(id: String): CustomFormulaEntity?

    @Query("SELECT * FROM custom_formulas WHERE buildingId = :buildingId ORDER BY version DESC, approvedAt DESC")
    suspend fun getCustomFormulasByBuildingId(buildingId: String): List<CustomFormulaEntity>

    @Query("SELECT * FROM custom_formulas WHERE buildingId = :buildingId ORDER BY version DESC, approvedAt DESC")
    fun observeCustomFormulasByBuildingId(buildingId: String): Flow<List<CustomFormulaEntity>>
}
