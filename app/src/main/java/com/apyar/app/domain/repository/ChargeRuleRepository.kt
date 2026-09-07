package com.apyar.app.domain.repository

import com.apyar.app.domain.model.ChargeRule
import kotlinx.coroutines.flow.Flow

interface ChargeRuleRepository {
    suspend fun insertChargeRule(rule: ChargeRule)
    suspend fun updateChargeRule(rule: ChargeRule)
    suspend fun getChargeRuleById(id: String): ChargeRule?
    suspend fun getChargeRulesByBuildingId(buildingId: String): List<ChargeRule>
    fun observeChargeRulesByBuildingId(buildingId: String): Flow<List<ChargeRule>>
    suspend fun getActiveRuleForBuilding(buildingId: String): ChargeRule?
}
