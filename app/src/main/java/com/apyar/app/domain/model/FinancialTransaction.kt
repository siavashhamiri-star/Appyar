package com.apyar.app.domain.model

/**
 * Immutable financial transaction record.
 * Financial transactions cannot be physically deleted; corrections occur via ADJUSTMENT.
 * Amounts are represented as positive Long integers.
 */
data class FinancialTransaction(
    val id: String,
    val buildingId: String,
    val accountId: String,
    val unitId: String? = null,
    val type: TransactionType,
    val amount: Long,
    val description: String,
    val reference: String? = null,
    val category: ExpenseCategory? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val createdBy: String
)
