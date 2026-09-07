package com.apyar.app.domain.model

import java.util.UUID

/**
 * AuditEvent represents a preliminary audit trail record for sensitive identity and role operations.
 * This structure will be expanded in the comprehensive Event Ledger stage.
 */
data class AuditEvent(
    val id: String = UUID.randomUUID().toString(),
    val actor: String,
    val action: String,
    val entity: String,
    val entityId: String,
    val buildingId: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val details: String? = null
)
