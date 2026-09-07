package com.apyar.app.test

import com.apyar.app.domain.model.BuildingMember
import com.apyar.app.domain.model.Role
import com.apyar.app.domain.repository.BuildingMemberRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class FakeBuildingMemberRepository : BuildingMemberRepository {
    private val members = MutableStateFlow<Map<String, BuildingMember>>(emptyMap())

    override suspend fun addMember(member: BuildingMember): BuildingMember {
        members.value = members.value + (member.id to member)
        return member
    }

    override suspend fun updateMemberRole(memberId: String, newRole: Role) {
        val current = members.value[memberId] ?: return
        members.value = members.value + (memberId to current.copy(role = newRole, updatedAt = System.currentTimeMillis()))
    }

    override suspend fun removeMember(memberId: String, endDate: Long) {
        val current = members.value[memberId] ?: return
        members.value = members.value + (memberId to current.copy(isActive = false, endDate = endDate, updatedAt = System.currentTimeMillis()))
    }

    override fun getMembersByBuilding(buildingId: String): Flow<List<BuildingMember>> {
        return members.asStateFlow().map { map ->
            map.values.filter { it.buildingId == buildingId && it.isActive }
        }
    }

    override fun getMembershipsByUser(userId: String): Flow<List<BuildingMember>> {
        return members.asStateFlow().map { map ->
            map.values.filter { it.userId == userId && it.isActive }
        }
    }

    override suspend fun getMembership(buildingId: String, userId: String): BuildingMember? {
        return members.value.values.find { it.buildingId == buildingId && it.userId == userId && it.isActive }
    }

    override suspend fun getMemberById(id: String): BuildingMember? {
        return members.value[id]
    }
}
