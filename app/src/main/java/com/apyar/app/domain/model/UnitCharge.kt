package com.apyar.app.domain.model

/**
 * Charge statement for an individual unit within a specific ChargePeriod.
 * Immutable after the parent period is finalized; amendments occur via adjustmentAmount.
 */
data class UnitCharge(
    val id: String,
    val chargePeriodId: String,
    val unitId: String,
    val inclusionStatus: InclusionStatus = InclusionStatus.INCLUDED,
    val calculatedAmount: Long = 0L,
    val adjustmentAmount: Long = 0L,
    val finalAmount: Long = 0L, // calculatedAmount + adjustmentAmount
    val paidAmount: Long = 0L,
    val remainingAmount: Long = finalAmount - paidAmount,
    val status: ChargeStatus = ChargeStatus.UNPAID,
    val shareType: ShareType = ShareType.SHARED,
    val calculationNotes: String? = null
)
