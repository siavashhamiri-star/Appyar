package com.apyar.app.domain.model

/**
 * Represents a billing period for calculating and issuing building charges.
 * Captures historical snapshot of calculationMethodId and formulaVersion.
 */
data class ChargePeriod(
    val id: String,
    val buildingId: String,
    val title: String,
    val periodStart: Long,
    val periodEnd: Long,
    val dueDate: Long = periodEnd,
    val status: ChargePeriodStatus = ChargePeriodStatus.DRAFT,
    val calculationMethodId: String = "",
    val totalBuildingCost: Long = 0L,
    val createdAt: Long = System.currentTimeMillis(),
    val createdBy: String,
    val finalizedAt: Long? = null,
    val formulaVersion: Int = 1
) {
    // Backwards-compatibility getters
    val startDate: Long get() = periodStart
    val endDate: Long get() = periodEnd
    val totalAmount: Long get() = totalBuildingCost
    val chargeRuleId: String get() = calculationMethodId

    // Secondary constructor for existing Stage 3 call sites
    constructor(
        id: String,
        buildingId: String,
        title: String,
        startDate: Long,
        endDate: Long,
        totalAmount: Long,
        chargeRuleId: String,
        formulaVersion: Int = 1,
        status: ChargePeriodStatus = ChargePeriodStatus.DRAFT,
        createdAt: Long = System.currentTimeMillis(),
        finalizedAt: Long? = null,
        createdBy: String
    ) : this(
        id = id,
        buildingId = buildingId,
        title = title,
        periodStart = startDate,
        periodEnd = endDate,
        dueDate = endDate,
        status = status,
        calculationMethodId = chargeRuleId,
        totalBuildingCost = totalAmount,
        createdAt = createdAt,
        createdBy = createdBy,
        finalizedAt = finalizedAt,
        formulaVersion = formulaVersion
    )
}

