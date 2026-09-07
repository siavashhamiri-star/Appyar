package com.apyar.app.presentation.members

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.apyar.app.domain.model.BuildingMemberWithDetails
import com.apyar.app.domain.model.Role
import com.apyar.app.domain.model.UserAccount
import com.apyar.app.domain.usecase.AddBuildingMemberUseCase
import com.apyar.app.domain.usecase.GetBuildingMembersUseCase
import com.apyar.app.domain.usecase.RemoveBuildingMemberUseCase
import com.apyar.app.domain.usecase.UpdateBuildingMemberRoleUseCase
import com.apyar.app.domain.repository.UserAccountRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BuildingMembersUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class BuildingMembersViewModel(
    savedStateHandle: SavedStateHandle,
    private val getBuildingMembersUseCase: GetBuildingMembersUseCase,
    private val addBuildingMemberUseCase: AddBuildingMemberUseCase,
    private val updateBuildingMemberRoleUseCase: UpdateBuildingMemberRoleUseCase,
    private val removeBuildingMemberUseCase: RemoveBuildingMemberUseCase,
    private val userAccountRepository: UserAccountRepository
) : ViewModel() {

    val buildingId: String = checkNotNull(savedStateHandle["buildingId"])

    val members: StateFlow<List<BuildingMemberWithDetails>> =
        getBuildingMembersUseCase(buildingId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    val allUserAccounts: StateFlow<List<UserAccount>> =
        userAccountRepository.getAllUserAccounts()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    private val _uiState = MutableStateFlow(BuildingMembersUiState())
    val uiState: StateFlow<BuildingMembersUiState> = _uiState.asStateFlow()

    fun addMember(
        targetUserId: String,
        role: Role,
        actorUserId: String? = null
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
            val result = addBuildingMemberUseCase(
                actorUserId = actorUserId,
                buildingId = buildingId,
                targetUserId = targetUserId,
                role = role
            )
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(isLoading = false, successMessage = "عضو با موفقیت به ساختمان اضافه شد.")
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "خطا در افزودن عضو"
                    )
                }
            }
        }
    }

    fun updateRole(
        memberId: String,
        newRole: Role,
        actorUserId: String? = null
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = updateBuildingMemberRoleUseCase(
                actorUserId = actorUserId,
                buildingId = buildingId,
                memberId = memberId,
                newRole = newRole
            )
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(isLoading = false, successMessage = "نقش کاربر با موفقیت تغییر کرد.")
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "خطا در ویرایش نقش"
                    )
                }
            }
        }
    }

    fun removeMember(
        memberId: String,
        actorUserId: String? = null
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = removeBuildingMemberUseCase(
                actorUserId = actorUserId,
                buildingId = buildingId,
                memberId = memberId
            )
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(isLoading = false, successMessage = "عضو از ساختمان خارج شد.")
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "خطا در حذف عضو"
                    )
                }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }

    class Factory(
        private val savedStateHandle: SavedStateHandle,
        private val getBuildingMembersUseCase: GetBuildingMembersUseCase,
        private val addBuildingMemberUseCase: AddBuildingMemberUseCase,
        private val updateBuildingMemberRoleUseCase: UpdateBuildingMemberRoleUseCase,
        private val removeBuildingMemberUseCase: RemoveBuildingMemberUseCase,
        private val userAccountRepository: UserAccountRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return BuildingMembersViewModel(
                savedStateHandle = savedStateHandle,
                getBuildingMembersUseCase = getBuildingMembersUseCase,
                addBuildingMemberUseCase = addBuildingMemberUseCase,
                updateBuildingMemberRoleUseCase = updateBuildingMemberRoleUseCase,
                removeBuildingMemberUseCase = removeBuildingMemberUseCase,
                userAccountRepository = userAccountRepository
            ) as T
        }
    }
}
