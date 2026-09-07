package com.apyar.app.test

import com.apyar.app.domain.model.CustomFormula
import com.apyar.app.domain.repository.CustomFormulaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class FakeCustomFormulaRepository : CustomFormulaRepository {
    private val formulas = MutableStateFlow<Map<String, CustomFormula>>(emptyMap())

    override suspend fun insertCustomFormula(formula: CustomFormula) {
        formulas.value = formulas.value + (formula.id to formula)
    }

    override suspend fun getCustomFormulaById(id: String): CustomFormula? {
        return formulas.value[id]
    }

    override suspend fun getCustomFormulasByBuildingId(buildingId: String): List<CustomFormula> {
        return formulas.value.values.filter { it.buildingId == buildingId }
    }

    override fun observeCustomFormulasByBuildingId(buildingId: String): Flow<List<CustomFormula>> {
        return formulas.asStateFlow().map { map ->
            map.values.filter { it.buildingId == buildingId }
        }
    }
}
