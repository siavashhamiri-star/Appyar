package com.apyar.app.domain.model

import java.util.UUID

/**
 * DelegatedPermission represents a temporary or specific authority granted by a user
 * (typically BUILDING_ADMIN) to another user (such as APYAR_EXECUTIVE or another member)
 * within the scope of a specific building.
 */
data class DelegatedPermission(
    val id: String = UUID.randomUUID().toString(),
    val buildingId: String,
    val grantedByUserId: String,
    val grantedToUserId: String,
    val permission: Permission,
    val startDate: Long = System.currentTimeMillis(),
    val endDate: Long,
    val status: DelegationStatus = DelegationStatus.ACTIVE,
    val createdAt: Long = System.currentTimeMillis()
) {
    /**
     * Determines whether the delegation is currently valid at the given timestamp.
     */
    fun isCurrentlyValid(currentTime: Long = System.currentTimeMillis()): Boolean {
        return status == DelegationStatus.ACTIVE &&
                currentTime >= startDate &&
                currentTime <= endDate
    }
}
