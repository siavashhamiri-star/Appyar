package com.apyar.app.domain.model

import java.util.UUID

/**
 * Domain entity representing a physical storage unit in the building.
 */
data class StorageUnit(
    val id: String = UUID.randomUUID().toString(),
    val buildingId: String,
    val code: String,
    val areaSquareMeters: Double? = null,
    val locationDescription: String? = null,
    val status: SpaceStatus = SpaceStatus.AVAILABLE,
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
)

/**
 * Historical assignment record for a storage unit.
 */
data class StorageAssignment(
    val id: String = UUID.randomUUID().toString(),
    val storageUnitId: String,
    val unitId: String,
    val personId: String? = null,
    val assignmentType: AssignmentType = AssignmentType.UNIT_ASSIGNED,
    val startDate: Long = System.currentTimeMillis(),
    val endDate: Long? = null,
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
)

/**
 * Rich domain model for a storage unit with associated context.
 */
data class StorageUnitWithDetails(
    val unit: StorageUnit,
    val currentAssignment: StorageAssignment? = null,
    val assignedUnitNumber: String? = null,
    val activeAgreement: Agreement? = null,
    val temporaryUserUnitNumber: String? = null,
    val assignmentHistory: List<StorageAssignment> = emptyList()
)
