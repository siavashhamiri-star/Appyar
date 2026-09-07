package com.apyar.app.domain.model

/**
 * Domain model representing an expense incurred by the building.
 * Can be linked to a specific ChargePeriod or remain general until allocated.
 */
data class BuildingExpense(
    val id: String,
    val buildingId: String,
    val chargePeriodId: String? = null,
    val title: String,
    val description: String? = null,
    val category: ExpenseCategory = ExpenseCategory.GENERAL_MAINTENANCE,
    val amount: Long, // Positive integer in smallest currency units (e.g. Rials)
    val expenseDate: Long = System.currentTimeMillis(),
    val createdBy: String,
    val status: ExpenseStatus = ExpenseStatus.APPROVED
)

enum class ExpenseStatus(val titleFa: String) {
    RECORDED("ثبت‌شده"),
    APPROVED("تأییدشده"),
    REJECTED("ردشده"),
    ALLOCATED("تخصیص‌یافته به دوره"),
    CANCELLED("ابطال‌شده")
}
