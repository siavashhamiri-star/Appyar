package com.apyar.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "buildings",
    indices = [
        Index(value = ["id"], unique = true)
    ]
)
data class BuildingEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val address: String,
    val city: String,
    val postalCode: String,
    val unitCount: Int,
    val createdAt: Long,
    val updatedAt: Long,
    val isActive: Boolean
)
