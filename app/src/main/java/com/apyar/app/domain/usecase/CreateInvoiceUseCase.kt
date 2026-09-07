package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.AuditEvent
import com.apyar.app.domain.model.Invoice
import com.apyar.app.domain.model.InvoicePaymentStatus
import com.apyar.app.domain.model.Permission
import com.apyar.app.domain.repository.AuditRepository
import com.apyar.app.domain.repository.BuildingRepository
import com.apyar.app.domain.repository.ServiceInvoiceRepository
import java.util.UUID

class CreateInvoiceUseCase(
    private val serviceInvoiceRepository: ServiceInvoiceRepository,
    private val buildingRepository: BuildingRepository,
    private val checkPermissionUseCase: CheckPermissionUseCase,
    private val auditRepository: AuditRepository
) {
    suspend operator fun invoke(
        userId: String,
        buildingId: String,
        amount: Double,
        description: String,
        serviceProviderId: String? = null,
        serviceRecordId: String? = null,
        invoiceNumber: String? = null,
        issueDate: Long = System.currentTimeMillis(),
        dueDate: Long? = null
    ): Result<Invoice> {
        checkPermissionUseCase.enforce(buildingId, userId, Permission.CREATE_INVOICE)

        val building = buildingRepository.getBuildingById(buildingId)
            ?: return Result.failure(IllegalArgumentException("ساختمان مورد نظر یافت نشد"))

        if (amount <= 0.0) {
            return Result.failure(IllegalArgumentException("مبلغ فاکتور باید بزرگتر از صفر باشد"))
        }

        val trimmedDesc = description.trim()
        if (trimmedDesc.isBlank()) {
            return Result.failure(IllegalArgumentException("شرح فاکتور نمی‌تواند خالی باشد"))
        }

        val invoice = Invoice(
            id = UUID.randomUUID().toString(),
            buildingId = buildingId,
            serviceProviderId = serviceProviderId,
            serviceRecordId = serviceRecordId,
            invoiceNumber = invoiceNumber?.trim(),
            issueDate = issueDate,
            dueDate = dueDate,
            amount = amount,
            description = trimmedDesc,
            paymentStatus = InvoicePaymentStatus.UNPAID,
            createdBy = userId
        )

        val created = serviceInvoiceRepository.createInvoice(invoice)

        auditRepository.recordEvent(
            AuditEvent(
                actor = userId,
                action = "CREATE_SERVICE_INVOICE",
                entity = "Invoice",
                entityId = created.id,
                buildingId = buildingId,
                details = "ثبت فاکتور خدماتی به مبلغ $amount تومان بابت '${created.description}'"
            )
        )

        return Result.success(created)
    }
}
