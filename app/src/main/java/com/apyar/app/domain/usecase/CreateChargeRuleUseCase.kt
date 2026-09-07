package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.AuditEvent
import com.apyar.app.domain.model.CalculationType
import com.apyar.app.domain.model.ChargeRule
import com.apyar.app.domain.model.Permission
import com.apyar.app.domain.repository.AuditRepository
import com.apyar.app.domain.repository.ChargeRuleRepository
import java.util.UUID

class CreateChargeRuleUseCase(
    private val chargeRuleRepository: ChargeRuleRepository,
    private val checkPermissionUseCase: CheckPermissionUseCase,
    private val auditRepository: AuditRepository
) {
    suspend operator fun invoke(
        userId: String,
        buildingId: String,
        name: String,
        description: String,
        calculationType: CalculationType,
        formulaDefinition: String = ""
    ): Result<ChargeRule> {
        val permissionResult = checkPermissionUseCase(userId, buildingId, Permission.MANAGE_FINANCIAL_DATA)
        if (permissionResult.isFailure) {
            return Result.failure(permissionResult.exceptionOrNull()!!)
        }

        val existingRules = chargeRuleRepository.getChargeRulesByBuildingId(buildingId)
        val nextVersion = (existingRules.maxOfOrNull { it.version } ?: 0) + 1

        val rule = ChargeRule(
            id = UUID.randomUUID().toString(),
            buildingId = buildingId,
            name = name,
            description = description,
            calculationType = calculationType,
            formulaDefinition = formulaDefinition,
            version = nextVersion,
            isActive = true,
            startDate = System.currentTimeMillis(),
            endDate = null,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            createdBy = userId
        )

        chargeRuleRepository.insertChargeRule(rule)

        auditRepository.logEvent(
            AuditEvent(
                id = UUID.randomUUID().toString(),
                buildingId = buildingId,
                actorUserId = userId,
                action = "CREATE_CHARGE_RULE",
                details = "تعریف قاعده محاسبه شارژ: $name (نوع: ${calculationType.titleFa} - نسخه: $nextVersion)",
                targetEntity = "ChargeRule",
                targetEntityId = rule.id
            )
        )

        return Result.success(rule)
    }
}
