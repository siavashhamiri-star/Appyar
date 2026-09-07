package com.apyar.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.apyar.app.data.local.entity.PaymentRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPaymentRecord(payment: PaymentRecordEntity)

    @Update
    suspend fun updatePaymentRecord(payment: PaymentRecordEntity)

    @Query("SELECT * FROM payment_records WHERE id = :id LIMIT 1")
    suspend fun getPaymentRecordById(id: String): PaymentRecordEntity?

    @Query("SELECT * FROM payment_records WHERE buildingId = :buildingId ORDER BY paidAt DESC")
    fun observePaymentRecordsByBuildingId(buildingId: String): Flow<List<PaymentRecordEntity>>

    @Query("SELECT * FROM payment_records WHERE buildingId = :buildingId ORDER BY paidAt DESC")
    suspend fun getPaymentRecordsByBuildingId(buildingId: String): List<PaymentRecordEntity>

    @Query("SELECT * FROM payment_records WHERE invoiceId = :invoiceId ORDER BY paidAt DESC")
    suspend fun getPaymentsByInvoiceId(invoiceId: String): List<PaymentRecordEntity>
}
