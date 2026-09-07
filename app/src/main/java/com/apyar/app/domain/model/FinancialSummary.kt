package com.apyar.app.domain.model

/**
 * Summary metrics for a building's financial state.
 */
data class FinancialSummary(
    val buildingId: String,
    val balance: Long,
    val totalIncome: Long,
    val totalExpense: Long,
    val currentPeriodCharge: Long,
    val totalPaid: Long,
    val totalRemaining: Long,
    val debtorUnitsCount: Int,
    val settledUnitsCount: Int
)
