package com.apyar.app.data.repository

import com.apyar.app.data.local.dao.InvoiceDao
import com.apyar.app.data.local.dao.PaymentRecordDao
import com.apyar.app.data.local.dao.ServiceProviderDao
import com.apyar.app.data.local.dao.ServiceRecordDao
import com.apyar.app.data.mapper.toDomain
import com.apyar.app.data.mapper.toEntity
import com.apyar.app.domain.model.Invoice
import com.apyar.app.domain.model.InvoicePaymentStatus
import com.apyar.app.domain.model.InvoiceWithDetails
import com.apyar.app.domain.model.PaymentRecord
import com.apyar.app.domain.repository.ServiceInvoiceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ServiceInvoiceRepositoryImpl(
    private val invoiceDao: InvoiceDao,
    private val paymentRecordDao: PaymentRecordDao,
    private val serviceProviderDao: ServiceProviderDao,
    private val serviceRecordDao: ServiceRecordDao
) : ServiceInvoiceRepository {

    override suspend fun createInvoice(invoice: Invoice): Invoice {
        invoiceDao.insertInvoice(invoice.toEntity())
        return invoice
    }

    override suspend fun updateInvoice(invoice: Invoice) {
        invoiceDao.updateInvoice(invoice.toEntity())
    }

    override suspend fun getInvoiceById(id: String): Invoice? {
        return invoiceDao.getInvoiceById(id)?.toDomain()
    }

    override suspend fun getInvoiceWithDetailsById(id: String): InvoiceWithDetails? {
        val invoiceEntity = invoiceDao.getInvoiceById(id) ?: return null
        val invoice = invoiceEntity.toDomain()
        val provider = invoice.serviceProviderId?.let { serviceProviderDao.getServiceProviderById(it)?.toDomain() }
        val serviceRecord = invoice.serviceRecordId?.let { serviceRecordDao.getServiceRecordById(it)?.toDomain() }
        val payments = paymentRecordDao.getPaymentsByInvoiceId(id).map { it.toDomain() }
        val totalPaid = payments.sumOf { it.amount }
        val remaining = (invoice.amount - totalPaid).coerceAtLeast(0.0)

        return InvoiceWithDetails(
            invoice = invoice,
            provider = provider,
            serviceRecord = serviceRecord,
            payments = payments,
            totalPaid = totalPaid,
            remainingAmount = remaining
        )
    }

    override fun observeInvoicesByBuilding(buildingId: String): Flow<List<InvoiceWithDetails>> {
        return invoiceDao.observeInvoicesByBuildingId(buildingId).map { list ->
            list.map { invoiceEntity ->
                val invoice = invoiceEntity.toDomain()
                val provider = invoice.serviceProviderId?.let { serviceProviderDao.getServiceProviderById(it)?.toDomain() }
                val serviceRecord = invoice.serviceRecordId?.let { serviceRecordDao.getServiceRecordById(it)?.toDomain() }
                val payments = paymentRecordDao.getPaymentsByInvoiceId(invoice.id).map { it.toDomain() }
                val totalPaid = payments.sumOf { it.amount }
                val remaining = (invoice.amount - totalPaid).coerceAtLeast(0.0)

                InvoiceWithDetails(
                    invoice = invoice,
                    provider = provider,
                    serviceRecord = serviceRecord,
                    payments = payments,
                    totalPaid = totalPaid,
                    remainingAmount = remaining
                )
            }
        }
    }

    override suspend fun getInvoicesByBuilding(buildingId: String): List<InvoiceWithDetails> {
        val list = invoiceDao.getInvoicesByBuildingId(buildingId)
        return list.map { invoiceEntity ->
            val invoice = invoiceEntity.toDomain()
            val provider = invoice.serviceProviderId?.let { serviceProviderDao.getServiceProviderById(it)?.toDomain() }
            val serviceRecord = invoice.serviceRecordId?.let { serviceRecordDao.getServiceRecordById(it)?.toDomain() }
            val payments = paymentRecordDao.getPaymentsByInvoiceId(invoice.id).map { it.toDomain() }
            val totalPaid = payments.sumOf { it.amount }
            val remaining = (invoice.amount - totalPaid).coerceAtLeast(0.0)

            InvoiceWithDetails(
                invoice = invoice,
                provider = provider,
                serviceRecord = serviceRecord,
                payments = payments,
                totalPaid = totalPaid,
                remainingAmount = remaining
            )
        }
    }

    override suspend fun getInvoicesByProvider(
        buildingId: String,
        providerId: String
    ): List<InvoiceWithDetails> {
        val list = invoiceDao.getInvoicesByProviderId(buildingId, providerId)
        val provider = serviceProviderDao.getServiceProviderById(providerId)?.toDomain()
        return list.map { invoiceEntity ->
            val invoice = invoiceEntity.toDomain()
            val serviceRecord = invoice.serviceRecordId?.let { serviceRecordDao.getServiceRecordById(it)?.toDomain() }
            val payments = paymentRecordDao.getPaymentsByInvoiceId(invoice.id).map { it.toDomain() }
            val totalPaid = payments.sumOf { it.amount }
            val remaining = (invoice.amount - totalPaid).coerceAtLeast(0.0)

            InvoiceWithDetails(
                invoice = invoice,
                provider = provider,
                serviceRecord = serviceRecord,
                payments = payments,
                totalPaid = totalPaid,
                remainingAmount = remaining
            )
        }
    }

    override suspend fun getInvoicesByPaymentStatus(
        buildingId: String,
        status: InvoicePaymentStatus
    ): List<InvoiceWithDetails> {
        val list = invoiceDao.getInvoicesByPaymentStatus(buildingId, status.name)
        return list.map { invoiceEntity ->
            val invoice = invoiceEntity.toDomain()
            val provider = invoice.serviceProviderId?.let { serviceProviderDao.getServiceProviderById(it)?.toDomain() }
            val serviceRecord = invoice.serviceRecordId?.let { serviceRecordDao.getServiceRecordById(it)?.toDomain() }
            val payments = paymentRecordDao.getPaymentsByInvoiceId(invoice.id).map { it.toDomain() }
            val totalPaid = payments.sumOf { it.amount }
            val remaining = (invoice.amount - totalPaid).coerceAtLeast(0.0)

            InvoiceWithDetails(
                invoice = invoice,
                provider = provider,
                serviceRecord = serviceRecord,
                payments = payments,
                totalPaid = totalPaid,
                remainingAmount = remaining
            )
        }
    }

    override suspend fun recordPayment(payment: PaymentRecord): PaymentRecord {
        paymentRecordDao.insertPaymentRecord(payment.toEntity())
        return payment
    }

    override suspend fun updatePayment(payment: PaymentRecord) {
        paymentRecordDao.updatePaymentRecord(payment.toEntity())
    }

    override suspend fun getPaymentById(id: String): PaymentRecord? {
        return paymentRecordDao.getPaymentRecordById(id)?.toDomain()
    }

    override fun observePaymentsByBuilding(buildingId: String): Flow<List<PaymentRecord>> {
        return paymentRecordDao.observePaymentRecordsByBuildingId(buildingId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getPaymentsByBuilding(buildingId: String): List<PaymentRecord> {
        return paymentRecordDao.getPaymentRecordsByBuildingId(buildingId).map { it.toDomain() }
    }

    override suspend fun getPaymentsByInvoice(invoiceId: String): List<PaymentRecord> {
        return paymentRecordDao.getPaymentsByInvoiceId(invoiceId).map { it.toDomain() }
    }
}
