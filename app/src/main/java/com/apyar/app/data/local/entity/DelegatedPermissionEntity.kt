package com.apyar.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "delegated_permissions",
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
            childColumns = ["grantedByUserId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserAccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["grantedToUserId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["buildingId"]),
        Index(value = ["grantedToUserId"]),
        Index(value = ["grantedByUserId"])
    ]
)
data class DelegatedPermissionEntity(
    @PrimaryKey
    val id: String,
    val buildingId: String,
    val grantedByUserId: String,
    val grantedToUserId: String,
    val permission: String,
    val startDate: Long,
    val endDate: Long,
    val status: String, // ACTIVE, EXPIRED, REVOKED
    val createdAt: Long
)
