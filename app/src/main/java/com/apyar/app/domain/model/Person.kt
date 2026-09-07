package com.apyar.app.domain.model

/**
 * Domain model representing a Person (Owner, Tenant, Resident, etc.).
 */
data class Person(
    val id: String,
    val firstName: String,
    val lastName: String,
    val mobileNumber: String,
    val email: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
) {
    val fullName: String
        get() = "$firstName $lastName".trim()
}
