package com.apyar.app.data.mapper

import com.apyar.app.data.local.entity.BuildingEntity
import com.apyar.app.domain.model.Building

fun BuildingEntity.toDomain(): Building {
    return Building(
        id = id,
        name = name,
        address = address,
        city = city,
        postalCode = postalCode,
        unitCount = unitCount,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isActive = isActive
    )
}

fun Building.toEntity(): BuildingEntity {
    return BuildingEntity(
        id = id,
        name = name,
        address = address,
        city = city,
        postalCode = postalCode,
        unitCount = unitCount,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isActive = isActive
    )
}
