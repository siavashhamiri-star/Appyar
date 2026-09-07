package com.apyar.app.domain.model

import java.util.UUID

/**
 * BuildingMember defines the contextual role of a UserAccount inside a specific Building.
 * A single user can belong to multiple buildings with different roles in each.
 */
data class BuildingMember(
    val id: String = UUID.randomUUID().toString(),
    val buildingId: String,
    val userId: String,
    val role: Role,
    val startDate: Long = System.currentTimeMillis(),
    val endDate: Long? = null,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
