package com.apyar.app.data.mapper

import com.apyar.app.data.local.entity.UnitEntity
import com.apyar.app.domain.model.OccupancyStatus
import com.apyar.app.domain.model.Unit

fun UnitEntity.toDomain(): Unit {
    val parsedStatus = try {
        OccupancyStatus.valueOf(occupancyStatus)
    } catch (_: Exception) {
        if (residentCount > 0) OccupancyStatus.OCCUPIED else OccupancyStatus.VACANT
    }

    return Unit(
        id = id,
        buildingId = buildingId,
        unitNumber = unitNumber,
        floor = floor,
        areaSquareMeters = areaSquareMeters,
        residentCount = residentCount,
        occupancyStatus = parsedStatus,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isActive = isActive
    )
}

fun Unit.toEntity(): UnitEntity {
    return UnitEntity(
        id = id,
        buildingId = buildingId,
        unitNumber = unitNumber,
        floor = floor,
        areaSquareMeters = areaSquareMeters,
        residentCount = residentCount,
        occupancyStatus = occupancyStatus.name,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isActive = isActive
    )
}
