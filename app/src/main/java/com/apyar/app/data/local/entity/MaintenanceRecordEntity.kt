package com.apyar.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "maintenance_records",
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
        Index(value = ["serviceRecordId"]),
        Index(value = ["buildingId", "performedAt"])
    ]
)
data class MaintenanceRecordEntity(
    @PrimaryKey
    val id: String,
    val buildingId: String,
    val serviceRecordId: String?,
    val title: String,
    val description: String,
    val equipmentType: String?,
    val location: String?,
    val performedAt: Long,
    val nextServiceDate: Long?,
    val performedBy: String,
    val cost: Double?,
    val notes: String?
)
