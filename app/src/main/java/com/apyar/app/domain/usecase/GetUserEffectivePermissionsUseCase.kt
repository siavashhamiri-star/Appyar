package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.AccountStatus
import com.apyar.app.domain.model.Permission
import com.apyar.app.domain.model.RolePermissionPolicy
import com.apyar.app.domain.repository.BuildingMemberRepository
import com.apyar.app.domain.repository.DelegatedPermissionRepository
import com.apyar.app.domain.repository.UserAccountRepository

/**
 * Calculates the total effective set of permissions for a user within a specific building context.
 *
 * Algorithm:
 * 1. Checks if the user account is active. If disabled/suspended, returns emptySet().
 * 2. Fetches the active BuildingMember record for the (buildingId, userId) pair.
 *    - Obtains base permissions for the user's Role in this building from RolePermissionPolicy.
 *    - Note: For Role.APYAR_EXECUTIVE, the base permission set is strictly emptySet().
 * 3. Fetches all active, non-expired, non-revoked DelegatedPermissions granted to this user in this building.
 * 4. Merges the base role permissions with all active delegated permissions.
 */
class GetUserEffectivePermissionsUseCase(
    private val buildingMemberRepository: BuildingMemberRepository,
    private val delegatedPermissionRepository: DelegatedPermissionRepository,
    private val userAccountRepository: UserAccountRepository? = null
) {
    suspend operator fun invoke(
        buildingId: String,
        userId: String,
        currentTime: Long = System.currentTimeMillis()
    ): Set<Permission> {
        // 1. If user account is suspended or disabled, no permissions apply
        if (userAccountRepository != null) {
            val userAccount = userAccountRepository.getUserAccountById(userId)
            if (userAccount != null && userAccount.accountStatus != AccountStatus.ACTIVE) {
                return emptySet()
            }
        }

        val effectivePermissions = mutableSetOf<Permission>()

        // 2. Base role permissions from BuildingMember relationship
        val membership = buildingMemberRepository.getMembership(buildingId, userId)
        if (membership != null && membership.isActive) {
            // Respect membership dates if specified
            val isDateValid = (membership.endDate == null || membership.endDate >= currentTime) &&
                    membership.startDate <= currentTime
            if (isDateValid) {
                val basePermissions = RolePermissionPolicy.getPermissionsForRole(membership.role)
                effectivePermissions.addAll(basePermissions)
            }
        }

        // 3. Delegated permissions for this building and user
        val activeDelegations = delegatedPermissionRepository.getActiveDelegationsForUser(
            buildingId = buildingId,
            userId = userId,
            currentTime = currentTime
        )

        for (delegation in activeDelegations) {
            if (delegation.isCurrentlyValid(currentTime)) {
                effectivePermissions.add(delegation.permission)
            }
        }

        return effectivePermissions
    }
}
