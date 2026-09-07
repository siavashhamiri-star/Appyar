package com.apyar.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "unit_person_relations",
    foreignKeys = [
        ForeignKey(
            entity = UnitEntity::class,
            parentColumns = ["id"],
            childColumns = ["unitId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = PersonEntity::class,
            parentColumns = ["id"],
            childColumns = ["personId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["id"], unique = true),
        Index(value = ["unitId"]),
        Index(value = ["personId"]),
        Index(value = ["unitId", "personId"])
    ]
)
data class UnitPersonRelationEntity(
    @PrimaryKey
    val id: String,
    val unitId: String,
    val personId: String,
    val relationType: String, // OWNER, TENANT, RESIDENT
    val startDate: Long,
    val endDate: Long?,
    val isActive: Boolean
)
