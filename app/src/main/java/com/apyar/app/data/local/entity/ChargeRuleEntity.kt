package com.apyar.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "charge_rules",
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
data class ChargeRuleEntity(
    @PrimaryKey
    val id: String,
    val buildingId: String,
    val name: String,
    val description: String,
    val calculationType: String,
    val formulaDefinition: String,
    val version: Int,
    val isActive: Boolean,
    val startDate: Long,
    val endDate: Long?,
    val createdAt: Long,
    val updatedAt: Long,
    val createdBy: String
)
