package com.apyar.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "invoices",
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
        Index(value = ["serviceProviderId"]),
        Index(value = ["serviceRecordId"]),
        Index(value = ["buildingId", "paymentStatus"])
    ]
)
data class InvoiceEntity(
    @PrimaryKey
    val id: String,
    val buildingId: String,
    val serviceProviderId: String?,
    val serviceRecordId: String?,
    val invoiceNumber: String?,
    val issueDate: Long,
    val dueDate: Long?,
    val amount: Double,
    val description: String,
    val paymentStatus: String,
    val createdBy: String
)
