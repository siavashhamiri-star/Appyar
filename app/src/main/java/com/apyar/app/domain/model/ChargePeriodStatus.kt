package com.apyar.app.domain.model

/**
 * Lifecycle status of a ChargePeriod.
 * Prior to FINALIZED, recalculations are allowed.
 * After FINALIZED, records become immutable and can only be amended via ADJUSTMENT.
 */
enum class ChargePeriodStatus(val titleFa: String) {
    DRAFT("پیش‌نویس"),
    CALCULATED("محاسبه‌شده"),
    PUBLISHED("صادر و منتشر شده"),
    PARTIALLY_PAID("پرداخت ناقص"),
    SETTLED("تسویه‌شده"),
    CANCELLED("لغوشده"),
    FINALIZED("نهایی و قطعی‌شده")
}
