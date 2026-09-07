package com.apyar.app.data.repository

import com.apyar.app.data.local.dao.ChargeRuleDao
import com.apyar.app.data.mapper.toDomain
import com.apyar.app.data.mapper.toEntity
import com.apyar.app.domain.model.ChargeRule
import com.apyar.app.domain.repository.ChargeRuleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ChargeRuleRepositoryImpl(
    private val chargeRuleDao: ChargeRuleDao
) : ChargeRuleRepository {

    override suspend fun insertChargeRule(rule: ChargeRule) {
        chargeRuleDao.insertChargeRule(rule.toEntity())
    }

    override suspend fun updateChargeRule(rule: ChargeRule) {
        chargeRuleDao.updateChargeRule(rule.toEntity())
    }

    override suspend fun getChargeRuleById(id: String): ChargeRule? {
        return chargeRuleDao.getChargeRuleById(id)?.toDomain()
    }

    override suspend fun getChargeRulesByBuildingId(buildingId: String): List<ChargeRule> {
        return chargeRuleDao.getChargeRulesByBuildingId(buildingId).map { it.toDomain() }
    }

    override fun observeChargeRulesByBuildingId(buildingId: String): Flow<List<ChargeRule>> {
        return chargeRuleDao.observeChargeRulesByBuildingId(buildingId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getActiveRuleForBuilding(buildingId: String): ChargeRule? {
        return chargeRuleDao.getActiveRuleForBuilding(buildingId)?.toDomain()
    }
}
