package com.apyar.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.apyar.app.data.local.entity.UnitChargeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UnitChargeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUnitCharges(charges: List<UnitChargeEntity>)

    @Update
    suspend fun updateUnitCharge(charge: UnitChargeEntity)

    @Query("DELETE FROM unit_charges WHERE chargePeriodId = :periodId")
    suspend fun deleteUnitChargesByPeriodId(periodId: String)

    @Query("SELECT * FROM unit_charges WHERE chargePeriodId = :periodId")
    suspend fun getUnitChargesByPeriodId(periodId: String): List<UnitChargeEntity>

    @Query("SELECT * FROM unit_charges WHERE chargePeriodId = :periodId")
    fun observeUnitChargesByPeriodId(periodId: String): Flow<List<UnitChargeEntity>>

    @Query("SELECT * FROM unit_charges WHERE id = :id LIMIT 1")
    suspend fun getUnitChargeById(id: String): UnitChargeEntity?

    @Query("SELECT * FROM unit_charges WHERE unitId = :unitId")
    suspend fun getUnitChargesByUnitId(unitId: String): List<UnitChargeEntity>

    @Query("UPDATE unit_charges SET paidAmount = :paidAmount, remainingAmount = (finalAmount - :paidAmount), status = :status WHERE id = :id")
    suspend fun updatePayment(id: String, paidAmount: Long, status: String)

    @Query("UPDATE unit_charges SET adjustmentAmount = :adjustmentAmount, finalAmount = :finalAmount, remainingAmount = :remainingAmount, calculationNotes = :notes WHERE id = :id")
    suspend fun updateAdjustment(id: String, adjustmentAmount: Long, finalAmount: Long, remainingAmount: Long, notes: String?)
}
