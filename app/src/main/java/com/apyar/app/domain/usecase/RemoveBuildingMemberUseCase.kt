package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.AccessDeniedException
import com.apyar.app.domain.model.AuditEvent
import com.apyar.app.domain.model.Permission
import com.apyar.app.domain.repository.AuditRepository
import com.apyar.app.domain.repository.BuildingMemberRepository

class RemoveBuildingMemberUseCase(
    private val buildingMemberRepository: BuildingMemberRepository,
    private val checkPermissionUseCase: CheckPermissionUseCase? = null,
    private val auditRepository: AuditRepository? = null
) {
    suspend operator fun invoke(
        actorUserId: String?,
        buildingId: String,
        memberId: String
    ): Result<Unit> {
        if (actorUserId != null && checkPermissionUseCase != null) {
            val hasPerm = checkPermissionUseCase.hasPermission(
                buildingId = buildingId,
                userId = actorUserId,
                requiredPermission = Permission.REMOVE_MEMBER
            )
            if (!hasPerm) {
                return Result.failure(
                    AccessDeniedException(
                        requiredPermission = Permission.REMOVE_MEMBER,
                        userId = actorUserId,
                        buildingId = buildingId
                    )
                )
            }
        }

        val member = buildingMemberRepository.getMemberById(memberId)
            ?: return Result.failure(IllegalArgumentException("عضو مورد نظر یافت نشد."))

        buildingMemberRepository.removeMember(memberId)

        auditRepository?.recordEvent(
            AuditEvent(
                actor = actorUserId ?: "SYSTEM",
                action = "REMOVE_BUILDING_MEMBER",
                entity = "BuildingMember",
                entityId = memberId,
                buildingId = buildingId,
                details = "عضو ${member.userId} از ساختمان غیرفعال شد."
            )
        )

        return Result.success(Unit)
    }
}
