package com.apyar.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "building_members",
    foreignKeys = [
        ForeignKey(
            entity = BuildingEntity::class,
            parentColumns = ["id"],
            childColumns = ["buildingId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserAccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["buildingId"]),
        Index(value = ["userId"]),
        Index(value = ["buildingId", "userId"])
    ]
)
data class BuildingMemberEntity(
    @PrimaryKey
    val id: String,
    val buildingId: String,
    val userId: String,
    val role: String, // PLATFORM_ADMIN, BUILDING_ADMIN, etc.
    val startDate: Long,
    val endDate: Long?,
    val isActive: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)
