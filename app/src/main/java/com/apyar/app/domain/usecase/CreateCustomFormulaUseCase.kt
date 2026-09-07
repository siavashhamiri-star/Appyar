package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.AuditEvent
import com.apyar.app.domain.model.CustomFormula
import com.apyar.app.domain.model.Permission
import com.apyar.app.domain.repository.AuditRepository
import com.apyar.app.domain.repository.CustomFormulaRepository
import java.util.UUID

class CreateCustomFormulaUseCase(
    private val customFormulaRepository: CustomFormulaRepository,
    private val checkPermissionUseCase: CheckPermissionUseCase,
    private val auditRepository: AuditRepository
) {
    suspend operator fun invoke(
        userId: String,
        buildingId: String,
        name: String,
        description: String,
        definition: String
    ): Result<CustomFormula> {
        val permissionResult = checkPermissionUseCase(userId, buildingId, Permission.MANAGE_FINANCIAL_DATA)
        if (permissionResult.isFailure) {
            return Result.failure(permissionResult.exceptionOrNull()!!)
        }

        val existing = customFormulaRepository.getCustomFormulasByBuildingId(buildingId)
        val nextVersion = (existing.maxOfOrNull { it.version } ?: 0) + 1

        val formula = CustomFormula(
            id = UUID.randomUUID().toString(),
            buildingId = buildingId,
            name = name,
            description = description,
            definition = definition,
            version = nextVersion,
            approvedAt = System.currentTimeMillis(),
            approvedBy = userId,
            effectiveFrom = System.currentTimeMillis(),
            isActive = true
        )

        customFormulaRepository.insertCustomFormula(formula)

        auditRepository.logEvent(
            AuditEvent(
                id = UUID.randomUUID().toString(),
                buildingId = buildingId,
                actorUserId = userId,
                action = "CREATE_CUSTOM_FORMULA",
                details = "ثبت فرمول اختصاصی مصوب ساختمان: $name (نسخه $nextVersion)",
                targetEntity = "CustomFormula",
                targetEntityId = formula.id
            )
        )

        return Result.success(formula)
    }
}
