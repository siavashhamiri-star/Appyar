package com.apyar.app.test

import com.apyar.app.domain.model.Invoice
import com.apyar.app.domain.model.InvoiceStatus
import com.apyar.app.domain.model.PaymentRecord
import com.apyar.app.domain.repository.ServiceInvoiceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class FakeServiceInvoiceRepository : ServiceInvoiceRepository {
    private val invoices = MutableStateFlow<Map<String, Invoice>>(emptyMap())
    private val payments = MutableStateFlow<Map<String, PaymentRecord>>(emptyMap())

    override suspend fun createInvoice(invoice: Invoice): Result<Invoice> {
        invoices.value = invoices.value + (invoice.id to invoice)
        return Result.success(invoice)
    }

    override suspend fun getInvoiceById(invoiceId: String): Invoice? {
        return invoices.value[invoiceId]
    }

    override fun getBuildingInvoices(buildingId: String): Flow<List<Invoice>> {
        return invoices.asStateFlow().map { map -> map.values.filter { it.buildingId == buildingId } }
    }

    override fun getInvoicesByStatus(buildingId: String, status: InvoiceStatus): Flow<List<Invoice>> {
        return invoices.asStateFlow().map { map -> map.values.filter { it.buildingId == buildingId && it.status == status } }
    }

    override suspend fun updateInvoiceStatus(invoiceId: String, status: InvoiceStatus, paidAmount: Long): Result<Unit> {
        val existing = invoices.value[invoiceId] ?: return Result.failure(IllegalArgumentException("Invoice not found"))
        val updated = existing.copy(status = status, paidAmount = paidAmount)
        invoices.value = invoices.value + (invoiceId to updated)
        return Result.success(Unit)
    }

    override suspend fun recordPayment(payment: PaymentRecord): Result<PaymentRecord> {
        payments.value = payments.value + (payment.id to payment)
        val invoice = invoices.value[payment.invoiceId]
        if (invoice != null) {
            val newPaid = invoice.paidAmount + payment.amount
            val newStatus = when {
                newPaid >= invoice.totalAmount -> InvoiceStatus.PAID
                newPaid > 0 -> InvoiceStatus.PARTIALLY_PAID
                else -> InvoiceStatus.UNPAID
            }
            invoices.value = invoices.value + (invoice.id to invoice.copy(paidAmount = newPaid, status = newStatus))
        }
        return Result.success(payment)
    }

    override fun getInvoicePayments(invoiceId: String): Flow<List<PaymentRecord>> {
        return payments.asStateFlow().map { map -> map.values.filter { it.invoiceId == invoiceId } }
    }

    override fun getBuildingPayments(buildingId: String): Flow<List<PaymentRecord>> {
        return payments.asStateFlow().map { map ->
            val invoiceIds = invoices.value.values.filter { it.buildingId == buildingId }.map { it.id }.toSet()
            map.values.filter { it.invoiceId in invoiceIds }
        }
    }
}
