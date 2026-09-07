package com.apyar.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.apyar.app.data.local.entity.FinancialAccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FinancialAccountDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: FinancialAccountEntity)

    @Query("SELECT * FROM financial_accounts WHERE id = :id LIMIT 1")
    suspend fun getAccountById(id: String): FinancialAccountEntity?

    @Query("SELECT * FROM financial_accounts WHERE buildingId = :buildingId LIMIT 1")
    suspend fun getAccountByBuildingId(buildingId: String): FinancialAccountEntity?

    @Query("SELECT * FROM financial_accounts WHERE buildingId = :buildingId LIMIT 1")
    fun observeAccountByBuildingId(buildingId: String): Flow<FinancialAccountEntity?>

    @Query("UPDATE financial_accounts SET balance = :balance, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateBalance(id: String, balance: Long, updatedAt: Long = System.currentTimeMillis())
}
