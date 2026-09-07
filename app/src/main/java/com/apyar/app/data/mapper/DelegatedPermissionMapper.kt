package com.apyar.app.data.mapper

import com.apyar.app.data.local.entity.DelegatedPermissionEntity
import com.apyar.app.domain.model.DelegatedPermission
import com.apyar.app.domain.model.DelegationStatus
import com.apyar.app.domain.model.Permission

fun DelegatedPermissionEntity.toDomain(): DelegatedPermission {
    return DelegatedPermission(
        id = id,
        buildingId = buildingId,
        grantedByUserId = grantedByUserId,
        grantedToUserId = grantedToUserId,
        permission = try {
            Permission.valueOf(permission)
        } catch (e: Exception) {
            Permission.VIEW_BUILDING
        },
        startDate = startDate,
        endDate = endDate,
        status = try {
            DelegationStatus.valueOf(status)
        } catch (e: Exception) {
            DelegationStatus.ACTIVE
        },
        createdAt = createdAt
    )
}

fun DelegatedPermission.toEntity(): DelegatedPermissionEntity {
    return DelegatedPermissionEntity(
        id = id,
        buildingId = buildingId,
        grantedByUserId = grantedByUserId,
        grantedToUserId = grantedToUserId,
        permission = permission.name,
        startDate = startDate,
        endDate = endDate,
        status = status.name,
        createdAt = createdAt
    )
}
