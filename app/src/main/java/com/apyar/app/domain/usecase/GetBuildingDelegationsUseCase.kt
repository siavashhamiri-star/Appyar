package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.DelegatedPermissionWithDetails
import com.apyar.app.domain.repository.DelegatedPermissionRepository
import com.apyar.app.domain.repository.PersonRepository
import com.apyar.app.domain.repository.UserAccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetBuildingDelegationsUseCase(
    private val delegatedPermissionRepository: DelegatedPermissionRepository,
    private val userAccountRepository: UserAccountRepository,
    private val personRepository: PersonRepository
) {
    operator fun invoke(buildingId: String): Flow<List<DelegatedPermissionWithDetails>> {
        return delegatedPermissionRepository.getDelegationsByBuilding(buildingId).map { delegations ->
            delegations.map { del ->
                val granterUser = userAccountRepository.getUserAccountById(del.grantedByUserId)
                val granterPerson = granterUser?.personId?.let { personRepository.getPersonById(it) }

                val granteeUser = userAccountRepository.getUserAccountById(del.grantedToUserId)
                val granteePerson = granteeUser?.personId?.let { personRepository.getPersonById(it) }

                DelegatedPermissionWithDetails(
                    delegation = del,
                    grantedByUser = granterUser,
                    grantedByPerson = granterPerson,
                    grantedToUser = granteeUser,
                    grantedToPerson = granteePerson
                )
            }
        }
    }
}
