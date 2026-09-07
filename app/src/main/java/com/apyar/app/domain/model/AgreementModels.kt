package com.apyar.app.domain.model

import java.util.UUID

/**
 * Categorization of mutual agreements between building units.
 */
enum class AgreementType(val titleFa: String) {
    PARKING_USE("استفاده موقت از پارکینگ"),
    PARKING_RENTAL("اجاره پارکینگ بین واحدها"),
    STORAGE_USE("استفاده از انباری"),
    STORAGE_RENTAL("اجاره انباری بین واحدها"),
    SHARED_USE("بهره‌برداری اشتراکی"),
    OTHER("سایر توافقات")
}

/**
 * Payment and financial arrangement between units for an agreement.
 * Apyar does not decide legal validity; it only applies the agreed computation.
 */
enum class PaymentArrangement(val titleFa: String) {
    FIXED_AMOUNT("مبلغ ثابت یکجا"),
    MONTHLY_AMOUNT("مبلغ ماهانه"),
    CHARGE_OFFSET("کسر از شارژ (انتقال بخشی از سهم شارژ)"),
    FULL_CHARGE_OFFSET("پرداخت کامل شارژ واحد مبدأ توسط واحد مقصد"),
    NO_PAYMENT("توافق رایگان / بدون تبادل مالی"),
    CUSTOM("فرمول یا توافق سفارشی")
}

/**
 * Operational lifecycle status of an agreement.
 */
enum class AgreementStatus(val titleFa: String) {
    DRAFT("پیش‌نویس"),
    ACTIVE("فعال"),
    EXPIRED("منقضی‌شده"),
    CANCELLED("لغوشده")
}

/**
 * Document metadata classification for agreement evidence/attachments.
 */
enum class DocumentType(val titleFa: String) {
    CONTRACT("قرارداد رسمی / اجاره‌نامه"),
    AGREEMENT("صورت‌جلسه یا توافق‌نامه کتبی"),
    RECEIPT("رسید پرداخت مالی"),
    DECLARATION("اظهارنامه / تأییدیه مالک"),
    OTHER("سایر مدارک")
}

/**
 * Generic Agreement model covering parking, storage, and mutual unit arrangements.
 */
data class Agreement(
    val id: String = UUID.randomUUID().toString(),
    val buildingId: String,
    val agreementType: AgreementType,
    val sourceUnitId: String,
    val targetUnitId: String,
    val relatedParkingId: String? = null,
    val relatedStorageId: String? = null,
    val startDate: Long = System.currentTimeMillis(),
    val endDate: Long? = null,
    val financialArrangement: PaymentArrangement = PaymentArrangement.NO_PAYMENT,
    val amount: Long? = null, // In Rials/Tomans
    val description: String,
    val status: AgreementStatus = AgreementStatus.ACTIVE,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val createdBy: String
)

/**
 * Metadata record for supporting document attached to an agreement.
 */
data class AgreementDocument(
    val id: String = UUID.randomUUID().toString(),
    val agreementId: String,
    val documentType: DocumentType = DocumentType.AGREEMENT,
    val title: String,
    val description: String? = null,
    val fileReference: String,
    val createdAt: Long = System.currentTimeMillis(),
    val uploadedBy: String
)

/**
 * Rich model for presenting an agreement with unit details, related asset labels, and documents.
 */
data class AgreementWithDetails(
    val agreement: Agreement,
    val sourceUnitNumber: String,
    val targetUnitNumber: String,
    val relatedParkingCode: String? = null,
    val relatedStorageCode: String? = null,
    val documents: List<AgreementDocument> = emptyList()
)
