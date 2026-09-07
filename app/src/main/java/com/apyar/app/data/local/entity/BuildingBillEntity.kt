package com.apyar.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "building_bills",
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
        Index(value = ["buildingId", "billType"]),
        Index(value = ["buildingId", "paymentStatus"])
    ]
)
data class BuildingBillEntity(
    @PrimaryKey
    val id: String,
    val buildingId: String,
    val billType: String,
    val providerName: String,
    val billingPeriod: String,
    val amount: Double,
    val issueDate: Long?,
    val dueDate: Long?,
    val paidAt: Long?,
    val paymentStatus: String,
    val referenceNumber: String?,
    val createdBy: String
)
