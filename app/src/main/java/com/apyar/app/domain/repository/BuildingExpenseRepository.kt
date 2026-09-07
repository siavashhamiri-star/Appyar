package com.apyar.app.domain.repository

import com.apyar.app.domain.model.BuildingExpense
import kotlinx.coroutines.flow.Flow

interface BuildingExpenseRepository {
    suspend fun insertExpense(expense: BuildingExpense)
    suspend fun updateExpense(expense: BuildingExpense)
    suspend fun deleteExpense(id: String)
    suspend fun getExpenseById(id: String): BuildingExpense?
    suspend fun getExpensesByBuildingId(buildingId: String): List<BuildingExpense>
    suspend fun getExpensesByChargePeriodId(chargePeriodId: String): List<BuildingExpense>
    fun observeExpensesByBuildingId(buildingId: String): Flow<List<BuildingExpense>>
    fun observeExpensesByChargePeriodId(chargePeriodId: String): Flow<List<BuildingExpense>>
}
