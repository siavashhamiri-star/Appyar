package com.apyar.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "agreement_documents",
    foreignKeys = [
        ForeignKey(
            entity = AgreementEntity::class,
            parentColumns = ["id"],
            childColumns = ["agreementId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["id"], unique = true),
        Index(value = ["agreementId"])
    ]
)
data class AgreementDocumentEntity(
    @PrimaryKey
    val id: String,
    val agreementId: String,
    val documentType: String,
    val title: String,
    val description: String?,
    val fileReference: String,
    val createdAt: Long,
    val uploadedBy: String
)
