package com.apyar.app.presentation.financial

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.apyar.app.core.di.AppContainer
import com.apyar.app.domain.model.FinancialTransaction
import com.apyar.app.domain.model.TransactionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TransactionsUiState(
    val isLoading: Boolean = true,
    val buildingId: String = "",
    val currentUserId: String = "user-1",
    val allTransactions: List<FinancialTransaction> = emptyList(),
    val filteredTransactions: List<FinancialTransaction> = emptyList(),
    val selectedFilter: TransactionType? = null,
    val error: String? = null
)

class TransactionsViewModel(
    private val appContainer: AppContainer,
    private val buildingId: String,
    private val currentUserId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        TransactionsUiState(buildingId = buildingId, currentUserId = currentUserId)
    )
    val uiState: StateFlow<TransactionsUiState> = _uiState.asStateFlow()

    init {
        loadTransactions()
    }

    fun loadTransactions() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = appContainer.getTransactionsUseCase(currentUserId, buildingId)
            if (result.isSuccess) {
                val list = result.getOrDefault(emptyList())
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        allTransactions = list,
                        filteredTransactions = filterList(list, state.selectedFilter)
                    )
                }
            } else {
                _uiState.update {
                    it.copy(isLoading = false, error = result.exceptionOrNull()?.message)
                }
            }
        }
    }

    fun setFilter(type: TransactionType?) {
        _uiState.update { state ->
            state.copy(
                selectedFilter = type,
                filteredTransactions = filterList(state.allTransactions, type)
            )
        }
    }

    private fun filterList(
        transactions: List<FinancialTransaction>,
        filter: TransactionType?
    ): List<FinancialTransaction> {
        return if (filter == null) {
            transactions
        } else {
            transactions.filter { it.type == filter }
        }
    }

    companion object {
        fun provideFactory(
            appContainer: AppContainer,
            buildingId: String,
            currentUserId: String
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return TransactionsViewModel(appContainer, buildingId, currentUserId) as T
            }
        }
    }
}
