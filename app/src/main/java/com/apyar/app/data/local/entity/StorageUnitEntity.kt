package com.apyar.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "storage_units",
    foreignKeys = [
        ForeignKey(
            entity = BuildingEntity::class,
            parentColumns = ["id"],
            childColumns = ["buildingId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["id"], unique = true),
        Index(value = ["buildingId"]),
        Index(value = ["buildingId", "code"], unique = true)
    ]
)
data class StorageUnitEntity(
    @PrimaryKey
    val id: String,
    val buildingId: String,
    val code: String,
    val areaSquareMeters: Double?,
    val locationDescription: String?,
    val status: String,
    val notes: String?,
    val createdAt: Long,
    val updatedAt: Long,
    val isActive: Boolean
)
