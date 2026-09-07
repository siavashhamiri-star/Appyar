package com.apyar.app.presentation.delegations

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.apyar.app.domain.model.BuildingMemberWithDetails
import com.apyar.app.domain.model.DelegatedPermissionWithDetails
import com.apyar.app.domain.model.Permission
import com.apyar.app.domain.usecase.CreateDelegationUseCase
import com.apyar.app.domain.usecase.GetBuildingDelegationsUseCase
import com.apyar.app.domain.usecase.GetBuildingMembersUseCase
import com.apyar.app.domain.usecase.RevokeDelegationUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DelegationsUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class DelegationsViewModel(
    savedStateHandle: SavedStateHandle,
    private val getBuildingDelegationsUseCase: GetBuildingDelegationsUseCase,
    private val createDelegationUseCase: CreateDelegationUseCase,
    private val revokeDelegationUseCase: RevokeDelegationUseCase,
    private val getBuildingMembersUseCase: GetBuildingMembersUseCase
) : ViewModel() {

    val buildingId: String = checkNotNull(savedStateHandle["buildingId"])

    val delegations: StateFlow<List<DelegatedPermissionWithDetails>> =
        getBuildingDelegationsUseCase(buildingId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    val members: StateFlow<List<BuildingMemberWithDetails>> =
        getBuildingMembersUseCase(buildingId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    private val _uiState = MutableStateFlow(DelegationsUiState())
    val uiState: StateFlow<DelegationsUiState> = _uiState.asStateFlow()

    fun createDelegation(
        actorUserId: String,
        grantedToUserId: String,
        permission: Permission,
        durationDays: Int = 30
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
            val now = System.currentTimeMillis()
            val endDate = now + (durationDays * 24L * 60L * 60L * 1000L)

            val result = createDelegationUseCase(
                actorUserId = actorUserId,
                buildingId = buildingId,
                grantedToUserId = grantedToUserId,
                permission = permission,
                startDate = now,
                endDate = endDate
            )

            if (result.isSuccess) {
                _uiState.update {
                    it.copy(isLoading = false, successMessage = "تفویض اختیار با موفقیت ثبت شد.")
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "خطا در ثبت تفویض اختیار"
                    )
                }
            }
        }
    }

    fun revokeDelegation(
        actorUserId: String,
        delegationId: String
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = revokeDelegationUseCase(
                actorUserId = actorUserId,
                buildingId = buildingId,
                delegationId = delegationId
            )
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(isLoading = false, successMessage = "تفویض اختیار با موفقیت لغو شد.")
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "خطا در لغو تفویض اختیار"
                    )
                }
            }
        }
    }

    class Factory(
        private val savedStateHandle: SavedStateHandle,
        private val getBuildingDelegationsUseCase: GetBuildingDelegationsUseCase,
        private val createDelegationUseCase: CreateDelegationUseCase,
        private val revokeDelegationUseCase: RevokeDelegationUseCase,
        private val getBuildingMembersUseCase: GetBuildingMembersUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DelegationsViewModel(
                savedStateHandle = savedStateHandle,
                getBuildingDelegationsUseCase = getBuildingDelegationsUseCase,
                createDelegationUseCase = createDelegationUseCase,
                revokeDelegationUseCase = revokeDelegationUseCase,
                getBuildingMembersUseCase = getBuildingMembersUseCase
            ) as T
        }
    }
}
