package com.apyar.app.presentation.parking

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.apyar.app.core.di.AppContainer
import com.apyar.app.domain.model.AssignmentType
import com.apyar.app.domain.model.ParkingSpaceWithDetails
import com.apyar.app.domain.model.ParkingType
import com.apyar.app.domain.model.SpaceStatus
import com.apyar.app.domain.model.Unit
import com.apyar.app.domain.usecase.AssignParkingSpaceUseCase
import com.apyar.app.domain.usecase.CreateParkingSpaceUseCase
import com.apyar.app.domain.usecase.CreateTemporaryParkingUseUseCase
import com.apyar.app.domain.usecase.EndParkingAssignmentUseCase
import com.apyar.app.domain.usecase.GetParkingSpacesUseCase
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

data class ParkingUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val units: List<Unit> = emptyList(),
    val filterStatus: SpaceStatus? = null,
    val isCreateSpaceDialogOpen: Boolean = false,
    val isAssignDialogOpen: Boolean = false,
    val isTempUseDialogOpen: Boolean = false,
    val isHistoryDialogOpen: Boolean = false,
    val selectedSpaceWithDetails: ParkingSpaceWithDetails? = null
)

class ParkingViewModel(
    private val buildingId: String,
    private val currentUserId: String,
    private val getParkingSpacesUseCase: GetParkingSpacesUseCase,
    private val createParkingSpaceUseCase: CreateParkingSpaceUseCase,
    private val assignParkingSpaceUseCase: AssignParkingSpaceUseCase,
    private val createTemporaryParkingUseUseCase: CreateTemporaryParkingUseUseCase,
    private val endParkingAssignmentUseCase: EndParkingAssignmentUseCase,
    private val getUnitsByBuildingUseCase: GetUnitsByBuildingUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ParkingUiState())
    val uiState: StateFlow<ParkingUiState> = _uiState.asStateFlow()

    private val _rawParkingSpaces = MutableStateFlow<List<ParkingSpaceWithDetails>>(emptyList())

    val parkingSpaces: StateFlow<List<ParkingSpaceWithDetails>> = combine(
        _rawParkingSpaces,
        _uiState
    ) { spaces, state ->
        if (state.filterStatus == null) {
            spaces
        } else {
            spaces.filter { it.space.status == state.filterStatus }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                // Load Units
                getUnitsByBuildingUseCase(buildingId).collect { unitList ->
                    _uiState.update { it.copy(units = unitList) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message) }
            }
        }

        viewModelScope.launch {
            try {
                getParkingSpacesUseCase(currentUserId, buildingId)
                    .catch { e ->
                        _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                    }
                    .collect { spaces ->
                        _rawParkingSpaces.value = spaces
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

    fun openCreateSpaceDialog() {
        _uiState.update { it.copy(isCreateSpaceDialogOpen = true, errorMessage = null) }
    }

    fun closeCreateSpaceDialog() {
        _uiState.update { it.copy(isCreateSpaceDialogOpen = false) }
    }

    fun openAssignDialog(space: ParkingSpaceWithDetails) {
        _uiState.update {
            it.copy(
                isAssignDialogOpen = true,
                selectedSpaceWithDetails = space,
                errorMessage = null
            )
        }
    }

    fun closeAssignDialog() {
        _uiState.update { it.copy(isAssignDialogOpen = false, selectedSpaceWithDetails = null) }
    }

    fun openTempUseDialog(space: ParkingSpaceWithDetails) {
        _uiState.update {
            it.copy(
                isTempUseDialogOpen = true,
                selectedSpaceWithDetails = space,
                errorMessage = null
            )
        }
    }

    fun closeTempUseDialog() {
        _uiState.update { it.copy(isTempUseDialogOpen = false, selectedSpaceWithDetails = null) }
    }

    fun openHistoryDialog(space: ParkingSpaceWithDetails) {
        _uiState.update {
            it.copy(
                isHistoryDialogOpen = true,
                selectedSpaceWithDetails = space
            )
        }
    }

    fun closeHistoryDialog() {
        _uiState.update { it.copy(isHistoryDialogOpen = false, selectedSpaceWithDetails = null) }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }

    fun createParkingSpace(
        code: String,
        floor: Int,
        locationDescription: String?,
        parkingType: ParkingType,
        notes: String?
    ) {
        viewModelScope.launch {
            val result = createParkingSpaceUseCase(
                userId = currentUserId,
                buildingId = buildingId,
                code = code,
                floor = floor,
                locationDescription = locationDescription,
                parkingType = parkingType,
                notes = notes
            )
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        isCreateSpaceDialogOpen = false,
                        successMessage = "پارکینگ با موفقیت تعریف شد"
                    )
                }
            } else {
                _uiState.update {
                    it.copy(errorMessage = result.exceptionOrNull()?.message ?: "خطا در ایجاد پارکینگ")
                }
            }
        }
    }

    fun assignParkingSpace(
        unitId: String,
        assignmentType: AssignmentType,
        notes: String?
    ) {
        val selected = _uiState.value.selectedSpaceWithDetails ?: return
        viewModelScope.launch {
            val result = assignParkingSpaceUseCase(
                userId = currentUserId,
                buildingId = buildingId,
                parkingSpaceId = selected.space.id,
                unitId = unitId,
                assignmentType = assignmentType,
                notes = notes
            )
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        isAssignDialogOpen = false,
                        selectedSpaceWithDetails = null,
                        successMessage = "پارکینگ به واحد منتسب شد"
                    )
                }
            } else {
                _uiState.update {
                    it.copy(errorMessage = result.exceptionOrNull()?.message ?: "خطا در تخصیص پارکینگ")
                }
            }
        }
    }

    fun createTemporaryUse(
        targetUnitId: String,
        description: String?
    ) {
        val selected = _uiState.value.selectedSpaceWithDetails ?: return
        viewModelScope.launch {
            val result = createTemporaryParkingUseUseCase(
                userId = currentUserId,
                buildingId = buildingId,
                parkingSpaceId = selected.space.id,
                targetUnitId = targetUnitId,
                description = description
            )
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        isTempUseDialogOpen = false,
                        selectedSpaceWithDetails = null,
                        successMessage = "استفاده موقت ثبت گردید"
                    )
                }
            } else {
                _uiState.update {
                    it.copy(errorMessage = result.exceptionOrNull()?.message ?: "خطا در ثبت استفاده موقت")
                }
            }
        }
    }

    fun endAssignment(parkingSpaceId: String) {
        viewModelScope.launch {
            val result = endParkingAssignmentUseCase(
                userId = currentUserId,
                buildingId = buildingId,
                parkingSpaceId = parkingSpaceId
            )
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        successMessage = "تخصیص پارکینگ با موفقیت خاتمه یافت و آزاد شد"
                    )
                }
            } else {
                _uiState.update {
                    it.copy(errorMessage = result.exceptionOrNull()?.message ?: "خطا در آزادسازی پارکینگ")
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
                return ParkingViewModel(
                    buildingId = buildingId,
                    currentUserId = currentUserId,
                    getParkingSpacesUseCase = appContainer.getParkingSpacesUseCase,
                    createParkingSpaceUseCase = appContainer.createParkingSpaceUseCase,
                    assignParkingSpaceUseCase = appContainer.assignParkingSpaceUseCase,
                    createTemporaryParkingUseUseCase = appContainer.createTemporaryParkingUseUseCase,
                    endParkingAssignmentUseCase = appContainer.endParkingAssignmentUseCase,
                    getUnitsByBuildingUseCase = appContainer.getUnitsByBuildingUseCase
                ) as T
            }
        }
    }
}
