package com.apyar.app.test

import com.apyar.app.domain.model.DelegatedPermission
import com.apyar.app.domain.model.DelegationStatus
import com.apyar.app.domain.repository.DelegatedPermissionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class FakeDelegatedPermissionRepository : DelegatedPermissionRepository {
    private val delegations = MutableStateFlow<Map<String, DelegatedPermission>>(emptyMap())

    override suspend fun createDelegation(delegation: DelegatedPermission): DelegatedPermission {
        delegations.value = delegations.value + (delegation.id to delegation)
        return delegation
    }

    override suspend fun revokeDelegation(id: String) {
        val current = delegations.value[id] ?: return
        delegations.value = delegations.value + (id to current.copy(status = DelegationStatus.REVOKED))
    }

    override suspend fun getDelegationById(id: String): DelegatedPermission? {
        return delegations.value[id]
    }

    override fun getDelegationsByBuilding(buildingId: String): Flow<List<DelegatedPermission>> {
        return delegations.asStateFlow().map { map ->
            map.values.filter { it.buildingId == buildingId }
        }
    }

    override fun getDelegationsForUser(buildingId: String, userId: String): Flow<List<DelegatedPermission>> {
        return delegations.asStateFlow().map { map ->
            map.values.filter { it.buildingId == buildingId && it.grantedToUserId == userId }
        }
    }

    override suspend fun getActiveDelegationsForUser(
        buildingId: String,
        userId: String,
        currentTime: Long
    ): List<DelegatedPermission> {
        return delegations.value.values.filter {
            it.buildingId == buildingId &&
            it.grantedToUserId == userId &&
            it.isCurrentlyValid(currentTime)
        }
    }
}
