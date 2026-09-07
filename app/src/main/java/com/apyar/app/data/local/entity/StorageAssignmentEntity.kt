package com.apyar.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "storage_assignments",
    foreignKeys = [
        ForeignKey(
            entity = StorageUnitEntity::class,
            parentColumns = ["id"],
            childColumns = ["storageUnitId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UnitEntity::class,
            parentColumns = ["id"],
            childColumns = ["unitId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["id"], unique = true),
        Index(value = ["storageUnitId"]),
        Index(value = ["unitId"]),
        Index(value = ["isActive"])
    ]
)
data class StorageAssignmentEntity(
    @PrimaryKey
    val id: String,
    val storageUnitId: String,
    val unitId: String,
    val personId: String?,
    val assignmentType: String,
    val startDate: Long,
    val endDate: Long?,
    val notes: String?,
    val createdAt: Long,
    val updatedAt: Long,
    val isActive: Boolean
)
