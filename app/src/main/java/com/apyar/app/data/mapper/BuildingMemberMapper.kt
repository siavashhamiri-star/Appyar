package com.apyar.app.data.mapper

import com.apyar.app.data.local.entity.BuildingMemberEntity
import com.apyar.app.domain.model.BuildingMember
import com.apyar.app.domain.model.Role

fun BuildingMemberEntity.toDomain(): BuildingMember {
    return BuildingMember(
        id = id,
        buildingId = buildingId,
        userId = userId,
        role = try {
            Role.valueOf(role)
        } catch (e: Exception) {
            Role.RESIDENT
        },
        startDate = startDate,
        endDate = endDate,
        isActive = isActive,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun BuildingMember.toEntity(): BuildingMemberEntity {
    return BuildingMemberEntity(
        id = id,
        buildingId = buildingId,
        userId = userId,
        role = role.name,
        startDate = startDate,
        endDate = endDate,
        isActive = isActive,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
