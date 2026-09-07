package com.apyar.app.domain.model

/**
 * Domain model representing a Building in Apyar.
 */
data class Building(
    val id: String,
    val name: String,
    val address: String,
    val city: String,
    val postalCode: String,
    val unitCount: Int,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
)
