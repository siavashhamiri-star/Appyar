package com.apyar.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "units",
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
        Index(value = ["buildingId", "unitNumber"], unique = true)
    ]
)
data class UnitEntity(
    @PrimaryKey
    val id: String,
    val buildingId: String,
    val unitNumber: String,
    val floor: Int,
    val areaSquareMeters: Double,
    val residentCount: Int,
    val occupancyStatus: String = "OCCUPIED",
    val createdAt: Long,
    val updatedAt: Long,
    val isActive: Boolean
)
