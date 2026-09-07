package com.apyar.app.presentation.building_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.apyar.app.domain.model.Building
import com.apyar.app.domain.model.Unit
import com.apyar.app.domain.usecase.CreateUnitUseCase
import com.apyar.app.domain.usecase.GetBuildingByIdUseCase
import com.apyar.app.domain.usecase.GetUnitsByBuildingUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BuildingDetailUiState(
    val building: Building? = null,
    val isLoading: Boolean = true,
    val isAddUnitDialogOpen: Boolean = false,
    val unitNumber: String = "",
    val floor: String = "1",
    val areaSquareMeters: String = "100",
    val residentCount: String = "2",
    val errorMessage: String? = null,
    val isSubmittingUnit: Boolean = false
)

class BuildingDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val getBuildingByIdUseCase: GetBuildingByIdUseCase,
    private val getUnitsByBuildingUseCase: GetUnitsByBuildingUseCase,
    private val createUnitUseCase: CreateUnitUseCase
) : ViewModel() {

    val buildingId: String = checkNotNull(savedStateHandle["buildingId"])

    private val _uiState = MutableStateFlow(BuildingDetailUiState())
    val uiState: StateFlow<BuildingDetailUiState> = _uiState.asStateFlow()

    val units: StateFlow<List<Unit>> = getUnitsByBuildingUseCase(buildingId).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        loadBuilding()
    }

    private fun loadBuilding() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val building = getBuildingByIdUseCase(buildingId)
            _uiState.update { it.copy(building = building, isLoading = false) }
        }
    }

    fun openAddUnitDialog() {
        _uiState.update {
            it.copy(
                isAddUnitDialogOpen = true,
                unitNumber = "",
                floor = "1",
                areaSquareMeters = "100",
                residentCount = "2",
                errorMessage = null
            )
        }
    }

    fun closeAddUnitDialog() {
        _uiState.update { it.copy(isAddUnitDialogOpen = false, errorMessage = null) }
    }

    fun onUnitNumberChanged(number: String) = _uiState.update { it.copy(unitNumber = number) }
    fun onFloorChanged(floor: String) = _uiState.update { it.copy(floor = floor) }
    fun onAreaChanged(area: String) = _uiState.update { it.copy(areaSquareMeters = area) }
    fun onResidentCountChanged(count: String) = _uiState.update { it.copy(residentCount = count) }

    fun submitCreateUnit() {
        val state = _uiState.value
        val number = state.unitNumber.trim()
        if (number.isBlank()) {
            _uiState.update { it.copy(errorMessage = "لطفاً شماره واحد را وارد کنید.") }
            return
        }

        val floor = state.floor.toIntOrNull() ?: 0
        val area = state.areaSquareMeters.toDoubleOrNull() ?: 0.0
        val residents = state.residentCount.toIntOrNull() ?: 0

        if (area <= 0) {
            _uiState.update { it.copy(errorMessage = "متراژ باید بزرگتر از صفر باشد.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmittingUnit = true, errorMessage = null) }
            val result = createUnitUseCase(
                buildingId = buildingId,
                unitNumber = number,
                floor = floor,
                areaSquareMeters = area,
                residentCount = residents
            )
            result.onSuccess {
                _uiState.update { it.copy(isSubmittingUnit = false, isAddUnitDialogOpen = false) }
            }.onFailure { error ->
                _uiState.update { it.copy(isSubmittingUnit = false, errorMessage = error.message ?: "خطا در ثبت واحد") }
            }
        }
    }

    class Factory(
        private val savedStateHandle: SavedStateHandle,
        private val getBuildingByIdUseCase: GetBuildingByIdUseCase,
        private val getUnitsByBuildingUseCase: GetUnitsByBuildingUseCase,
        private val createUnitUseCase: CreateUnitUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return BuildingDetailViewModel(
                savedStateHandle,
                getBuildingByIdUseCase,
                getUnitsByBuildingUseCase,
                createUnitUseCase
            ) as T
        }
    }
}
