package com.apyar.app.domain.model

/**
 * Lifecycle status of an individual unit's calculated charge item.
 */
enum class ChargeItemStatus(val titleFa: String) {
    CALCULATED("محاسبه‌شده"),
    ISSUED("صادرشده"),
    PAID("پرداخت‌شده"),
    PARTIALLY_PAID("پرداخت ناقص"),
    SETTLED("تسویه‌شده"),
    CANCELLED("لغوشده")
}

/**
 * Domain model representing a calculated charge item for an individual unit in a period.
 * Forms an immutable historical snapshot at calculation time.
 */
data class ChargeItem(
    val id: String,
    val chargePeriodId: String,
    val buildingId: String,
    val unitId: String,
    val calculationMethodId: String,
    val baseAmount: Long, // Base calculated amount in smallest currency unit
    val adjustments: Long = 0L, // Any manual/policy adjustments
    val finalAmount: Long = baseAmount + adjustments,
    val calculatedAt: Long = System.currentTimeMillis(),
    val status: ChargeItemStatus = ChargeItemStatus.CALCULATED
)
