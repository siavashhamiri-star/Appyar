package com.apyar.app.presentation.buildings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.apyar.app.domain.model.Building
import com.apyar.app.domain.usecase.CreateBuildingUseCase
import com.apyar.app.domain.usecase.GetBuildingsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BuildingsUiState(
    val searchQuery: String = "",
    val isCreateDialogOpen: Boolean = false,
    val createName: String = "",
    val createAddress: String = "",
    val createCity: String = "تهران",
    val createPostalCode: String = "",
    val createUnitCount: String = "",
    val errorMessage: String? = null,
    val isSubmitting: Boolean = false
)

class BuildingsViewModel(
    private val getBuildingsUseCase: GetBuildingsUseCase,
    private val createBuildingUseCase: CreateBuildingUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BuildingsUiState())
    val uiState: StateFlow<BuildingsUiState> = _uiState.asStateFlow()

    val buildings: StateFlow<List<Building>> = combine(
        getBuildingsUseCase(),
        _uiState
    ) { list, state ->
        if (state.searchQuery.isBlank()) {
            list
        } else {
            list.filter {
                it.name.contains(state.searchQuery, ignoreCase = true) ||
                it.address.contains(state.searchQuery, ignoreCase = true) ||
                it.city.contains(state.searchQuery, ignoreCase = true)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun openCreateDialog() {
        _uiState.update {
            it.copy(
                isCreateDialogOpen = true,
                createName = "",
                createAddress = "",
                createCity = "تهران",
                createPostalCode = "",
                createUnitCount = "",
                errorMessage = null
            )
        }
    }

    fun closeCreateDialog() {
        _uiState.update { it.copy(isCreateDialogOpen = false, errorMessage = null) }
    }

    fun onNameChanged(name: String) = _uiState.update { it.copy(createName = name) }
    fun onAddressChanged(address: String) = _uiState.update { it.copy(createAddress = address) }
    fun onCityChanged(city: String) = _uiState.update { it.copy(createCity = city) }
    fun onPostalCodeChanged(code: String) = _uiState.update { it.copy(createPostalCode = code) }
    fun onUnitCountChanged(count: String) = _uiState.update { it.copy(createUnitCount = count) }

    fun submitCreateBuilding(onSuccess: (String) -> Unit = {}) {
        val state = _uiState.value
        val name = state.createName.trim()
        if (name.isBlank()) {
            _uiState.update { it.copy(errorMessage = "لطفاً نام ساختمان را وارد کنید.") }
            return
        }

        val unitCount = state.createUnitCount.toIntOrNull() ?: 0
        if (unitCount <= 0) {
            _uiState.update { it.copy(errorMessage = "تعداد واحدها باید عددی بزرگتر از صفر باشد.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
            val result = createBuildingUseCase(
                name = name,
                address = state.createAddress.trim(),
                city = state.createCity.trim(),
                postalCode = state.createPostalCode.trim(),
                unitCount = unitCount
            )
            result.onSuccess { building ->
                _uiState.update { it.copy(isSubmitting = false, isCreateDialogOpen = false) }
                onSuccess(building.id)
            }.onFailure { error ->
                _uiState.update { it.copy(isSubmitting = false, errorMessage = error.message ?: "خطا در ثبت ساختمان") }
            }
        }
    }

    class Factory(
        private val getBuildingsUseCase: GetBuildingsUseCase,
        private val createBuildingUseCase: CreateBuildingUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return BuildingsViewModel(getBuildingsUseCase, createBuildingUseCase) as T
        }
    }
}
