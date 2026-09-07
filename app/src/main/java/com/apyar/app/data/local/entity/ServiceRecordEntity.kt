package com.apyar.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "service_records",
    foreignKeys = [
        ForeignKey(
            entity = BuildingEntity::class,
            parentColumns = ["id"],
            childColumns = ["buildingId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ServiceProviderEntity::class,
            parentColumns = ["id"],
            childColumns = ["serviceProviderId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["id"], unique = true),
        Index(value = ["buildingId"]),
        Index(value = ["serviceProviderId"]),
        Index(value = ["buildingId", "status"]),
        Index(value = ["buildingId", "serviceCategory"])
    ]
)
data class ServiceRecordEntity(
    @PrimaryKey
    val id: String,
    val buildingId: String,
    val serviceProviderId: String,
    val serviceCategory: String,
    val title: String,
    val description: String,
    val requestedAt: Long,
    val scheduledAt: Long?,
    val completedAt: Long?,
    val status: String,
    val createdBy: String,
    val notes: String?
)
