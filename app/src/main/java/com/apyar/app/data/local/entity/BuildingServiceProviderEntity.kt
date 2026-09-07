package com.apyar.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "building_service_providers",
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
        Index(value = ["buildingId", "serviceProviderId", "serviceCategory"])
    ]
)
data class BuildingServiceProviderEntity(
    @PrimaryKey
    val id: String,
    val buildingId: String,
    val serviceProviderId: String,
    val serviceCategory: String,
    val role: String,
    val isPrimary: Boolean,
    val isActive: Boolean,
    val startDate: Long,
    val endDate: Long?,
    val notes: String?
)
