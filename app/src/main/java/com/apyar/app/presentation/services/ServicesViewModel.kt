package com.apyar.app.presentation.services

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.apyar.app.core.di.AppContainer
import com.apyar.app.domain.model.BillType
import com.apyar.app.domain.model.BuildingBill
import com.apyar.app.domain.model.BuildingServiceArchive
import com.apyar.app.domain.model.BuildingServiceProvider
import com.apyar.app.domain.model.Invoice
import com.apyar.app.domain.model.InvoiceStatus
import com.apyar.app.domain.model.MaintenanceRecord
import com.apyar.app.domain.model.PaymentMethod
import com.apyar.app.domain.model.PaymentRecord
import com.apyar.app.domain.model.ProviderCategory
import com.apyar.app.domain.model.ProviderRoleInBuilding
import com.apyar.app.domain.model.ProviderStatus
import com.apyar.app.domain.model.ServiceDashboardSummary
import com.apyar.app.domain.model.ServicePriority
import com.apyar.app.domain.model.ServiceProvider
import com.apyar.app.domain.model.ServiceRecord
import com.apyar.app.domain.model.ServiceRecordStatus
import com.apyar.app.domain.model.ServiceType
import com.apyar.app.domain.usecase.CreateBuildingBillUseCase
import com.apyar.app.domain.usecase.CreateInvoiceUseCase
import com.apyar.app.domain.usecase.CreateMaintenanceRecordUseCase
import com.apyar.app.domain.usecase.CreateServiceProviderUseCase
import com.apyar.app.domain.usecase.CreateServiceRecordUseCase
import com.apyar.app.domain.usecase.GetBuildingBillsUseCase
import com.apyar.app.domain.usecase.GetBuildingInvoicesUseCase
import com.apyar.app.domain.usecase.GetBuildingMaintenanceRecordsUseCase
import com.apyar.app.domain.usecase.GetBuildingPaymentsUseCase
import com.apyar.app.domain.usecase.GetBuildingServiceArchiveUseCase
import com.apyar.app.domain.usecase.GetBuildingServiceProvidersUseCase
import com.apyar.app.domain.usecase.GetBuildingServiceRecordsUseCase
import com.apyar.app.domain.usecase.GetServiceDashboardSummaryUseCase
import com.apyar.app.domain.usecase.LinkServiceProviderToBuildingUseCase
import com.apyar.app.domain.usecase.RecordBuildingPaymentUseCase
import com.apyar.app.domain.usecase.UpdateBuildingBillPaymentUseCase
import com.apyar.app.domain.usecase.UpdateBuildingServiceProviderRoleUseCase
import com.apyar.app.domain.usecase.UpdateServiceRecordStatusUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class ServiceTab(val title: String) {
    DASHBOARD("داشبورد"),
    PROVIDERS("سرویسکاران"),
    SERVICES("خدمات و تعمیرات"),
    MAINTENANCE("نگهداری دوره‌ای"),
    INVOICES("فاکتور و تسویه"),
    BILLS("قبوض عمومی"),
    ARCHIVE("آرشیو سوابق")
}

data class ServicesUiState(
    val selectedTab: ServiceTab = ServiceTab.DASHBOARD,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val summary: ServiceDashboardSummary? = null,
    val providers: List<BuildingServiceProvider> = emptyList(),
    val serviceRecords: List<ServiceRecord> = emptyList(),
    val maintenanceRecords: List<MaintenanceRecord> = emptyList(),
    val invoices: List<Invoice> = emptyList(),
    val payments: List<PaymentRecord> = emptyList(),
    val bills: List<BuildingBill> = emptyList(),
    val archive: BuildingServiceArchive? = null,
    // Filters & Search
    val serviceStatusFilter: ServiceRecordStatus? = null,
    val invoiceStatusFilter: InvoiceStatus? = null,
    val billTypeFilter: BillType? = null,
    val archiveQuery: String = "",
    // Dialog triggers
    val isAddProviderDialogOpen: Boolean = false,
    val isAddServiceRecordDialogOpen: Boolean = false,
    val isAddMaintenanceDialogOpen: Boolean = false,
    val isAddInvoiceDialogOpen: Boolean = false,
    val isAddPaymentDialogOpen: Boolean = false,
    val isAddBillDialogOpen: Boolean = false,
    val selectedInvoiceForPayment: Invoice? = null,
    val selectedServiceRecordForStatusUpdate: ServiceRecord? = null
)

