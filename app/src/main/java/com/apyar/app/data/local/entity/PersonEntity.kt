package com.apyar.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "people",
    indices = [
        Index(value = ["id"], unique = true),
        Index(value = ["mobileNumber"])
    ]
)
data class PersonEntity(
    @PrimaryKey
    val id: String,
    val firstName: String,
    val lastName: String,
    val mobileNumber: String,
    val email: String?,
    val createdAt: Long,
    val updatedAt: Long,
    val isActive: Boolean
)
