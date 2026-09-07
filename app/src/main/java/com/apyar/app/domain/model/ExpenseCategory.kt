package com.apyar.app.domain.model

/**
 * Building expense categories.
 * Apyar does not enforce legal liability for expenses between owner and tenant.
 */
enum class ExpenseCategory(val titleFa: String) {
    ROUTINE("جاری و مصرفی"),
    MAINTENANCE("نگهداری و سرویس دوره‌ای"),
    UTILITIES("قبوض مشترک (آب، برق، گاز)"),
    CLEANING("نظافت و بهداشت"),
    SECURITY("نگهبانی و حراست"),
    REPAIR("تعمیرات و نوسازی"),
    CONSTRUCTION("عمرانی و سرمایه‌ای"),
    OTHER("سایر هزینه‌ها")
}
