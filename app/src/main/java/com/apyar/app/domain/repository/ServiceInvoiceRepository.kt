package com.apyar.app.domain.repository

import com.apyar.app.domain.model.Invoice
import com.apyar.app.domain.model.InvoicePaymentStatus
import com.apyar.app.domain.model.InvoiceWithDetails
import com.apyar.app.domain.model.PaymentRecord
import kotlinx.coroutines.flow.Flow

interface ServiceInvoiceRepository {
    suspend fun createInvoice(invoice: Invoice): Invoice
    suspend fun updateInvoice(invoice: Invoice)
    suspend fun getInvoiceById(id: String): Invoice?
    suspend fun getInvoiceWithDetailsById(id: String): InvoiceWithDetails?
    fun observeInvoicesByBuilding(buildingId: String): Flow<List<InvoiceWithDetails>>
    suspend fun getInvoicesByBuilding(buildingId: String): List<InvoiceWithDetails>
    suspend fun getInvoicesByProvider(buildingId: String, providerId: String): List<InvoiceWithDetails>
    suspend fun getInvoicesByPaymentStatus(buildingId: String, status: InvoicePaymentStatus): List<InvoiceWithDetails>

    suspend fun recordPayment(payment: PaymentRecord): PaymentRecord
    suspend fun updatePayment(payment: PaymentRecord)
    suspend fun getPaymentById(id: String): PaymentRecord?
    fun observePaymentsByBuilding(buildingId: String): Flow<List<PaymentRecord>>
    suspend fun getPaymentsByBuilding(buildingId: String): List<PaymentRecord>
    suspend fun getPaymentsByInvoice(invoiceId: String): List<PaymentRecord>
}
