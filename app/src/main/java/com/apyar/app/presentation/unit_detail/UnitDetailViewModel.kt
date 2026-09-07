package com.apyar.app.presentation.unit_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.apyar.app.domain.model.Person
import com.apyar.app.domain.model.RelationType
import com.apyar.app.domain.model.Unit
import com.apyar.app.domain.model.UnitOccupantInfo
import com.apyar.app.domain.usecase.AssignPersonToUnitUseCase
import com.apyar.app.domain.usecase.CreatePersonUseCase
import com.apyar.app.domain.usecase.GetPeopleByUnitUseCase
import com.apyar.app.domain.usecase.GetPeopleUseCase
import com.apyar.app.domain.usecase.GetUnitByIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UnitDetailUiState(
    val unit: Unit? = null,
    val isLoading: Boolean = true,
    val isAssignDialogOpen: Boolean = false,
    val selectedPersonId: String? = null,
    val isNewPerson: Boolean = true,
    val newFirstName: String = "",
    val newLastName: String = "",
    val newMobileNumber: String = "",
    val newEmail: String = "",
    val selectedRelationType: RelationType = RelationType.OWNER,
    val errorMessage: String? = null,
    val isSubmitting: Boolean = false
)

class UnitDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val getUnitByIdUseCase: GetUnitByIdUseCase,
    private val getPeopleByUnitUseCase: GetPeopleByUnitUseCase,
    private val getPeopleUseCase: GetPeopleUseCase,
    private val createPersonUseCase: CreatePersonUseCase,
    private val assignPersonToUnitUseCase: AssignPersonToUnitUseCase
) : ViewModel() {

    val unitId: String = checkNotNull(savedStateHandle["unitId"])

    private val _uiState = MutableStateFlow(UnitDetailUiState())
    val uiState: StateFlow<UnitDetailUiState> = _uiState.asStateFlow()

    val occupants: StateFlow<List<UnitOccupantInfo>> = getPeopleByUnitUseCase(unitId).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allPeople: StateFlow<List<Person>> = getPeopleUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        loadUnit()
    }

    private fun loadUnit() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val unit = getUnitByIdUseCase(unitId)
            _uiState.update { it.copy(unit = unit, isLoading = false) }
        }
    }

    fun openAssignDialog() {
        _uiState.update {
            it.copy(
                isAssignDialogOpen = true,
                isNewPerson = true,
                newFirstName = "",
                newLastName = "",
                newMobileNumber = "",
                newEmail = "",
                selectedRelationType = RelationType.OWNER,
                errorMessage = null
            )
        }
    }

    fun closeAssignDialog() {
        _uiState.update { it.copy(isAssignDialogOpen = false, errorMessage = null) }
    }

    fun setIsNewPerson(isNew: Boolean) = _uiState.update { it.copy(isNewPerson = isNew) }
    fun onSelectedPersonIdChanged(id: String) = _uiState.update { it.copy(selectedPersonId = id) }
    fun onFirstNameChanged(name: String) = _uiState.update { it.copy(newFirstName = name) }
    fun onLastNameChanged(name: String) = _uiState.update { it.copy(newLastName = name) }
    fun onMobileChanged(mobile: String) = _uiState.update { it.copy(newMobileNumber = mobile) }
    fun onEmailChanged(email: String) = _uiState.update { it.copy(newEmail = email) }
    fun onRelationTypeChanged(type: RelationType) = _uiState.update { it.copy(selectedRelationType = type) }

    fun submitAssignment() {
        val state = _uiState.value

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }

            val targetPersonId: String
            if (state.isNewPerson) {
                val firstName = state.newFirstName.trim()
                val lastName = state.newLastName.trim()
                val mobile = state.newMobileNumber.trim()
                if (firstName.isBlank() || lastName.isBlank()) {
                    _uiState.update { it.copy(isSubmitting = false, errorMessage = "لطفاً نام و نام خانوادگی را وارد کنید.") }
                    return@launch
                }
                if (mobile.isBlank()) {
                    _uiState.update { it.copy(isSubmitting = false, errorMessage = "لطفاً شماره همراه را وارد کنید.") }
                    return@launch
                }

                val personResult = createPersonUseCase(
                    firstName = firstName,
                    lastName = lastName,
                    mobileNumber = mobile,
                    email = state.newEmail.trim()
                )
                if (personResult.isFailure) {
                    _uiState.update { it.copy(isSubmitting = false, errorMessage = personResult.exceptionOrNull()?.message ?: "خطا در ثبت شخص") }
                    return@launch
                }
                targetPersonId = personResult.getOrThrow().id
            } else {
                val selectedId = state.selectedPersonId
                if (selectedId.isNullOrBlank()) {
                    _uiState.update { it.copy(isSubmitting = false, errorMessage = "لطفاً یک شخص را انتخاب کنید.") }
                    return@launch
                }
                targetPersonId = selectedId
            }

            val assignResult = assignPersonToUnitUseCase(
                unitId = unitId,
                personId = targetPersonId,
                relationType = state.selectedRelationType,
                startDate = System.currentTimeMillis()
            )

            assignResult.onSuccess {
                _uiState.update { it.copy(isSubmitting = false, isAssignDialogOpen = false) }
            }.onFailure { error ->
                _uiState.update { it.copy(isSubmitting = false, errorMessage = error.message ?: "خطا در اتصال به واحد") }
            }
        }
    }

    class Factory(
        private val savedStateHandle: SavedStateHandle,
        private val getUnitByIdUseCase: GetUnitByIdUseCase,
        private val getPeopleByUnitUseCase: GetPeopleByUnitUseCase,
        private val getPeopleUseCase: GetPeopleUseCase,
        private val createPersonUseCase: CreatePersonUseCase,
        private val assignPersonToUnitUseCase: AssignPersonToUnitUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return UnitDetailViewModel(
                savedStateHandle,
                getUnitByIdUseCase,
                getPeopleByUnitUseCase,
                getPeopleUseCase,
                createPersonUseCase,
                assignPersonToUnitUseCase
            ) as T
        }
    }
}
