package com.apyar.app.domain.model

/**
 * Composite model containing UnitCharge along with rich unit details for presentation and review.
 */
data class UnitChargeWithDetails(
    val unitCharge: UnitCharge,
    val unit: Unit
)
