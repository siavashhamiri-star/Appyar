package com.apyar.app.data.repository

import com.apyar.app.data.local.dao.DelegatedPermissionDao
import com.apyar.app.data.mapper.toDomain
import com.apyar.app.data.mapper.toEntity
import com.apyar.app.domain.model.DelegatedPermission
import com.apyar.app.domain.repository.DelegatedPermissionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DelegatedPermissionRepositoryImpl(
    private val dao: DelegatedPermissionDao
) : DelegatedPermissionRepository {

    override suspend fun createDelegation(delegation: DelegatedPermission): DelegatedPermission {
        dao.insertDelegation(delegation.toEntity())
        return delegation
    }

    override suspend fun revokeDelegation(id: String) {
        dao.revokeDelegation(id)
    }

    override suspend fun getDelegationById(id: String): DelegatedPermission? {
        return dao.getDelegationById(id)?.toDomain()
    }

    override fun getDelegationsByBuilding(buildingId: String): Flow<List<DelegatedPermission>> {
        return dao.getDelegationsByBuilding(buildingId).map { list -> list.map { it.toDomain() } }
    }

    override fun getDelegationsForUser(buildingId: String, userId: String): Flow<List<DelegatedPermission>> {
        return dao.getDelegationsForUser(buildingId, userId).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getActiveDelegationsForUser(
        buildingId: String,
        userId: String,
        currentTime: Long
    ): List<DelegatedPermission> {
        return dao.getActiveDelegationsForUser(buildingId, userId, currentTime).map { it.toDomain() }
    }
}
