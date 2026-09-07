package com.apyar.app.domain.model

/**
 * Domain model representing the association between a Person and a Unit,
 * supporting historical tracking of ownership and tenancy.
 */
data class UnitPersonRelation(
    val id: String,
    val unitId: String,
    val personId: String,
    val relationType: RelationType,
    val startDate: Long = System.currentTimeMillis(),
    val endDate: Long? = null,
    val isActive: Boolean = true
)

/**
 * Composite model for presentation layer displaying person details along with their relation info.
 */
data class UnitOccupantInfo(
    val relation: UnitPersonRelation,
    val person: Person
)
