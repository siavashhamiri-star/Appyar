package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.AccessDeniedException
import com.apyar.app.domain.model.Permission

/**
 * Enforces permission checks at the Domain Layer.
 * Can be used by other UseCases to guard sensitive operations.
 */
class CheckPermissionUseCase(
    private val getUserEffectivePermissionsUseCase: GetUserEffectivePermissionsUseCase
) {
    suspend fun hasPermission(
        buildingId: String,
        userId: String,
        requiredPermission: Permission,
        currentTime: Long = System.currentTimeMillis()
    ): Boolean {
        val permissions = getUserEffectivePermissionsUseCase(
            buildingId = buildingId,
            userId = userId,
            currentTime = currentTime
        )
        return permissions.contains(requiredPermission)
    }

    suspend fun enforce(
        buildingId: String,
        userId: String,
        requiredPermission: Permission,
        currentTime: Long = System.currentTimeMillis()
    ) {
        val allowed = hasPermission(buildingId, userId, requiredPermission, currentTime)
        if (!allowed) {
            throw AccessDeniedException(
                requiredPermission = requiredPermission,
                userId = userId,
                buildingId = buildingId,
                message = "دسترسی غیرمجاز: کاربر با شناسه $userId مجوز ${requiredPermission.titleFa} را در این ساختمان ندارد."
            )
        }
    }
}
