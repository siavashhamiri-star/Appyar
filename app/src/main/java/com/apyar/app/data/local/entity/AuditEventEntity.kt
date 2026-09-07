package com.apyar.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "audit_events",
    indices = [
        Index(value = ["buildingId"]),
        Index(value = ["actor"]),
        Index(value = ["timestamp"])
    ]
)
data class AuditEventEntity(
    @PrimaryKey
    val id: String,
    val actor: String,
    val action: String,
    val entity: String,
    val entityId: String,
    val buildingId: String?,
    val timestamp: Long,
    val details: String?
)
