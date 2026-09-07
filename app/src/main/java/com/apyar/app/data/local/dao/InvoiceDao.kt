package com.apyar.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.apyar.app.data.local.entity.InvoiceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InvoiceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoice(invoice: InvoiceEntity)

    @Update
    suspend fun updateInvoice(invoice: InvoiceEntity)

    @Query("SELECT * FROM invoices WHERE id = :id LIMIT 1")
    suspend fun getInvoiceById(id: String): InvoiceEntity?

    @Query("SELECT * FROM invoices WHERE buildingId = :buildingId ORDER BY issueDate DESC")
    fun observeInvoicesByBuildingId(buildingId: String): Flow<List<InvoiceEntity>>

    @Query("SELECT * FROM invoices WHERE buildingId = :buildingId ORDER BY issueDate DESC")
    suspend fun getInvoicesByBuildingId(buildingId: String): List<InvoiceEntity>

    @Query("SELECT * FROM invoices WHERE buildingId = :buildingId AND serviceProviderId = :providerId ORDER BY issueDate DESC")
    suspend fun getInvoicesByProviderId(buildingId: String, providerId: String): List<InvoiceEntity>

    @Query("SELECT * FROM invoices WHERE buildingId = :buildingId AND paymentStatus = :status ORDER BY issueDate DESC")
    suspend fun getInvoicesByPaymentStatus(buildingId: String, status: String): List<InvoiceEntity>
}
