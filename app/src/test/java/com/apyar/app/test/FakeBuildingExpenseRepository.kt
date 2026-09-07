package com.apyar.app.test

import com.apyar.app.domain.model.BuildingExpense
import com.apyar.app.domain.repository.BuildingExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeBuildingExpenseRepository : BuildingExpenseRepository {
    private val expenses = MutableStateFlow<List<BuildingExpense>>(emptyList())

    override suspend fun insertExpense(expense: BuildingExpense) {
        expenses.value = expenses.value.filterNot { it.id == expense.id } + expense
    }

    override suspend fun updateExpense(expense: BuildingExpense) {
        expenses.value = expenses.value.map { if (it.id == expense.id) expense else it }
    }

    override suspend fun deleteExpense(id: String) {
        expenses.value = expenses.value.filterNot { it.id == id }
    }

    override suspend fun getExpenseById(id: String): BuildingExpense? {
        return expenses.value.find { it.id == id }
    }

    override suspend fun getExpensesByBuildingId(buildingId: String): List<BuildingExpense> {
        return expenses.value.filter { it.buildingId == buildingId }
    }

    override suspend fun getExpensesByChargePeriodId(chargePeriodId: String): List<BuildingExpense> {
        return expenses.value.filter { it.chargePeriodId == chargePeriodId }
    }

    override fun observeExpensesByBuildingId(buildingId: String): Flow<List<BuildingExpense>> {
        return expenses.map { list -> list.filter { it.buildingId == buildingId } }
    }

    override fun observeExpensesByChargePeriodId(chargePeriodId: String): Flow<List<BuildingExpense>> {
        return expenses.map { list -> list.filter { it.chargePeriodId == chargePeriodId } }
    }
}
