package com.apyar.app.domain.model

/**
 * Custom charge formula approved by a building's assembly or board.
 * Apyar does not judge legal validity; it simply records, versions, and executes approved formulas.
 */
data class CustomFormula(
    val id: String,
    val buildingId: String,
    val name: String,
    val description: String,
    val definition: String, // Formula parameters, coefficients, or logic
    val version: Int = 1,
    val approvedAt: Long = System.currentTimeMillis(),
    val approvedBy: String,
    val effectiveFrom: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
)
