package com.apyar.app.data.repository

import com.apyar.app.data.local.dao.CustomFormulaDao
import com.apyar.app.data.mapper.toDomain
import com.apyar.app.data.mapper.toEntity
import com.apyar.app.domain.model.CustomFormula
import com.apyar.app.domain.repository.CustomFormulaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CustomFormulaRepositoryImpl(
    private val customFormulaDao: CustomFormulaDao
) : CustomFormulaRepository {

    override suspend fun insertCustomFormula(formula: CustomFormula) {
        customFormulaDao.insertCustomFormula(formula.toEntity())
    }

    override suspend fun getCustomFormulaById(id: String): CustomFormula? {
        return customFormulaDao.getCustomFormulaById(id)?.toDomain()
    }

    override suspend fun getCustomFormulasByBuildingId(buildingId: String): List<CustomFormula> {
        return customFormulaDao.getCustomFormulasByBuildingId(buildingId).map { it.toDomain() }
    }

    override fun observeCustomFormulasByBuildingId(buildingId: String): Flow<List<CustomFormula>> {
        return customFormulaDao.observeCustomFormulasByBuildingId(buildingId).map { list ->
            list.map { it.toDomain() }
        }
    }
}
