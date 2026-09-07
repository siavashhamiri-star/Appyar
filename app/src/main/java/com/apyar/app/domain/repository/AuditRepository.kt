package com.apyar.app.domain.repository

import com.apyar.app.domain.model.AuditEvent
import kotlinx.coroutines.flow.Flow

interface AuditRepository {
    suspend fun recordEvent(event: AuditEvent)
    fun getEventsByBuilding(buildingId: String): Flow<List<AuditEvent>>
    fun getEventsByActor(actor: String): Flow<List<AuditEvent>>
}
