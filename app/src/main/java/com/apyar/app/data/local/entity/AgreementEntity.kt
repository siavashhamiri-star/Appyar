package com.apyar.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "agreements",
    foreignKeys = [
        ForeignKey(
            entity = BuildingEntity::class,
            parentColumns = ["id"],
            childColumns = ["buildingId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UnitEntity::class,
            parentColumns = ["id"],
            childColumns = ["sourceUnitId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UnitEntity::class,
            parentColumns = ["id"],
            childColumns = ["targetUnitId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["id"], unique = true),
        Index(value = ["buildingId"]),
        Index(value = ["sourceUnitId"]),
        Index(value = ["targetUnitId"]),
        Index(value = ["relatedParkingId"]),
        Index(value = ["relatedStorageId"]),
        Index(value = ["status"])
    ]
)
data class AgreementEntity(
    @PrimaryKey
    val id: String,
    val buildingId: String,
    val agreementType: String,
    val sourceUnitId: String,
    val targetUnitId: String,
    val relatedParkingId: String?,
    val relatedStorageId: String?,
    val startDate: Long,
    val endDate: Long?,
    val financialArrangement: String,
    val amount: Long?,
    val description: String,
    val status: String,
    val createdAt: Long,
    val updatedAt: Long,
    val createdBy: String
)
