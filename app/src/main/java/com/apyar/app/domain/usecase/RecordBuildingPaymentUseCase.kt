package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.AuditEvent
import com.apyar.app.domain.model.InvoicePaymentStatus
import com.apyar.app.domain.model.PaymentMethod
import com.apyar.app.domain.model.PaymentRecord
import com.apyar.app.domain.model.Permission
import com.apyar.app.domain.repository.AuditRepository
import com.apyar.app.domain.repository.BuildingRepository
import com.apyar.app.domain.repository.ServiceInvoiceRepository
import java.util.UUID

class RecordBuildingPaymentUseCase(
    private val serviceInvoiceRepository: ServiceInvoiceRepository,
    private val buildingRepository: BuildingRepository,
    private val checkPermissionUseCase: CheckPermissionUseCase,
    private val auditRepository: AuditRepository
) {
    suspend operator fun invoke(
        userId: String,
        buildingId: String,
        amount: Double,
        invoiceId: String? = null,
        paymentMethod: PaymentMethod = PaymentMethod.BANK_TRANSFER,
        referenceNumber: String? = null,
        notes: String? = null
    ): Result<PaymentRecord> {
        checkPermissionUseCase.enforce(buildingId, userId, Permission.CREATE_BUILDING_PAYMENT)

        val building = buildingRepository.getBuildingById(buildingId)
            ?: return Result.failure(IllegalArgumentException("ساختمان مورد نظر یافت نشد"))

        if (amount <= 0.0) {
            return Result.failure(IllegalArgumentException("مبلغ پرداختی باید بزرگتر از صفر باشد"))
        }

        val payment = PaymentRecord(
            id = UUID.randomUUID().toString(),
            buildingId = buildingId,
            invoiceId = invoiceId,
            amount = amount,
            paidAt = System.currentTimeMillis(),
            paymentMethod = paymentMethod,
            referenceNumber = referenceNumber?.trim(),
            recordedBy = userId,
            notes = notes?.trim()
        )

        val created = serviceInvoiceRepository.recordPayment(payment)

        // If invoice is linked, recalculate its payment status
        if (invoiceId != null) {
            val invoice = serviceInvoiceRepository.getInvoiceById(invoiceId)
            if (invoice != null && invoice.buildingId == buildingId) {
                val allPayments = serviceInvoiceRepository.getPaymentsByInvoice(invoiceId)
                val totalPaid = allPayments.sumOf { it.amount }

                val newStatus = when {
                    totalPaid >= invoice.amount -> InvoicePaymentStatus.PAID
                    totalPaid > 0.0 -> InvoicePaymentStatus.PARTIALLY_PAID
                    else -> InvoicePaymentStatus.UNPAID
                }

                if (newStatus != invoice.paymentStatus) {
                    serviceInvoiceRepository.updateInvoice(invoice.copy(paymentStatus = newStatus))
                }
            }
        }

        auditRepository.recordEvent(
            AuditEvent(
                actor = userId,
                action = "RECORD_BUILDING_PAYMENT",
                entity = "PaymentRecord",
                entityId = created.id,
                buildingId = buildingId,
                details = "ثبت پرداخت به مبلغ $amount تومان (${paymentMethod.titleFa}, کد پیگیری: ${referenceNumber ?: "ندارد"})"
            )
        )

        return Result.success(created)
    }
}
