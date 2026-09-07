package com.apyar.app.presentation.permissions

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.apyar.app.domain.model.Building
import com.apyar.app.domain.model.BuildingMember
import com.apyar.app.domain.model.DelegatedPermission
import com.apyar.app.domain.model.Permission
import com.apyar.app.domain.model.Person
import com.apyar.app.domain.model.RolePermissionPolicy
import com.apyar.app.domain.model.UserAccount
import com.apyar.app.domain.repository.BuildingMemberRepository
import com.apyar.app.domain.repository.BuildingRepository
import com.apyar.app.domain.repository.DelegatedPermissionRepository
import com.apyar.app.domain.repository.PersonRepository
import com.apyar.app.domain.repository.UserAccountRepository
import com.apyar.app.domain.usecase.GetUserEffectivePermissionsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PermissionsUiState(
    val isLoading: Boolean = true,
    val building: Building? = null,
    val userAccount: UserAccount? = null,
    val person: Person? = null,
    val member: BuildingMember? = null,
    val effectivePermissions: Set<Permission> = emptySet(),
    val baseRolePermissions: Set<Permission> = emptySet(),
    val activeDelegations: List<DelegatedPermission> = emptyList(),
    val errorMessage: String? = null
)

class PermissionsViewModel(
    savedStateHandle: SavedStateHandle,
    private val getUserEffectivePermissionsUseCase: GetUserEffectivePermissionsUseCase,
    private val buildingMemberRepository: BuildingMemberRepository,
    private val buildingRepository: BuildingRepository,
    private val userAccountRepository: UserAccountRepository,
    private val personRepository: PersonRepository,
    private val delegatedPermissionRepository: DelegatedPermissionRepository
) : ViewModel() {

    private val buildingId: String = checkNotNull(savedStateHandle["buildingId"])
    private val userId: String = checkNotNull(savedStateHandle["userId"])

    private val _uiState = MutableStateFlow(PermissionsUiState())
    val uiState: StateFlow<PermissionsUiState> = _uiState.asStateFlow()

    init {
        loadPermissions()
    }

    fun loadPermissions() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val building = buildingRepository.getBuildingById(buildingId)
                val user = userAccountRepository.getUserAccountById(userId)
                val person = user?.personId?.let { personRepository.getPersonById(it) }
                val membership = buildingMemberRepository.getMembership(buildingId, userId)

                val effective = getUserEffectivePermissionsUseCase(buildingId, userId)
                val base = membership?.role?.let { RolePermissionPolicy.getPermissionsForRole(it) } ?: emptySet()
                val delegations = delegatedPermissionRepository.getActiveDelegationsForUser(buildingId, userId)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        building = building,
                        userAccount = user,
                        person = person,
                        member = membership,
                        effectivePermissions = effective,
                        baseRolePermissions = base,
                        activeDelegations = delegations
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "خطا در بارگذاری دسترسی‌ها"
                    )
                }
            }
        }
    }

    class Factory(
        private val savedStateHandle: SavedStateHandle,
        private val getUserEffectivePermissionsUseCase: GetUserEffectivePermissionsUseCase,
        private val buildingMemberRepository: BuildingMemberRepository,
        private val buildingRepository: BuildingRepository,
        private val userAccountRepository: UserAccountRepository,
        private val personRepository: PersonRepository,
        private val delegatedPermissionRepository: DelegatedPermissionRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PermissionsViewModel(
                savedStateHandle = savedStateHandle,
                getUserEffectivePermissionsUseCase = getUserEffectivePermissionsUseCase,
                buildingMemberRepository = buildingMemberRepository,
                buildingRepository = buildingRepository,
                userAccountRepository = userAccountRepository,
                personRepository = personRepository,
                delegatedPermissionRepository = delegatedPermissionRepository
            ) as T
        }
    }
}
