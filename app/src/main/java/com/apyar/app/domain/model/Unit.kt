package com.apyar.app.domain.model

/**
 * Domain model representing a Unit within a Building.
 * Includes explicit OccupancyStatus independent of active status or ownership.
 */
data class Unit(
    val id: String,
    val buildingId: String,
    val unitNumber: String,
    val floor: Int,
    val areaSquareMeters: Double,
    val residentCount: Int,
    val occupancyStatus: OccupancyStatus = if (residentCount > 0) OccupancyStatus.OCCUPIED else OccupancyStatus.VACANT,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
)
