package com.apyar.app.data.mapper

import com.apyar.app.data.local.entity.ParkingAssignmentEntity
import com.apyar.app.data.local.entity.ParkingSpaceEntity
import com.apyar.app.domain.model.AssignmentType
import com.apyar.app.domain.model.ParkingAssignment
import com.apyar.app.domain.model.ParkingSpace
import com.apyar.app.domain.model.ParkingType
import com.apyar.app.domain.model.SpaceStatus

fun ParkingSpaceEntity.toDomain(): ParkingSpace {
    return ParkingSpace(
        id = id,
        buildingId = buildingId,
        code = code,
        floor = floor,
        locationDescription = locationDescription,
        parkingType = try { ParkingType.valueOf(parkingType) } catch (e: Exception) { ParkingType.NORMAL },
        status = try { SpaceStatus.valueOf(status) } catch (e: Exception) { SpaceStatus.AVAILABLE },
        notes = notes,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isActive = isActive
    )
}

fun ParkingSpace.toEntity(): ParkingSpaceEntity {
    return ParkingSpaceEntity(
        id = id,
        buildingId = buildingId,
        code = code,
        floor = floor,
        locationDescription = locationDescription,
        parkingType = parkingType.name,
        status = status.name,
        notes = notes,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isActive = isActive
    )
}

fun ParkingAssignmentEntity.toDomain(): ParkingAssignment {
    return ParkingAssignment(
        id = id,
        parkingSpaceId = parkingSpaceId,
        unitId = unitId,
        personId = personId,
        assignmentType = try { AssignmentType.valueOf(assignmentType) } catch (e: Exception) { AssignmentType.UNIT_ASSIGNED },
        startDate = startDate,
        endDate = endDate,
        notes = notes,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isActive = isActive
    )
}

fun ParkingAssignment.toEntity(): ParkingAssignmentEntity {
    return ParkingAssignmentEntity(
        id = id,
        parkingSpaceId = parkingSpaceId,
        unitId = unitId,
        personId = personId,
        assignmentType = assignmentType.name,
        startDate = startDate,
        endDate = endDate,
        notes = notes,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isActive = isActive
    )
}