class ServicesViewModel(
    private val buildingId: String,
    private val currentUserId: String,
    private val createServiceProviderUseCase: CreateServiceProviderUseCase,
    private val linkServiceProviderToBuildingUseCase: LinkServiceProviderToBuildingUseCase,
    private val updateBuildingServiceProviderRoleUseCase: UpdateBuildingServiceProviderRoleUseCase,
    private val getBuildingServiceProvidersUseCase: GetBuildingServiceProvidersUseCase,
    private val createServiceRecordUseCase: CreateServiceRecordUseCase,
    private val updateServiceRecordStatusUseCase: UpdateServiceRecordStatusUseCase,
    private val getBuildingServiceRecordsUseCase: GetBuildingServiceRecordsUseCase,
    private val createMaintenanceRecordUseCase: CreateMaintenanceRecordUseCase,
    private val getBuildingMaintenanceRecordsUseCase: GetBuildingMaintenanceRecordsUseCase,
    private val createInvoiceUseCase: CreateInvoiceUseCase,
    private val getBuildingInvoicesUseCase: GetBuildingInvoicesUseCase,
    private val recordBuildingPaymentUseCase: RecordBuildingPaymentUseCase,
    private val getBuildingPaymentsUseCase: GetBuildingPaymentsUseCase,
    private val createBuildingBillUseCase: CreateBuildingBillUseCase,
    private val updateBuildingBillPaymentUseCase: UpdateBuildingBillPaymentUseCase,
    private val getBuildingBillsUseCase: GetBuildingBillsUseCase,
    private val getServiceDashboardSummaryUseCase: GetServiceDashboardSummaryUseCase,
    private val getBuildingServiceArchiveUseCase: GetBuildingServiceArchiveUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ServicesUiState())
    val uiState: StateFlow<ServicesUiState> = _uiState.asStateFlow()

    init {
        loadAllData()
    }

    fun selectTab(tab: ServiceTab) {
        _uiState.update { it.copy(selectedTab = tab, errorMessage = null) }
        if (tab == ServiceTab.ARCHIVE) {
            loadArchive()
        }
    }

    fun loadAllData() {
        loadSummary()
        loadProviders()
        loadServiceRecords()
        loadMaintenanceRecords()
        loadInvoices()
        loadPayments()
        loadBills()
    }

    fun loadSummary() {
        viewModelScope.launch {
            try {
                val summary = getServiceDashboardSummaryUseCase(currentUserId, buildingId)
                _uiState.update { it.copy(summary = summary) }
            } catch (e: Exception) {
                // Non-critical summary error
            }
        }
    }

    fun loadProviders() {
        viewModelScope.launch {
            getBuildingServiceProvidersUseCase(currentUserId, buildingId)
                .catch { e -> _uiState.update { it.copy(errorMessage = e.message) } }
                .collect { list ->
                    _uiState.update { it.copy(providers = list, isLoading = false) }
                }
        }
    }

    fun loadServiceRecords() {
        viewModelScope.launch {
            getBuildingServiceRecordsUseCase(currentUserId, buildingId)
                .catch { e -> _uiState.update { it.copy(errorMessage = e.message) } }
                .collect { list ->
                    _uiState.update { it.copy(serviceRecords = list) }
                }
        }
    }

    fun loadMaintenanceRecords() {
        viewModelScope.launch {
            getBuildingMaintenanceRecordsUseCase(currentUserId, buildingId)
                .catch { e -> _uiState.update { it.copy(errorMessage = e.message) } }
                .collect { list ->
                    _uiState.update { it.copy(maintenanceRecords = list) }
                }
        }
    }

    fun loadInvoices() {
        viewModelScope.launch {
            getBuildingInvoicesUseCase(currentUserId, buildingId)
                .catch { e -> _uiState.update { it.copy(errorMessage = e.message) } }
                .collect { list ->
                    _uiState.update { it.copy(invoices = list) }
                }
        }
    }

    fun loadPayments() {
        viewModelScope.launch {
            getBuildingPaymentsUseCase(currentUserId, buildingId)
                .catch { e -> _uiState.update { it.copy(errorMessage = e.message) } }
                .collect { list ->
                    _uiState.update { it.copy(payments = list) }
                }
        }
    }

    fun loadBills() {
        viewModelScope.launch {
            getBuildingBillsUseCase(currentUserId, buildingId)
                .catch { e -> _uiState.update { it.copy(errorMessage = e.message) } }
                .collect { list ->
                    _uiState.update { it.copy(bills = list) }
                }
        }
    }

    fun loadArchive() {
        viewModelScope.launch {
            val res = getBuildingServiceArchiveUseCase(currentUserId, buildingId)
            if (res.isSuccess) {
                _uiState.update { it.copy(archive = res.getOrNull()) }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }

    // Dialog handlers
    fun openAddProviderDialog() = _uiState.update { it.copy(isAddProviderDialogOpen = true) }
    fun closeAddProviderDialog() = _uiState.update { it.copy(isAddProviderDialogOpen = false) }

    fun openAddServiceRecordDialog() = _uiState.update { it.copy(isAddServiceRecordDialogOpen = true) }
    fun closeAddServiceRecordDialog() = _uiState.update { it.copy(isAddServiceRecordDialogOpen = false) }

    fun openAddMaintenanceDialog() = _uiState.update { it.copy(isAddMaintenanceDialogOpen = true) }
    fun closeAddMaintenanceDialog() = _uiState.update { it.copy(isAddMaintenanceDialogOpen = false) }

    fun openAddInvoiceDialog() = _uiState.update { it.copy(isAddInvoiceDialogOpen = true) }
    fun closeAddInvoiceDialog() = _uiState.update { it.copy(isAddInvoiceDialogOpen = false) }

    fun openAddPaymentDialog(invoice: Invoice? = null) = _uiState.update {
        it.copy(isAddPaymentDialogOpen = true, selectedInvoiceForPayment = invoice)
    }
    fun closeAddPaymentDialog() = _uiState.update {
        it.copy(isAddPaymentDialogOpen = false, selectedInvoiceForPayment = null)
    }

    fun openAddBillDialog() = _uiState.update { it.copy(isAddBillDialogOpen = true) }
    fun closeAddBillDialog() = _uiState.update { it.copy(isAddBillDialogOpen = false) }

    fun openStatusUpdateDialog(record: ServiceRecord) = _uiState.update {
        it.copy(selectedServiceRecordForStatusUpdate = record)
    }
    fun closeStatusUpdateDialog() = _uiState.update {
        it.copy(selectedServiceRecordForStatusUpdate = null)
    }

    fun setServiceStatusFilter(status: ServiceRecordStatus?) {
        _uiState.update { it.copy(serviceStatusFilter = status) }
    }

    fun setInvoiceStatusFilter(status: InvoiceStatus?) {
        _uiState.update { it.copy(invoiceStatusFilter = status) }
    }

    fun setBillTypeFilter(type: BillType?) {
        _uiState.update { it.copy(billTypeFilter = type) }
    }

    fun setArchiveQuery(query: String) {
        _uiState.update { it.copy(archiveQuery = query) }
    }

    // Actions
    fun createProviderAndLink(
        name: String,
        phone: String,
        companyName: String?,
        category: ProviderCategory,
        notes: String?,
        roleInBuilding: ProviderRoleInBuilding
    ) {
        viewModelScope.launch {
            val providerRes = createServiceProviderUseCase(
                userId = currentUserId,
                buildingId = buildingId,
                name = name,
                phoneNumber = phone,
                companyName = companyName,
                category = category,
                notes = notes
            )
            if (providerRes.isSuccess) {
                val provider = providerRes.getOrNull()!!
                val linkRes = linkServiceProviderToBuildingUseCase(
                    userId = currentUserId,
                    buildingId = buildingId,
                    providerId = provider.id,
                    roleInBuilding = roleInBuilding,
                    category = category,
                    status = ProviderStatus.ACTIVE
                )
                if (linkRes.isSuccess) {
                    _uiState.update {
                        it.copy(
                            isAddProviderDialogOpen = false,
                            successMessage = "سرویسکار/تأمین‌کننده با موفقیت افزوده و متصل گردید"
                        )
                    }
                    loadSummary()
                } else {
                    _uiState.update { it.copy(errorMessage = linkRes.exceptionOrNull()?.message) }
                }
            } else {
                _uiState.update { it.copy(errorMessage = providerRes.exceptionOrNull()?.message) }
            }
        }
    }

    fun updateProviderBuildingRole(
        providerId: String,
        newRole: ProviderRoleInBuilding,
        newStatus: ProviderStatus,
        newCategory: ProviderCategory
    ) {
        viewModelScope.launch {
            val res = updateBuildingServiceProviderRoleUseCase(
                userId = currentUserId,
                buildingId = buildingId,
                providerId = providerId,
                roleInBuilding = newRole,
                status = newStatus,
                category = newCategory
            )
            if (res.isSuccess) {
                _uiState.update {
                    it.copy(successMessage = "نقش و وضعیت سرویسکار در ساختمان به‌روزرسانی شد")
                }
                loadSummary()
            } else {
                _uiState.update { it.copy(errorMessage = res.exceptionOrNull()?.message) }
            }
        }
    }

    fun createServiceRecord(
        type: ServiceType,
        category: ProviderCategory,
        providerId: String?,
        title: String,
        description: String,
        priority: ServicePriority,
        scheduledDate: Long?
    ) {
        viewModelScope.launch {
            val res = createServiceRecordUseCase(
                userId = currentUserId,
                buildingId = buildingId,
                type = type,
                category = category,
                providerId = providerId,
                title = title,
                description = description,
                priority = priority,
                scheduledDate = scheduledDate
            )
            if (res.isSuccess) {
                _uiState.update {
                    it.copy(
                        isAddServiceRecordDialogOpen = false,
                        successMessage = "درخواست/سفارش خدمت با موفقیت ثبت شد"
                    )
                }
                loadSummary()
            } else {
                _uiState.update { it.copy(errorMessage = res.exceptionOrNull()?.message) }
            }
        }
    }

    fun updateServiceRecordStatus(
        recordId: String,
        newStatus: ServiceRecordStatus,
        costEstimate: Long?,
        actualCost: Long?,
        resolutionNotes: String?
    ) {
        viewModelScope.launch {
            val res = updateServiceRecordStatusUseCase(
                userId = currentUserId,
                serviceRecordId = recordId,
                newStatus = newStatus,
                costEstimate = costEstimate,
                actualCost = actualCost,
                resolutionNotes = resolutionNotes
            )
            if (res.isSuccess) {
                _uiState.update {
                    it.copy(
                        selectedServiceRecordForStatusUpdate = null,
                        successMessage = "وضعیت خدمت به‌روزرسانی شد"
                    )
                }
                loadSummary()
            } else {
                _uiState.update { it.copy(errorMessage = res.exceptionOrNull()?.message) }
            }
        }
    }

    fun createMaintenanceRecord(
        equipmentName: String,
        serviceType: String,
        maintenanceDate: Long,
        nextScheduledDate: Long?,
        performedBy: String,
        cost: Long,
        partsReplaced: String?,
        checklistNotes: String?
    ) {
        viewModelScope.launch {
            val res = createMaintenanceRecordUseCase(
                userId = currentUserId,
                buildingId = buildingId,
                equipmentName = equipmentName,
                serviceType = serviceType,
                maintenanceDate = maintenanceDate,
                nextScheduledDate = nextScheduledDate,
                performedBy = performedBy,
                cost = cost,
                partsReplaced = partsReplaced,
                checklistNotes = checklistNotes
            )
            if (res.isSuccess) {
                _uiState.update {
                    it.copy(
                        isAddMaintenanceDialogOpen = false,
                        successMessage = "سابقه سرویس و نگهداری با موفقیت ثبت شد"
                    )
                }
                loadSummary()
            } else {
                _uiState.update { it.copy(errorMessage = res.exceptionOrNull()?.message) }
            }
        }
    }

    fun createInvoice(
        invoiceNumber: String,
        providerId: String?,
        serviceRecordId: String?,
        issueDate: Long,
        dueDate: Long,
        totalAmount: Long,
        description: String?
    ) {
        viewModelScope.launch {
            val res = createInvoiceUseCase(
                userId = currentUserId,
                buildingId = buildingId,
                invoiceNumber = invoiceNumber,
                providerId = providerId,
                serviceRecordId = serviceRecordId,
                issueDate = issueDate,
                dueDate = dueDate,
                totalAmount = totalAmount,
                description = description
            )
            if (res.isSuccess) {
                _uiState.update {
                    it.copy(
                        isAddInvoiceDialogOpen = false,
                        successMessage = "فاکتور خدمت با موفقیت ایجاد شد"
                    )
                }
                loadSummary()
            } else {
                _uiState.update { it.copy(errorMessage = res.exceptionOrNull()?.message) }
            }
        }
    }

    fun recordPayment(
        invoiceId: String,
        amount: Long,
        paymentDate: Long,
        paymentMethod: PaymentMethod,
        referenceNumber: String?,
        paidBy: String,
        notes: String?
    ) {
        viewModelScope.launch {
            val res = recordBuildingPaymentUseCase(
                userId = currentUserId,
                buildingId = buildingId,
                invoiceId = invoiceId,
                amount = amount,
                paymentDate = paymentDate,
                paymentMethod = paymentMethod,
                referenceNumber = referenceNumber,
                paidBy = paidBy,
                notes = notes
            )
            if (res.isSuccess) {
                _uiState.update {
                    it.copy(
                        isAddPaymentDialogOpen = false,
                        selectedInvoiceForPayment = null,
                        successMessage = "پرداخت فاکتور ثبت شد و وضعیت تسویه به‌روز گردید"
                    )
                }
                loadSummary()
                loadInvoices()
                loadPayments()
            } else {
                _uiState.update { it.copy(errorMessage = res.exceptionOrNull()?.message) }
            }
        }
    }

    fun createBuildingBill(
        billType: BillType,
        billIdentifier: String,
        periodStartDate: Long,
        periodEndDate: Long,
        dueDate: Long,
        amount: Long,
        description: String?
    ) {
        viewModelScope.launch {
            val res = createBuildingBillUseCase(
                userId = currentUserId,
                buildingId = buildingId,
                billType = billType,
                billIdentifier = billIdentifier,
                periodStartDate = periodStartDate,
                periodEndDate = periodEndDate,
                dueDate = dueDate,
                amount = amount,
                description = description
            )
            if (res.isSuccess) {
                _uiState.update {
                    it.copy(
                        isAddBillDialogOpen = false,
                        successMessage = "قبض عمومی ساختمان با موفقیت ثبت شد"
                    )
                }
                loadSummary()
            } else {
                _uiState.update { it.copy(errorMessage = res.exceptionOrNull()?.message) }
            }
        }
    }

    fun updateBillPayment(
        billId: String,
        isPaid: Boolean,
        paymentDate: Long?,
        paymentReference: String?
    ) {
        viewModelScope.launch {
            val res = updateBuildingBillPaymentUseCase(
                userId = currentUserId,
                buildingId = buildingId,
                billId = billId,
                isPaid = isPaid,
                paymentDate = paymentDate,
                paymentReference = paymentReference,
                receiptDocumentId = null
            )
            if (res.isSuccess) {
                _uiState.update {
                    it.copy(
                        successMessage = if (isPaid) "قبض به عنوان پرداخت شده ثبت شد" else "وضعیت قبض بازنشانی شد"
                    )
                }
                loadSummary()
            } else {
                _uiState.update { it.copy(errorMessage = res.exceptionOrNull()?.message) }
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
                return ServicesViewModel(
                    buildingId = buildingId,
                    currentUserId = currentUserId,
                    createServiceProviderUseCase = appContainer.createServiceProviderUseCase,
                    linkServiceProviderToBuildingUseCase = appContainer.linkServiceProviderToBuildingUseCase,
                    updateBuildingServiceProviderRoleUseCase = appContainer.updateBuildingServiceProviderRoleUseCase,
                    getBuildingServiceProvidersUseCase = appContainer.getBuildingServiceProvidersUseCase,
                    createServiceRecordUseCase = appContainer.createServiceRecordUseCase,
                    updateServiceRecordStatusUseCase = appContainer.updateServiceRecordStatusUseCase,
                    getBuildingServiceRecordsUseCase = appContainer.getBuildingServiceRecordsUseCase,
                    createMaintenanceRecordUseCase = appContainer.createMaintenanceRecordUseCase,
                    getBuildingMaintenanceRecordsUseCase = appContainer.getBuildingMaintenanceRecordsUseCase,
                    createInvoiceUseCase = appContainer.createInvoiceUseCase,
                    getBuildingInvoicesUseCase = appContainer.getBuildingInvoicesUseCase,
                    recordBuildingPaymentUseCase = appContainer.recordBuildingPaymentUseCase,
                    getBuildingPaymentsUseCase = appContainer.getBuildingPaymentsUseCase,
                    createBuildingBillUseCase = appContainer.createBuildingBillUseCase,
                    updateBuildingBillPaymentUseCase = appContainer.updateBuildingBillPaymentUseCase,
                    getBuildingBillsUseCase = appContainer.getBuildingBillsUseCase,
                    getServiceDashboardSummaryUseCase = appContainer.getServiceDashboardSummaryUseCase,
                    getBuildingServiceArchiveUseCase = appContainer.getBuildingServiceArchiveUseCase
                ) as T
            }
        }
    }
}
