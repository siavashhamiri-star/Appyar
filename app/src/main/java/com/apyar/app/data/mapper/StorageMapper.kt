package com.apyar.app.data.mapper

import com.apyar.app.data.local.entity.StorageAssignmentEntity
import com.apyar.app.data.local.entity.StorageUnitEntity
import com.apyar.app.domain.model.AssignmentType
import com.apyar.app.domain.model.SpaceStatus
import com.apyar.app.domain.model.StorageAssignment
import com.apyar.app.domain.model.StorageUnit

fun StorageUnitEntity.toDomain(): StorageUnit {
    return StorageUnit(
        id = id,
        buildingId = buildingId,
        code = code,
        areaSquareMeters = areaSquareMeters,
        locationDescription = locationDescription,
        status = try { SpaceStatus.valueOf(status) } catch (e: Exception) { SpaceStatus.AVAILABLE },
        notes = notes,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isActive = isActive
    )
}

fun StorageUnit.toEntity(): StorageUnitEntity {
    return StorageUnitEntity(
        id = id,
        buildingId = buildingId,
        code = code,
        areaSquareMeters = areaSquareMeters,
        locationDescription = locationDescription,
        status = status.name,
        notes = notes,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isActive = isActive
    )
}

fun StorageAssignmentEntity.toDomain(): StorageAssignment {
    return StorageAssignment(
        id = id,
        storageUnitId = storageUnitId,
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

fun StorageAssignment.toEntity(): StorageAssignmentEntity {
    return StorageAssignmentEntity(
        id = id,
        storageUnitId = storageUnitId,
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
