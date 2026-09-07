package com.apyar.app.data.repository

import com.apyar.app.data.local.dao.AuditEventDao
import com.apyar.app.data.mapper.toDomain
import com.apyar.app.data.mapper.toEntity
import com.apyar.app.domain.model.AuditEvent
import com.apyar.app.domain.repository.AuditRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuditRepositoryImpl(
    private val dao: AuditEventDao
) : AuditRepository {

    override suspend fun recordEvent(event: AuditEvent) {
        dao.insertEvent(event.toEntity())
    }

    override fun getEventsByBuilding(buildingId: String): Flow<List<AuditEvent>> {
        return dao.getEventsByBuilding(buildingId).map { list -> list.map { it.toDomain() } }
    }

    override fun getEventsByActor(actor: String): Flow<List<AuditEvent>> {
        return dao.getEventsByActor(actor).map { list -> list.map { it.toDomain() } }
    }
}
