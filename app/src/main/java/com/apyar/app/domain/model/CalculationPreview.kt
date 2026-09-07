package com.apyar.app.domain.model

/**
 * Transparent calculation preview displayed to managers before finalizing a ChargePeriod.
 * Explicitly details total amount, rule version, included/excluded units, breakdown, and rounding audit.
 */
data class CalculationPreview(
    val chargePeriodId: String,
    val ruleName: String,
    val calculationType: CalculationType,
    val formulaVersion: Int,
    val totalCost: Long,
    val includedUnitsCount: Int,
    val excludedUnitsCount: Int,
    val totalIncludedArea: Double,
    val totalIncludedResidents: Int,
    val unitBreakdown: List<UnitChargeWithDetails>,
    val calculatedSum: Long,
    val roundingDifference: Long,
    val auditNote: String
)
