package com.apyar.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "service_providers",
    indices = [
        Index(value = ["id"], unique = true),
        Index(value = ["mobileNumber"])
    ]
)
data class ServiceProviderEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val companyName: String?,
    val mobileNumber: String,
    val secondaryPhone: String?,
    val email: String?,
    val address: String?,
    val serviceCategory: String,
    val description: String?,
    val createdAt: Long,
    val updatedAt: Long,
    val isActive: Boolean
)
