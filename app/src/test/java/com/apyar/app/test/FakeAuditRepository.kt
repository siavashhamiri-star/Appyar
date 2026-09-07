package com.apyar.app.test

import com.apyar.app.domain.model.AuditEvent
import com.apyar.app.domain.repository.AuditRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class FakeAuditRepository : AuditRepository {
    private val events = MutableStateFlow<List<AuditEvent>>(emptyList())

    override suspend fun recordEvent(event: AuditEvent) {
        events.value = events.value + event
    }

    override fun getEventsByBuilding(buildingId: String): Flow<List<AuditEvent>> {
        return events.asStateFlow().map { list ->
            list.filter { it.buildingId == buildingId }
        }
    }

    override fun getEventsByActor(actor: String): Flow<List<AuditEvent>> {
        return events.asStateFlow().map { list ->
            list.filter { it.actor == actor }
        }
    }
}
