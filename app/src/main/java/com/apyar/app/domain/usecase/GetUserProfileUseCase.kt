package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.UserProfile
import com.apyar.app.domain.model.UserBuildingRole
import com.apyar.app.domain.repository.BuildingMemberRepository
import com.apyar.app.domain.repository.BuildingRepository
import com.apyar.app.domain.repository.PersonRepository
import com.apyar.app.domain.repository.UserAccountRepository
import kotlinx.coroutines.flow.first

class GetUserProfileUseCase(
    private val userAccountRepository: UserAccountRepository,
    private val personRepository: PersonRepository,
    private val buildingMemberRepository: BuildingMemberRepository,
    private val buildingRepository: BuildingRepository
) {
    suspend operator fun invoke(userId: String): Result<UserProfile> {
        val userAccount = userAccountRepository.getUserAccountById(userId)
            ?: return Result.failure(IllegalArgumentException("حساب کاربری یافت نشد."))

        val person = personRepository.getPersonById(userAccount.personId)
            ?: return Result.failure(IllegalArgumentException("اطلاعات فردی شخص یافت نشد."))

        val memberships = buildingMemberRepository.getMembershipsByUser(userId).first()
        val buildingRoles = mutableListOf<UserBuildingRole>()

        for (member in memberships) {
            if (member.isActive) {
                val building = buildingRepository.getBuildingById(member.buildingId)
                if (building != null) {
                    buildingRoles.add(
                        UserBuildingRole(
                            building = building,
                            member = member,
                            role = member.role
                        )
                    )
                }
            }
        }

        return Result.success(
            UserProfile(
                userAccount = userAccount,
                person = person,
                buildingRoles = buildingRoles
            )
        )
    }
}
