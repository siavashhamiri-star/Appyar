package com.apyar.app.data.mapper

import com.apyar.app.data.local.dao.UnitOccupantRelationWithPerson
import com.apyar.app.data.local.entity.UnitPersonRelationEntity
import com.apyar.app.domain.model.RelationType
import com.apyar.app.domain.model.UnitOccupantInfo
import com.apyar.app.domain.model.UnitPersonRelation

fun UnitPersonRelationEntity.toDomain(): UnitPersonRelation {
    return UnitPersonRelation(
        id = id,
        unitId = unitId,
        personId = personId,
        relationType = RelationType.fromString(relationType),
        startDate = startDate,
        endDate = endDate,
        isActive = isActive
    )
}

fun UnitPersonRelation.toEntity(): UnitPersonRelationEntity {
    return UnitPersonRelationEntity(
        id = id,
        unitId = unitId,
        personId = personId,
        relationType = relationType.name,
        startDate = startDate,
        endDate = endDate,
        isActive = isActive
    )
}

fun UnitOccupantRelationWithPerson.toDomain(): UnitOccupantInfo {
    return UnitOccupantInfo(
        relation = relation.toDomain(),
        person = person.toDomain()
    )
}
