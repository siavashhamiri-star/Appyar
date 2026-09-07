package com.apyar.app.domain.model

/**
 * Internal financial ledger account for a building.
 * Note: This is an internal application ledger, not an actual bank account.
 * Balance is stored in smallest currency units (e.g., Tomans or Rials) using Long.
 */
data class BuildingFinancialAccount(
    val id: String,
    val buildingId: String,
    val accountName: String,
    val currency: String = "TOMAN",
    val balance: Long = 0L,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
)
