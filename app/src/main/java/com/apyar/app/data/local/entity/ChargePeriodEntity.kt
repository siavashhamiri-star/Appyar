package com.apyar.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "charge_periods",
    foreignKeys = [
        ForeignKey(
            entity = BuildingEntity::class,
            parentColumns = ["id"],
            childColumns = ["buildingId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ChargeRuleEntity::class,
            parentColumns = ["id"],
            childColumns = ["chargeRuleId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["id"], unique = true),
        Index(value = ["buildingId"]),
        Index(value = ["chargeRuleId"]),
        Index(value = ["startDate", "endDate"])
    ]
)
data class ChargePeriodEntity(
    @PrimaryKey
    val id: String,
    val buildingId: String,
    val title: String,
    val startDate: Long,
    val endDate: Long,
    val totalAmount: Long,
    val chargeRuleId: String,
    val formulaVersion: Int,
    val status: String,
    val createdAt: Long,
    val finalizedAt: Long?,
    val createdBy: String
)
