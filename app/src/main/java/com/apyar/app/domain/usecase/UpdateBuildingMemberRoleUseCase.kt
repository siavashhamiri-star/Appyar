package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.AccessDeniedException
import com.apyar.app.domain.model.AuditEvent
import com.apyar.app.domain.model.Permission
import com.apyar.app.domain.model.Role
import com.apyar.app.domain.repository.AuditRepository
import com.apyar.app.domain.repository.BuildingMemberRepository

class UpdateBuildingMemberRoleUseCase(
    private val buildingMemberRepository: BuildingMemberRepository,
    private val checkPermissionUseCase: CheckPermissionUseCase? = null,
    private val auditRepository: AuditRepository? = null
) {
    suspend operator fun invoke(
        actorUserId: String?,
        buildingId: String,
        memberId: String,
        newRole: Role
    ): Result<Unit> {
        if (actorUserId != null && checkPermissionUseCase != null) {
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
                        buildingId = buildingId
                    )
                )
            }
        }

        val member = buildingMemberRepository.getMemberById(memberId)
            ?: return Result.failure(IllegalArgumentException("عضو مورد نظر یافت نشد."))

        buildingMemberRepository.updateMemberRole(memberId, newRole)

        auditRepository?.recordEvent(
            AuditEvent(
                actor = actorUserId ?: "SYSTEM",
                action = "UPDATE_MEMBER_ROLE",
                entity = "BuildingMember",
                entityId = memberId,
                buildingId = buildingId,
                details = "نقش کاربر ${member.userId} به ${newRole.titleFa} تغییر یافت."
            )
        )

        return Result.success(Unit)
    }
}
