package com.apyar.app.presentation.financial

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.apyar.app.core.di.AppContainer
import com.apyar.app.domain.model.BuildingFinancialAccount
import com.apyar.app.domain.model.ChargePeriod
import com.apyar.app.domain.model.ExpenseCategory
import com.apyar.app.domain.model.FinancialSummary
import com.apyar.app.domain.model.FinancialTransaction
import com.apyar.app.domain.model.TransactionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FinancialDashboardUiState(
    val isLoading: Boolean = true,
    val buildingId: String = "",
    val currentUserId: String = "user-1",
    val account: BuildingFinancialAccount? = null,
    val summary: FinancialSummary? = null,
    val recentTransactions: List<FinancialTransaction> = emptyList(),
    val chargePeriods: List<ChargePeriod> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null
)

class FinancialDashboardViewModel(
    private val appContainer: AppContainer,
    private val buildingId: String,
    private val currentUserId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        FinancialDashboardUiState(buildingId = buildingId, currentUserId = currentUserId)
    )
    val uiState: StateFlow<FinancialDashboardUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                // Ensure account exists or load it
                val accountResult = appContainer.getFinancialAccountUseCase(currentUserId, buildingId)
                val account = if (accountResult.isSuccess && accountResult.getOrNull() != null) {
                    accountResult.getOrNull()
                } else {
                    // Create default account if not exists
                    val createResult = appContainer.createFinancialAccountUseCase(
                        userId = currentUserId,
                        buildingId = buildingId,
                        accountName = "صندوق مالی ساختمان",
                        currency = "تومان",
                        initialBalance = 0L
                    )
                    createResult.getOrNull()
                }

                val summaryResult = appContainer.getFinancialSummaryUseCase(currentUserId, buildingId)
                val txResult = appContainer.getTransactionsUseCase(currentUserId, buildingId)
                val periodsResult = appContainer.getChargePeriodsUseCase(currentUserId, buildingId)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        account = account,
                        summary = summaryResult.getOrNull(),
                        recentTransactions = txResult.getOrDefault(emptyList()).take(10),
                        chargePeriods = periodsResult.getOrDefault(emptyList()),
                        error = if (summaryResult.isFailure) summaryResult.exceptionOrNull()?.message else null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun recordIncomeExpense(
        type: TransactionType,
        amount: Long,
        description: String,
        category: ExpenseCategory? = null
    ) {
        viewModelScope.launch {
            val accountId = _uiState.value.account?.id ?: return@launch
            val result = appContainer.recordTransactionUseCase(
                userId = currentUserId,
                buildingId = buildingId,
                accountId = accountId,
                unitId = null,
                type = type,
                amount = amount,
                description = description,
                category = category
            )

            if (result.isSuccess) {
                _uiState.update { it.copy(successMessage = "تراکنش مالی با موفقیت در دفتر کل ثبت شد") }
                loadData()
            } else {
                _uiState.update { it.copy(error = result.exceptionOrNull()?.message) }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(error = null, successMessage = null) }
    }

    companion object {
        fun provideFactory(
            appContainer: AppContainer,
            buildingId: String,
            currentUserId: String
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return FinancialDashboardViewModel(appContainer, buildingId, currentUserId) as T
            }
        }
    }
}
