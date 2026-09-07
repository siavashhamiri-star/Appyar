package com.apyar.app.presentation.agreements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.apyar.app.core.di.AppContainer
import com.apyar.app.domain.model.AgreementDetails
import com.apyar.app.domain.model.AgreementStatus
import com.apyar.app.domain.model.AgreementType
import com.apyar.app.domain.model.FinancialArrangement
import com.apyar.app.domain.model.ParkingSpaceWithDetails
import com.apyar.app.domain.model.StorageUnitWithDetails
import com.apyar.app.domain.model.Unit
import com.apyar.app.domain.usecase.CalculateParkingAdjustmentUseCase
import com.apyar.app.domain.usecase.CalculateStorageAdjustmentUseCase
import com.apyar.app.domain.usecase.CancelAgreementUseCase
import com.apyar.app.domain.usecase.CreateParkingAgreementUseCase
import com.apyar.app.domain.usecase.CreateStorageAgreementUseCase
import com.apyar.app.domain.usecase.GetBuildingAgreementsUseCase
import com.apyar.app.domain.usecase.GetParkingSpacesUseCase
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

data class AgreementsUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val units: List<Unit> = emptyList(),
    val parkingSpaces: List<ParkingSpaceWithDetails> = emptyList(),
    val storageUnits: List<StorageUnitWithDetails> = emptyList(),
    val filterStatus: AgreementStatus? = null,
    val isCreateParkingAgreementDialogOpen: Boolean = false,
    val isCreateStorageAgreementDialogOpen: Boolean = false,
    val selectedAgreement: AgreementDetails? = null,
    val isDetailsDialogOpen: Boolean = false
)

