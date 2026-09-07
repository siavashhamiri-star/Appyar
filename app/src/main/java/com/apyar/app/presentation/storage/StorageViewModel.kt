package com.apyar.app.presentation.storage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.apyar.app.core.di.AppContainer
import com.apyar.app.domain.model.AssignmentType
import com.apyar.app.domain.model.SpaceStatus
import com.apyar.app.domain.model.StorageUnitWithDetails
import com.apyar.app.domain.model.Unit
import com.apyar.app.domain.usecase.AssignStorageUnitUseCase
import com.apyar.app.domain.usecase.CreateStorageUnitUseCase
import com.apyar.app.domain.usecase.EndStorageAssignmentUseCase
import com.apyar.app.domain.usecase.GetStorageUnitsUseCase
import com.apyar.app.domain.usecase.GetUnitsByBuildingUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StorageUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val units: List<Unit> = emptyList(),
    val filterStatus: SpaceStatus? = null,
    val isCreateUnitDialogOpen: Boolean = false,
    val isAssignDialogOpen: Boolean = false,
    val isHistoryDialogOpen: Boolean = false,
    val selectedUnitWithDetails: StorageUnitWithDetails? = null
)

class StorageViewModel(
    private val buildingId: String,
    private val currentUserId: String,
    private val getStorageUnitsUseCase: GetStorageUnitsUseCase,
    private val createStorageUnitUseCase: CreateStorageUnitUseCase,
    private val assignStorageUnitUseCase: AssignStorageUnitUseCase,
    private val endStorageAssignmentUseCase: EndStorageAssignmentUseCase,
    private val getUnitsByBuildingUseCase: GetUnitsByBuildingUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(StorageUiState())
    val uiState: StateFlow<StorageUiState> = _uiState.asStateFlow()

    private val _rawStorageUnits = MutableStateFlow<List<StorageUnitWithDetails>>(emptyList())

    val storageUnits: StateFlow<List<StorageUnitWithDetails>> = combine(
        _rawStorageUnits,
        _uiState
    ) { storages, state ->
        if (state.filterStatus == null) {
            storages
        } else {
            storages.filter { it.storage.status == state.filterStatus }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                getUnitsByBuildingUseCase(buildingId).collect { unitList ->
                    _uiState.update { it.copy(units = unitList) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message) }
            }
        }

        viewModelScope.launch {
            try {
                getStorageUnitsUseCase(currentUserId, buildingId)
                    .catch { e ->
                        _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                    }
                    .collect { storages ->
                        _rawStorageUnits.value = storages
                        _uiState.update { it.copy(isLoading = false) }
                    }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun setFilterStatus(status: SpaceStatus?) {
        _uiState.update { it.copy(filterStatus = status) }
    }

    fun openCreateUnitDialog() {
        _uiState.update { it.copy(isCreateUnitDialogOpen = true, errorMessage = null) }
    }

    fun closeCreateUnitDialog() {
        _uiState.update { it.copy(isCreateUnitDialogOpen = false) }
    }

    fun openAssignDialog(unitWithDetails: StorageUnitWithDetails) {
        _uiState.update {
            it.copy(
                isAssignDialogOpen = true,
                selectedUnitWithDetails = unitWithDetails,
                errorMessage = null
            )
        }
    }

    fun closeAssignDialog() {
        _uiState.update { it.copy(isAssignDialogOpen = false, selectedUnitWithDetails = null) }
    }

    fun openHistoryDialog(unitWithDetails: StorageUnitWithDetails) {
        _uiState.update {
            it.copy(
                isHistoryDialogOpen = true,
                selectedUnitWithDetails = unitWithDetails
            )
        }
    }

    fun closeHistoryDialog() {
        _uiState.update { it.copy(isHistoryDialogOpen = false, selectedUnitWithDetails = null) }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }

    fun createStorageUnit(
        code: String,
        floor: Int,
        areaSquareMeters: Double,
        locationDescription: String?,
        notes: String?
    ) {
        viewModelScope.launch {
            val result = createStorageUnitUseCase(
                userId = currentUserId,
                buildingId = buildingId,
                code = code,
                floor = floor,
                areaSquareMeters = areaSquareMeters,
                locationDescription = locationDescription,
                notes = notes
            )
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        isCreateUnitDialogOpen = false,
                        successMessage = "انباری با موفقیت تعریف شد"
                    )
                }
            } else {
                _uiState.update {
                    it.copy(errorMessage = result.exceptionOrNull()?.message ?: "خطا در ایجاد انباری")
                }
            }
        }
    }

    fun assignStorageUnit(
        unitId: String,
        assignmentType: AssignmentType,
        notes: String?
    ) {
        val selected = _uiState.value.selectedUnitWithDetails ?: return
        viewModelScope.launch {
            val result = assignStorageUnitUseCase(
                userId = currentUserId,
                buildingId = buildingId,
                storageUnitId = selected.storage.id,
                unitId = unitId,
                assignmentType = assignmentType,
                notes = notes
            )
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        isAssignDialogOpen = false,
                        selectedUnitWithDetails = null,
                        successMessage = "انباری به واحد منتسب شد"
                    )
                }
            } else {
                _uiState.update {
                    it.copy(errorMessage = result.exceptionOrNull()?.message ?: "خطا در تخصیص انباری")
                }
            }
        }
    }

    fun endAssignment(storageUnitId: String) {
        viewModelScope.launch {
            val result = endStorageAssignmentUseCase(
                userId = currentUserId,
                buildingId = buildingId,
                storageUnitId = storageUnitId
            )
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        successMessage = "تخصیص انباری خاتمه یافت و آزاد شد"
                    )
                }
            } else {
                _uiState.update {
                    it.copy(errorMessage = result.exceptionOrNull()?.message ?: "خطا در آزادسازی انباری")
                }
            }
        }
    }

    companion object {
        fun provideFactory(
            appContainer: AppContainer,
            buildingId: String,
            currentUserId: String = "user-1"
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return StorageViewModel(
                    buildingId = buildingId,
                    currentUserId = currentUserId,
                    getStorageUnitsUseCase = appContainer.getStorageUnitsUseCase,
                    createStorageUnitUseCase = appContainer.createStorageUnitUseCase,
                    assignStorageUnitUseCase = appContainer.assignStorageUnitUseCase,
                    endStorageAssignmentUseCase = appContainer.endStorageAssignmentUseCase,
                    getUnitsByBuildingUseCase = appContainer.getUnitsByBuildingUseCase
                ) as T
            }
        }
    }
}
