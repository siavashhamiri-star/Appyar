package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.AuditEvent
import com.apyar.app.domain.model.ChargeCalculationMethod
import com.apyar.app.domain.model.FormulaComponent
import com.apyar.app.domain.model.MethodType
import com.apyar.app.domain.model.Permission
import com.apyar.app.domain.repository.AuditRepository
import com.apyar.app.domain.repository.BuildingRepository
import com.apyar.app.domain.repository.ChargeCalculationMethodRepository
import java.util.UUID

class CreateCalculationMethodUseCase(
    private val calculationMethodRepository: ChargeCalculationMethodRepository,
    private val buildingRepository: BuildingRepository? = null,
    private val checkPermissionUseCase: CheckPermissionUseCase? = null,
    private val auditRepository: AuditRepository? = null
) {
    suspend operator fun invoke(
        buildingId: String,
        name: String,
        description: String? = null,
        methodType: MethodType = MethodType.EQUAL,
        isDefault: Boolean = false,
        isActive: Boolean = true,
        createdBy: String,
        components: List<FormulaComponent> = emptyList()
    ): Result<ChargeCalculationMethod> {
        if (name.isBlank()) {
            return Result.failure(IllegalArgumentException("نام روش محاسبه نمی‌تواند خالی باشد"))
        }
        if (buildingId.isBlank()) {
            return Result.failure(IllegalArgumentException("شناسه ساختمان معتبر نیست"))
        }
        if (createdBy.isBlank()) {
            return Result.failure(IllegalArgumentException("ثبت‌کننده روش محاسبه مشخص نشده است"))
        }

        if (buildingRepository != null) {
            val building = buildingRepository.getBuildingById(buildingId)
            if (building == null) {
                return Result.failure(IllegalArgumentException("ساختمان مورد نظر یافت نشد"))
            }
        }

        // Validate that no component contains unsafe executable script syntax
        for (component in components) {
            for ((key, value) in component.configuration) {
                if (containsScriptInjection(value) || containsScriptInjection(key)) {
                    return Result.failure(IllegalArgumentException("فرمول اختصاصی شامل دستورات یا اسکریپت‌های غیرمجاز است"))
                }
            }
        }

        if (checkPermissionUseCase != null) {
            val permCheck = checkPermissionUseCase(createdBy, buildingId, Permission.MANAGE_FINANCIAL_DATA)
            if (permCheck.isFailure) {
                return Result.failure(permCheck.exceptionOrNull()!!)
            }
        }

        val methodId = UUID.randomUUID().toString()
        val boundComponents = components.map { comp ->
            comp.copy(
                id = if (comp.id.isBlank()) UUID.randomUUID().toString() else comp.id,
                calculationMethodId = methodId
            )
        }

        // If this new method is default, ensure other methods of this building are not default
        if (isDefault) {
            val existing = calculationMethodRepository.getCalculationMethodsByBuildingId(buildingId)
            existing.filter { it.isDefault }.forEach {
                calculationMethodRepository.updateCalculationMethod(it.copy(isDefault = false))
            }
        }

        val method = ChargeCalculationMethod(
            id = methodId,
            buildingId = buildingId,
            name = name.trim(),
            description = description?.trim(),
            methodType = methodType,
            isDefault = isDefault,
            isActive = isActive,
            createdAt = System.currentTimeMillis(),
            createdBy = createdBy,
            components = boundComponents
        )

        calculationMethodRepository.insertCalculationMethod(method)

        auditRepository?.logEvent(
            AuditEvent(
                id = UUID.randomUUID().toString(),
                buildingId = buildingId,
                actorUserId = createdBy,
                action = "CREATE_CALCULATION_METHOD",
                details = "ایجاد روش محاسبه شارژ: $name (${methodType.titleFa})",
                targetEntity = "ChargeCalculationMethod",
                targetEntityId = method.id
            )
        )

        return Result.success(method)
    }

    private fun containsScriptInjection(input: String): Boolean {
        val lower = input.lowercase()
        return lower.contains("<script") ||
                lower.contains("eval(") ||
                lower.contains("function(") ||
                lower.contains("javascript:") ||
                lower.contains("system.") ||
                lower.contains("runtime.") ||
                lower.contains("process.")
    }
}
