package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.BuildingMemberWithDetails
import com.apyar.app.domain.repository.BuildingMemberRepository
import com.apyar.app.domain.repository.PersonRepository
import com.apyar.app.domain.repository.UserAccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetBuildingMembersUseCase(
    private val buildingMemberRepository: BuildingMemberRepository,
    private val userAccountRepository: UserAccountRepository,
    private val personRepository: PersonRepository
) {
    operator fun invoke(buildingId: String): Flow<List<BuildingMemberWithDetails>> {
        return buildingMemberRepository.getMembersByBuilding(buildingId).map { members ->
            members.map { member ->
                val userAccount = userAccountRepository.getUserAccountById(member.userId)
                val person = userAccount?.personId?.let { personRepository.getPersonById(it) }
                BuildingMemberWithDetails(
                    member = member,
                    userAccount = userAccount,
                    person = person
                )
            }
        }
    }
}
