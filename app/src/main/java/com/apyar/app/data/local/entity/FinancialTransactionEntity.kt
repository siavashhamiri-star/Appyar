package com.apyar.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "financial_transactions",
    foreignKeys = [
        ForeignKey(
            entity = BuildingEntity::class,
            parentColumns = ["id"],
            childColumns = ["buildingId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = FinancialAccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["accountId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["id"], unique = true),
        Index(value = ["buildingId"]),
        Index(value = ["accountId"]),
        Index(value = ["unitId"]),
        Index(value = ["createdAt"])
    ]
)
data class FinancialTransactionEntity(
    @PrimaryKey
    val id: String,
    val buildingId: String,
    val accountId: String,
    val unitId: String?,
    val type: String,
    val amount: Long,
    val description: String,
    val reference: String?,
    val category: String?,
    val createdAt: Long,
    val createdBy: String
)
