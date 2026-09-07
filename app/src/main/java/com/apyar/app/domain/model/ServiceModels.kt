package com.apyar.app.domain.model

import java.util.UUID

/**
 * ServiceCategory defines standard building service categories.
 * Extensible for future categories.
 */
enum class ServiceCategory(val titleFa: String, val iconName: String) {
    CLEANING("نظافت و بهداشت", "cleaning_services"),
    PLUMBING("تأسیسات لوله‌کشی و فاضلاب", "plumbing"),
    ELECTRICAL("برق، روشنایی و آیفون", "electrical_services"),
    ELEVATOR("آسانسور و بالابر", "elevator"),
    HVAC("سرمایش، گرمایش و تهویه", "hvac"),
    PUMP("پمپ، بوستر و مخازن آب", "water_damage"),
    MOTOR_ROOM("موتورخانه و مشعل", "local_fire_department"),
    SECURITY("حفاظت، دوربین و دزدگیر", "security"),
    FIRE_SAFETY("اعلام و اطفای حریق", "fire_extinguisher"),
    INTERNET_NETWORK("اینترنت، شبکه و آنتن", "router"),
    GLASS_DOOR("درب اتوماتیک و کرکره برقی", "door_sliding"),
    GENERAL_REPAIR("تعمیرات عمومی و بنایی", "construction"),
    GARDENING("فضای سبز و باغبانی", "yard"),
    OTHER("سایر خدمات و امور عمومی", "more_horiz")
}

/**
 * Role of service provider in a specific building.
 */
enum class ProviderRole(val titleFa: String) {
    PRIMARY_PROVIDER("سرویس‌کار / پیمانکار اصلی"),
    BACKUP_PROVIDER("سرویس‌کار / پیمانکار جایگزین"),
    EMERGENCY_PROVIDER("سرویس‌کار اورژانسی و کشیک")
}

/**
 * Status of a requested service.
 */
enum class ServiceRecordStatus(val titleFa: String) {
    REQUESTED("درخواست‌شده"),
    SCHEDULED("برنامه‌ریزی‌شده"),
    IN_PROGRESS("در حال انجام"),
    COMPLETED("تکمیل‌شده"),
    CANCELLED("لغوشده")
}

/**
 * Status of an invoice.
 */
enum class InvoicePaymentStatus(val titleFa: String) {
    UNPAID("پرداخت‌نشده"),
    PARTIALLY_PAID("پرداخت ناقص"),
    PAID("تسویه‌شده"),
    CANCELLED("باطل‌شده / لغوشده")
}

/**
 * Payment method for building payments.
 */
enum class PaymentMethod(val titleFa: String) {
    CASH("نقدی"),
    BANK_TRANSFER("انتقال بانکی / پایا / ساتنا"),
    CARD("کارت به کارت / پوز"),
    OTHER("سایر روش‌ها")
}

/**
 * Utility and public bills of the building.
 */
enum class BillType(val titleFa: String) {
    WATER("آب"),
    ELECTRICITY("برق مشاعات"),
    GAS("گاز"),
    TELEPHONE("تلفن ثابت و ارتباطات"),
    INTERNET("اینترنت و شبکه ساختمان"),
    COMMON_AREA("عوارض شهرداری و پسماند"),
    OTHER("سایر قبوض")
}

/**
 * Payment status for public utility bills.
 */
enum class BillPaymentStatus(val titleFa: String) {
    UNPAID("پرداخت‌نشده"),
    PAID("پرداخت‌شده"),
    OVERDUE("سررسیدگذشته"),
    CANCELLED("لغوشده")
}

/**
 * ServiceProvider model (Person or Company).
 */
data class ServiceProvider(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val companyName: String? = null,
    val mobileNumber: String,
    val secondaryPhone: String? = null,
    val email: String? = null,
    val address: String? = null,
    val serviceCategory: ServiceCategory,
    val description: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
)

/**
 * Links a ServiceProvider to a Building with a specific role and category.
 */
data class BuildingServiceProvider(
    val id: String = UUID.randomUUID().toString(),
    val buildingId: String,
    val serviceProviderId: String,
    val serviceCategory: ServiceCategory,
    val role: ProviderRole = ProviderRole.PRIMARY_PROVIDER,
    val isPrimary: Boolean = true,
    val isActive: Boolean = true,
    val startDate: Long = System.currentTimeMillis(),
    val endDate: Long? = null,
    val notes: String? = null
)

