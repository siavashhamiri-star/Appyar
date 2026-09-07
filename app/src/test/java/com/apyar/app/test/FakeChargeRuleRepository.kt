package com.apyar.app.test

import com.apyar.app.domain.model.ChargeRule
import com.apyar.app.domain.repository.ChargeRuleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class FakeChargeRuleRepository : ChargeRuleRepository {
    private val rules = MutableStateFlow<Map<String, ChargeRule>>(emptyMap())

    override suspend fun insertChargeRule(rule: ChargeRule) {
        rules.value = rules.value + (rule.id to rule)
    }

    override suspend fun updateChargeRule(rule: ChargeRule) {
        rules.value = rules.value + (rule.id to rule)
    }

    override suspend fun getChargeRuleById(id: String): ChargeRule? {
        return rules.value[id]
    }

    override suspend fun getChargeRulesByBuildingId(buildingId: String): List<ChargeRule> {
        return rules.value.values.filter { it.buildingId == buildingId }
    }

    override fun observeChargeRulesByBuildingId(buildingId: String): Flow<List<ChargeRule>> {
        return rules.asStateFlow().map { map ->
            map.values.filter { it.buildingId == buildingId }
        }
    }

    override suspend fun getActiveRuleForBuilding(buildingId: String): ChargeRule? {
        return rules.value.values.find { it.buildingId == buildingId && it.isActive }
    }
}
