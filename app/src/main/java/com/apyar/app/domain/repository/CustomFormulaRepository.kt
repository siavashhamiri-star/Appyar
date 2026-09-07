package com.apyar.app.domain.repository

import com.apyar.app.domain.model.CustomFormula
import kotlinx.coroutines.flow.Flow

interface CustomFormulaRepository {
    suspend fun insertCustomFormula(formula: CustomFormula)
    suspend fun getCustomFormulaById(id: String): CustomFormula?
    suspend fun getCustomFormulasByBuildingId(buildingId: String): List<CustomFormula>
    fun observeCustomFormulasByBuildingId(buildingId: String): Flow<List<CustomFormula>>
}
