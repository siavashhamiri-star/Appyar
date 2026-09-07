package com.apyar.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "unit_charges",
    foreignKeys = [
        ForeignKey(
            entity = ChargePeriodEntity::class,
            parentColumns = ["id"],
            childColumns = ["chargePeriodId"],
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
        Index(value = ["chargePeriodId"]),
        Index(value = ["unitId"]),
        Index(value = ["chargePeriodId", "unitId"], unique = true)
    ]
)
data class UnitChargeEntity(
    @PrimaryKey
    val id: String,
    val chargePeriodId: String,
    val unitId: String,
    val inclusionStatus: String,
    val calculatedAmount: Long,
    val adjustmentAmount: Long,
    val finalAmount: Long,
    val paidAmount: Long,
    val remainingAmount: Long,
    val status: String,
    val shareType: String,
    val calculationNotes: String?
)
