package com.apyar.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.apyar.app.data.local.entity.FinancialTransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FinancialTransactionDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertTransaction(transaction: FinancialTransactionEntity)

    @Query("SELECT * FROM financial_transactions WHERE id = :id LIMIT 1")
    suspend fun getTransactionById(id: String): FinancialTransactionEntity?

    @Query("SELECT * FROM financial_transactions WHERE buildingId = :buildingId ORDER BY createdAt DESC")
    suspend fun getTransactionsByBuildingId(buildingId: String): List<FinancialTransactionEntity>

    @Query("SELECT * FROM financial_transactions WHERE buildingId = :buildingId ORDER BY createdAt DESC")
    fun observeTransactionsByBuildingId(buildingId: String): Flow<List<FinancialTransactionEntity>>

    @Query("SELECT * FROM financial_transactions WHERE unitId = :unitId ORDER BY createdAt DESC")
    suspend fun getTransactionsByUnitId(unitId: String): List<FinancialTransactionEntity>

    @Query("SELECT * FROM financial_transactions WHERE buildingId = :buildingId AND type = :type ORDER BY createdAt DESC")
    suspend fun getTransactionsByType(buildingId: String, type: String): List<FinancialTransactionEntity>
}