class AgreementsViewModel(
    private val buildingId: String,
    private val currentUserId: String,
    private val getBuildingAgreementsUseCase: GetBuildingAgreementsUseCase,
    private val createParkingAgreementUseCase: CreateParkingAgreementUseCase,
    private val createStorageAgreementUseCase: CreateStorageAgreementUseCase,
    private val cancelAgreementUseCase: CancelAgreementUseCase,
    private val calculateParkingAdjustmentUseCase: CalculateParkingAdjustmentUseCase,
    private val calculateStorageAdjustmentUseCase: CalculateStorageAdjustmentUseCase,
    private val getUnitsByBuildingUseCase: GetUnitsByBuildingUseCase,
    private val getParkingSpacesUseCase: GetParkingSpacesUseCase,
    private val getStorageUnitsUseCase: GetStorageUnitsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AgreementsUiState())
    val uiState: StateFlow<AgreementsUiState> = _uiState.asStateFlow()

    private val _rawAgreements = MutableStateFlow<List<AgreementDetails>>(emptyList())

    val agreements: StateFlow<List<AgreementDetails>> = combine(
        _rawAgreements,
        _uiState
    ) { items, state ->
        if (state.filterStatus == null) {
            items
        } else {
            items.filter { it.agreement.status == state.filterStatus }
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
                getParkingSpacesUseCase(currentUserId, buildingId).collect { spaces ->
                    _uiState.update { it.copy(parkingSpaces = spaces) }
                }
            } catch (_: Exception) {}
        }

        viewModelScope.launch {
            try {
                getStorageUnitsUseCase(currentUserId, buildingId).collect { storages ->
                    _uiState.update { it.copy(storageUnits = storages) }
                }
            } catch (_: Exception) {}
        }

        viewModelScope.launch {
            try {
                getBuildingAgreementsUseCase(currentUserId, buildingId)
                    .catch { e ->
                        _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                    }
                    .collect { list ->
                        _rawAgreements.value = list
                        _uiState.update { it.copy(isLoading = false) }
                    }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun setFilterStatus(status: AgreementStatus?) {
        _uiState.update { it.copy(filterStatus = status) }
    }

    fun openCreateParkingAgreementDialog() {
        _uiState.update { it.copy(isCreateParkingAgreementDialogOpen = true, errorMessage = null) }
    }

    fun closeCreateParkingAgreementDialog() {
        _uiState.update { it.copy(isCreateParkingAgreementDialogOpen = false) }
    }

    fun openCreateStorageAgreementDialog() {
        _uiState.update { it.copy(isCreateStorageAgreementDialogOpen = true, errorMessage = null) }
    }

    fun closeCreateStorageAgreementDialog() {
        _uiState.update { it.copy(isCreateStorageAgreementDialogOpen = false) }
    }

    fun openDetailsDialog(agreement: AgreementDetails) {
        _uiState.update { it.copy(isDetailsDialogOpen = true, selectedAgreement = agreement) }
    }

    fun closeDetailsDialog() {
        _uiState.update { it.copy(isDetailsDialogOpen = false, selectedAgreement = null) }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }

    fun createParkingAgreement(
        parkingSpaceId: String,
        sourceUnitId: String,
        targetUnitId: String,
        startDate: Long,
        endDate: Long?,
        financialArrangement: FinancialArrangement,
        amount: Double,
        description: String?,
        documentTitle: String?,
        documentFilePath: String?
    ) {
        viewModelScope.launch {
            val result = createParkingAgreementUseCase(
                userId = currentUserId,
                buildingId = buildingId,
                parkingSpaceId = parkingSpaceId,
                sourceUnitId = sourceUnitId,
                targetUnitId = targetUnitId,
                startDate = startDate,
                endDate = endDate,
                financialArrangement = financialArrangement,
                amount = amount,
                description = description,
                documentTitle = documentTitle,
                documentFilePath = documentFilePath
            )
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        isCreateParkingAgreementDialogOpen = false,
                        successMessage = "توافق استفاده از پارکینگ با موفقیت ثبت شد"
                    )
                }
            } else {
                _uiState.update {
                    it.copy(errorMessage = result.exceptionOrNull()?.message ?: "خطا در ثبت توافق")
                }
            }
        }
    }

    fun createStorageAgreement(
        storageUnitId: String,
        sourceUnitId: String,
        targetUnitId: String,
        startDate: Long,
        endDate: Long?,
        financialArrangement: FinancialArrangement,
        amount: Double,
        description: String?,
        documentTitle: String?,
        documentFilePath: String?
    ) {
        viewModelScope.launch {
            val result = createStorageAgreementUseCase(
                userId = currentUserId,
                buildingId = buildingId,
                storageUnitId = storageUnitId,
                sourceUnitId = sourceUnitId,
                targetUnitId = targetUnitId,
                startDate = startDate,
                endDate = endDate,
                financialArrangement = financialArrangement,
                amount = amount,
                description = description,
                documentTitle = documentTitle,
                documentFilePath = documentFilePath
            )
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        isCreateStorageAgreementDialogOpen = false,
                        successMessage = "توافق استفاده از انباری با موفقیت ثبت شد"
                    )
                }
            } else {
                _uiState.update {
                    it.copy(errorMessage = result.exceptionOrNull()?.message ?: "خطا در ثبت توافق")
                }
            }
        }
    }

    fun cancelAgreement(agreementId: String, reason: String?) {
        viewModelScope.launch {
            val result = cancelAgreementUseCase(
                userId = currentUserId,
                buildingId = buildingId,
                agreementId = agreementId,
                reason = reason
            )
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        isDetailsDialogOpen = false,
                        selectedAgreement = null,
                        successMessage = "توافق با موفقیت لغو شد و وضعیت فضاها به‌روز گردید"
                    )
                }
            } else {
                _uiState.update {
                    it.copy(errorMessage = result.exceptionOrNull()?.message ?: "خطا در لغو توافق")
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
                return AgreementsViewModel(
                    buildingId = buildingId,
                    currentUserId = currentUserId,
                    getBuildingAgreementsUseCase = appContainer.getBuildingAgreementsUseCase,
                    createParkingAgreementUseCase = appContainer.createParkingAgreementUseCase,
                    createStorageAgreementUseCase = appContainer.createStorageAgreementUseCase,
                    cancelAgreementUseCase = appContainer.cancelAgreementUseCase,
                    calculateParkingAdjustmentUseCase = appContainer.calculateParkingAdjustmentUseCase,
                    calculateStorageAdjustmentUseCase = appContainer.calculateStorageAdjustmentUseCase,
                    getUnitsByBuildingUseCase = appContainer.getUnitsByBuildingUseCase,
                    getParkingSpacesUseCase = appContainer.getParkingSpacesUseCase,
                    getStorageUnitsUseCase = appContainer.getStorageUnitsUseCase
                ) as T
            }
        }
    }
}
