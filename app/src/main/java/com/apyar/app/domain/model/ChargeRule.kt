package com.apyar.app.domain.model

/**
 * ChargeRule defines how charges are calculated for a building.
 * Supports versioning and extensible formula definitions.
 */
data class ChargeRule(
    val id: String,
    val buildingId: String,
    val name: String,
    val description: String,
    val calculationType: CalculationType,
    val formulaDefinition: String, // Extensible JSON definition, weights, or script
    val version: Int = 1,
    val isActive: Boolean = true,
    val startDate: Long = System.currentTimeMillis(),
    val endDate: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val createdBy: String
)
