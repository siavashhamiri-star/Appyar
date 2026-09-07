package com.apyar.app.domain.repository

import com.apyar.app.domain.model.DelegatedPermission
import kotlinx.coroutines.flow.Flow

interface DelegatedPermissionRepository {
    suspend fun createDelegation(delegation: DelegatedPermission): DelegatedPermission
    suspend fun revokeDelegation(id: String)
    suspend fun getDelegationById(id: String): DelegatedPermission?
    fun getDelegationsByBuilding(buildingId: String): Flow<List<DelegatedPermission>>
    fun getDelegationsForUser(buildingId: String, userId: String): Flow<List<DelegatedPermission>>
    suspend fun getActiveDelegationsForUser(
        buildingId: String,
        userId: String,
        currentTime: Long = System.currentTimeMillis()
    ): List<DelegatedPermission>
}