/**
 * Combined model with ServiceProvider entity and building link details.
 */
data class BuildingServiceProviderWithDetails(
    val link: BuildingServiceProvider,
    val provider: ServiceProvider
)

/**
 * ServiceRecord represents an instance of requested or delivered service.
 */
data class ServiceRecord(
    val id: String = UUID.randomUUID().toString(),
    val buildingId: String,
    val serviceProviderId: String,
    val serviceCategory: ServiceCategory,
    val title: String,
    val description: String,
    val requestedAt: Long = System.currentTimeMillis(),
    val scheduledAt: Long? = null,
    val completedAt: Long? = null,
    val status: ServiceRecordStatus = ServiceRecordStatus.REQUESTED,
    val createdBy: String,
    val notes: String? = null
)

/**
 * ServiceRecord with Provider information for display.
 */
data class ServiceRecordWithDetails(
    val record: ServiceRecord,
    val provider: ServiceProvider?
)

/**
 * MaintenanceRecord tracks preventative and corrective equipment maintenance.
 */
data class MaintenanceRecord(
    val id: String = UUID.randomUUID().toString(),
    val buildingId: String,
    val serviceRecordId: String? = null,
    val title: String,
    val description: String,
    val equipmentType: String? = null,
    val location: String? = null,
    val performedAt: Long = System.currentTimeMillis(),
    val nextServiceDate: Long? = null,
    val performedBy: String,
    val cost: Double? = null,
    val notes: String? = null
)

/**
 * Invoice represents a service bill/invoice for building expenses.
 */
data class Invoice(
    val id: String = UUID.randomUUID().toString(),
    val buildingId: String,
    val serviceProviderId: String? = null,
    val serviceRecordId: String? = null,
    val invoiceNumber: String? = null,
    val issueDate: Long = System.currentTimeMillis(),
    val dueDate: Long? = null,
    val amount: Double,
    val description: String,
    val paymentStatus: InvoicePaymentStatus = InvoicePaymentStatus.UNPAID,
    val createdBy: String
)

/**
 * PaymentRecord logs payments made by the building toward invoices or services.
 */
data class PaymentRecord(
    val id: String = UUID.randomUUID().toString(),
    val buildingId: String,
    val invoiceId: String? = null,
    val amount: Double,
    val paidAt: Long = System.currentTimeMillis(),
    val paymentMethod: PaymentMethod = PaymentMethod.BANK_TRANSFER,
    val referenceNumber: String? = null,
    val recordedBy: String,
    val notes: String? = null
)

/**
 * Invoice with related payments and provider details.
 */
data class InvoiceWithDetails(
    val invoice: Invoice,
    val provider: ServiceProvider?,
    val serviceRecord: ServiceRecord?,
    val payments: List<PaymentRecord> = emptyList(),
    val totalPaid: Double = 0.0,
    val remainingAmount: Double = invoice.amount
)

/**
 * BuildingBill stores utility and common bills for archive and tracking.
 */
data class BuildingBill(
    val id: String = UUID.randomUUID().toString(),
    val buildingId: String,
    val billType: BillType,
    val providerName: String,
    val billingPeriod: String,
    val amount: Double,
    val issueDate: Long? = null,
    val dueDate: Long? = null,
    val paidAt: Long? = null,
    val paymentStatus: BillPaymentStatus = BillPaymentStatus.UNPAID,
    val referenceNumber: String? = null,
    val createdBy: String
)

/**
 * Summary for the Service & Operations Dashboard.
 */
data class ServiceDashboardSummary(
    val activeServicesCount: Int = 0,
    val pendingServicesCount: Int = 0,
    val upcomingMaintenanceCount: Int = 0,
    val unpaidInvoicesCount: Int = 0,
    val totalUnpaidInvoicesAmount: Double = 0.0,
    val overdueBillsCount: Int = 0,
    val totalOverdueBillsAmount: Double = 0.0,
    val totalActiveProvidersCount: Int = 0,
    val recentMaintenance: List<MaintenanceRecord> = emptyList(),
    val recentPayments: List<PaymentRecord> = emptyList()
)
