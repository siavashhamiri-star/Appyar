package com.apyar.app.data.repository

import com.apyar.app.data.local.dao.BuildingMemberDao
import com.apyar.app.data.mapper.toDomain
import com.apyar.app.data.mapper.toEntity
import com.apyar.app.domain.model.BuildingMember
import com.apyar.app.domain.model.Role
import com.apyar.app.domain.repository.BuildingMemberRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BuildingMemberRepositoryImpl(
    private val dao: BuildingMemberDao
) : BuildingMemberRepository {

    override suspend fun addMember(member: BuildingMember): BuildingMember {
        dao.insertMember(member.toEntity())
        return member
    }

    override suspend fun updateMemberRole(memberId: String, newRole: Role) {
        dao.updateMemberRole(memberId, newRole.name, System.currentTimeMillis())
    }

    override suspend fun removeMember(memberId: String, endDate: Long) {
        dao.deactivateMember(memberId, endDate, System.currentTimeMillis())
    }

    override fun getMembersByBuilding(buildingId: String): Flow<List<BuildingMember>> {
        return dao.getMembersByBuilding(buildingId).map { list -> list.map { it.toDomain() } }
    }

    override fun getMembershipsByUser(userId: String): Flow<List<BuildingMember>> {
        return dao.getMembershipsByUser(userId).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getMembership(buildingId: String, userId: String): BuildingMember? {
        return dao.getMembership(buildingId, userId)?.toDomain()
    }

    override suspend fun getMemberById(id: String): BuildingMember? {
        return dao.getMemberById(id)?.toDomain()
    }
}
