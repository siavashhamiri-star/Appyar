package com.apyar.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "financial_accounts",
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
        Index(value = ["buildingId"], unique = true)
    ]
)
data class FinancialAccountEntity(
    @PrimaryKey
    val id: String,
    val buildingId: String,
    val accountName: String,
    val currency: String,
    val balance: Long,
    val createdAt: Long,
    val updatedAt: Long,
    val isActive: Boolean
)
