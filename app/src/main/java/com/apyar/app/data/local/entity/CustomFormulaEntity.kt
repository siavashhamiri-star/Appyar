package com.apyar.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "custom_formulas",
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
        Index(value = ["buildingId", "version"])
    ]
)
data class CustomFormulaEntity(
    @PrimaryKey
    val id: String,
    val buildingId: String,
    val name: String,
    val description: String,
    val definition: String,
    val version: Int,
    val approvedAt: Long,
    val approvedBy: String,
    val effectiveFrom: Long,
    val isActive: Boolean
)
