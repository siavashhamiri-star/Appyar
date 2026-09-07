package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.ChargeRule
import com.apyar.app.domain.model.Permission
import com.apyar.app.domain.repository.ChargeRuleRepository
import kotlinx.coroutines.flow.Flow

class GetChargeRulesUseCase(
    private val chargeRuleRepository: ChargeRuleRepository,
    private val checkPermissionUseCase: CheckPermissionUseCase
) {
    suspend operator fun invoke(
        userId: String,
        buildingId: String
    ): Result<List<ChargeRule>> {
        val permissionResult = checkPermissionUseCase(userId, buildingId, Permission.VIEW_FINANCIAL_DATA)
        if (permissionResult.isFailure) {
            return Result.failure(permissionResult.exceptionOrNull()!!)
        }

        val rules = chargeRuleRepository.getChargeRulesByBuildingId(buildingId)
        return Result.success(rules)
    }

    fun observe(
        userId: String,
        buildingId: String
    ): Flow<List<ChargeRule>> {
        return chargeRuleRepository.observeChargeRulesByBuildingId(buildingId)
    }
}
