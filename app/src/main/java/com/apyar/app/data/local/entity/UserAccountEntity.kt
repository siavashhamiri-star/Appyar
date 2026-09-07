package com.apyar.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_accounts",
    foreignKeys = [
        ForeignKey(
            entity = PersonEntity::class,
            parentColumns = ["id"],
            childColumns = ["personId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["personId"]),
        Index(value = ["mobileNumber"], unique = true)
    ]
)
data class UserAccountEntity(
    @PrimaryKey
    val id: String,
    val personId: String,
    val mobileNumber: String,
    val accountStatus: String, // ACTIVE, SUSPENDED, DISABLED
    val createdAt: Long,
    val updatedAt: Long,
    val lastLoginAt: Long?
)
