package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.AccessDeniedException
import com.apyar.app.domain.model.AuditEvent
import com.apyar.app.domain.model.Permission
import com.apyar.app.domain.repository.AuditRepository
import com.apyar.app.domain.repository.DelegatedPermissionRepository

class RevokeDelegationUseCase(
    private val delegatedPermissionRepository: DelegatedPermissionRepository,
    private val checkPermissionUseCase: CheckPermissionUseCase? = null,
    private val auditRepository: AuditRepository? = null
) {
    suspend operator fun invoke(
        actorUserId: String,
        buildingId: String,
        delegationId: String
    ): Result<Unit> {
        if (checkPermissionUseCase != null) {
            val hasPerm = checkPermissionUseCase.hasPermission(
                buildingId = buildingId,
                userId = actorUserId,
                requiredPermission = Permission.MANAGE_MEMBERS
            )
            if (!hasPerm) {
                return Result.failure(
                    AccessDeniedException(
                        requiredPermission = Permission.MANAGE_MEMBERS,
                        userId = actorUserId,
                        buildingId = buildingId,
                        message = "مجوز لازم برای لغو تفویض اختیار وجود ندارد."
                    )
                )
            }
        }

        val delegation = delegatedPermissionRepository.getDelegationById(delegationId)
            ?: return Result.failure(IllegalArgumentException("تفویض اختیار مورد نظر یافت نشد."))

        delegatedPermissionRepository.revokeDelegation(delegationId)

        auditRepository?.recordEvent(
            AuditEvent(
                actor = actorUserId,
                action = "REVOKE_DELEGATION",
                entity = "DelegatedPermission",
                entityId = delegationId,
                buildingId = buildingId,
                details = "تفویض اختیار ${delegation.permission.titleFa} از کاربر ${delegation.grantedToUserId} لغو شد."
            )
        )

        return Result.success(Unit)
    }
}
