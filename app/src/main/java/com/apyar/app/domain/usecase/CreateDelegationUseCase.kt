package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.AccessDeniedException
import com.apyar.app.domain.model.AuditEvent
import com.apyar.app.domain.model.DelegatedPermission
import com.apyar.app.domain.model.DelegationStatus
import com.apyar.app.domain.model.Permission
import com.apyar.app.domain.repository.AuditRepository
import com.apyar.app.domain.repository.BuildingRepository
import com.apyar.app.domain.repository.DelegatedPermissionRepository
import com.apyar.app.domain.repository.UserAccountRepository

class CreateDelegationUseCase(
    private val delegatedPermissionRepository: DelegatedPermissionRepository,
    private val buildingRepository: BuildingRepository,
    private val userAccountRepository: UserAccountRepository,
    private val checkPermissionUseCase: CheckPermissionUseCase? = null,
    private val auditRepository: AuditRepository? = null
) {
    suspend operator fun invoke(
        actorUserId: String,
        buildingId: String,
        grantedToUserId: String,
        permission: Permission,
        startDate: Long = System.currentTimeMillis(),
        endDate: Long
    ): Result<DelegatedPermission> {
        // Enforce permission for actor
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
                        message = "فقط کاربران دارای مجوز مدیریت اعضا می‌توانند تفویض اختیار انجام دهند."
                    )
                )
            }
        }

        if (endDate <= startDate) {
            return Result.failure(IllegalArgumentException("تاریخ پایان تفویض اختیار باید بعد از تاریخ شروع باشد."))
        }

        val building = buildingRepository.getBuildingById(buildingId)
            ?: return Result.failure(IllegalArgumentException("ساختمان مورد نظر یافت نشد."))

        val targetUser = userAccountRepository.getUserAccountById(grantedToUserId)
            ?: return Result.failure(IllegalArgumentException("کاربر دریافت‌کننده تفویض یافت نشد."))

        val delegation = DelegatedPermission(
            buildingId = buildingId,
            grantedByUserId = actorUserId,
            grantedToUserId = grantedToUserId,
            permission = permission,
            startDate = startDate,
            endDate = endDate,
            status = DelegationStatus.ACTIVE
        )

        val created = delegatedPermissionRepository.createDelegation(delegation)

        auditRepository?.recordEvent(
            AuditEvent(
                actor = actorUserId,
                action = "CREATE_DELEGATION",
                entity = "DelegatedPermission",
                entityId = created.id,
                buildingId = buildingId,
                details = "مجوز ${permission.titleFa} به کاربر $grantedToUserId تا تاریخ $endDate تفویض شد."
            )
        )

        return Result.success(created)
    }
}
