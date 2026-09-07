package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.AuditEvent
import com.apyar.app.domain.model.BuildingMember
import com.apyar.app.domain.model.Permission
import com.apyar.app.domain.model.Role
import com.apyar.app.domain.repository.AuditRepository
import com.apyar.app.domain.repository.BuildingMemberRepository
import com.apyar.app.domain.repository.BuildingRepository
import com.apyar.app.domain.repository.UserAccountRepository

class AddBuildingMemberUseCase(
    private val buildingMemberRepository: BuildingMemberRepository,
    private val buildingRepository: BuildingRepository,
    private val userAccountRepository: UserAccountRepository,
    private val checkPermissionUseCase: CheckPermissionUseCase? = null,
    private val auditRepository: AuditRepository? = null
) {
    suspend operator fun invoke(
        actorUserId: String?,
        buildingId: String,
        targetUserId: String,
        role: Role,
        startDate: Long = System.currentTimeMillis(),
        endDate: Long? = null
    ): Result<BuildingMember> {
        // Enforce permission if actorUserId is provided
        if (actorUserId != null && checkPermissionUseCase != null) {
            val hasPerm = checkPermissionUseCase.hasPermission(
                buildingId = buildingId,
                userId = actorUserId,
                requiredPermission = Permission.INVITE_MEMBER
            ) || checkPermissionUseCase.hasPermission(
                buildingId = buildingId,
                userId = actorUserId,
                requiredPermission = Permission.MANAGE_MEMBERS
            )
            if (!hasPerm) {
                return Result.failure(
                    com.apyar.app.domain.model.AccessDeniedException(
                        requiredPermission = Permission.INVITE_MEMBER,
                        userId = actorUserId,
                        buildingId = buildingId
                    )
                )
            }
        }

        // Validate building exists
        val building = buildingRepository.getBuildingById(buildingId)
            ?: return Result.failure(IllegalArgumentException("ساختمان مورد نظر یافت نشد."))

        // Validate target user exists
        val user = userAccountRepository.getUserAccountById(targetUserId)
            ?: return Result.failure(IllegalArgumentException("حساب کاربری مورد نظر یافت نشد."))

        // Check if member is already active in this building
        val existingMembership = buildingMemberRepository.getMembership(buildingId, targetUserId)
        if (existingMembership != null && existingMembership.isActive) {
            return Result.failure(IllegalStateException("این کاربر در حال حاضر عضو فعال این ساختمان است."))
        }

        val member = BuildingMember(
            buildingId = buildingId,
            userId = targetUserId,
            role = role,
            startDate = startDate,
            endDate = endDate,
            isActive = true
        )

        val createdMember = buildingMemberRepository.addMember(member)

        auditRepository?.recordEvent(
            AuditEvent(
                actor = actorUserId ?: "SYSTEM",
                action = "ADD_BUILDING_MEMBER",
                entity = "BuildingMember",
                entityId = createdMember.id,
                buildingId = buildingId,
                details = "کاربر $targetUserId با نقش ${role.titleFa} به ساختمان $buildingId افزوده شد."
            )
        )

        return Result.success(createdMember)
    }
}
