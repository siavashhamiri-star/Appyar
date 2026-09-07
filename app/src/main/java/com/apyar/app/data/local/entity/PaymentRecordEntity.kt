package com.apyar.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "payment_records",
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
        Index(value = ["invoiceId"]),
        Index(value = ["buildingId", "paidAt"])
    ]
)
data class PaymentRecordEntity(
    @PrimaryKey
    val id: String,
    val buildingId: String,
    val invoiceId: String?,
    val amount: Double,
    val paidAt: Long,
    val paymentMethod: String,
    val referenceNumber: String?,
    val recordedBy: String,
    val notes: String?
)
