package com.apyar.app.domain.model

/**
 * Payment status of an individual unit's charge.
 */
enum class ChargeStatus(val titleFa: String) {
    UNPAID("پرداخت‌نشده"),
    PARTIALLY_PAID("پرداخت ناقص"),
    PAID("تسویه‌شده"),
    OVERDUE("دارای سررسید گذشته")
}
