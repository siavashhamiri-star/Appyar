package com.apyar.app.data.mapper

import com.apyar.app.data.local.entity.AuditEventEntity
import com.apyar.app.domain.model.AuditEvent

fun AuditEventEntity.toDomain(): AuditEvent {
    return AuditEvent(
        id = id,
        actor = actor,
        action = action,
        entity = entity,
        entityId = entityId,
        buildingId = buildingId,
        timestamp = timestamp,
        details = details
    )
}

fun AuditEvent.toEntity(): AuditEventEntity {
    return AuditEventEntity(
        id = id,
        actor = actor,
        action = action,
        entity = entity,
        entityId = entityId,
        buildingId = buildingId,
        timestamp = timestamp,
        details = details
    )
}
