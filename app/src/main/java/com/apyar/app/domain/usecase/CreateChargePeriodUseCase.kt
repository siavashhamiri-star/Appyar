package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.AuditEvent
import com.apyar.app.domain.model.ChargePeriod
import com.apyar.app.domain.model.ChargePeriodStatus
import com.apyar.app.domain.model.Permission
import com.apyar.app.domain.repository.AuditRepository
import com.apyar.app.domain.repository.ChargeCalculationMethodRepository
import com.apyar.app.domain.repository.ChargePeriodRepository
import com.apyar.app.domain.repository.ChargeRuleRepository
import java.util.UUID

class CreateChargePeriodUseCase(
    private val chargePeriodRepository: ChargePeriodRepository,
    private val chargeRuleRepository: ChargeRuleRepository? = null,
    private val checkPermissionUseCase: CheckPermissionUseCase? = null,
    private val auditRepository: AuditRepository? = null,
    private val calculationMethodRepository: ChargeCalculationMethodRepository? = null
) {
    /**
     * Stage 7.1 Primary Signature
     */
    suspend operator fun invoke(
        buildingId: String,
        title: String,
        periodStart: Long,
        periodEnd: Long,
        dueDate: Long = periodEnd,
        calculationMethodId: String = "",
        totalBuildingCost: Long = 0L,
        createdBy: String
    ): Result<ChargePeriod> {
        if (title.isBlank()) {
            return Result.failure(IllegalArgumentException("عنوان دوره شارژ نمی‌تواند خالی باشد"))
        }
        if (buildingId.isBlank()) {
            return Result.failure(IllegalArgumentException("شناسه ساختمان معتبر نیست"))
        }
        if (createdBy.isBlank()) {
            return Result.failure(IllegalArgumentException("ثبت‌کننده دوره مشخص نشده است"))
        }
        if (periodStart > periodEnd) {
            return Result.failure(IllegalArgumentException("تاریخ شروع دوره نمی‌تواند بعد از تاریخ پایان باشد"))
        }
        if (dueDate < periodStart) {
            return Result.failure(IllegalArgumentException("مهلت پرداخت نمی‌تواند قبل از شروع دوره باشد"))
        }
        if (totalBuildingCost < 0L) {
            return Result.failure(IllegalArgumentException("هزینه ساختمان نمی‌تواند منفی باشد"))
        }

        // Check calculationMethodId if specified and repository available
        if (calculationMethodId.isNotBlank() && calculationMethodRepository != null) {
            val method = calculationMethodRepository.getCalculationMethodById(calculationMethodId)
            if (method != null && method.buildingId != buildingId) {
                return Result.failure(IllegalArgumentException("روش محاسبه متعلق به ساختمان دیگری است"))
            }
        }

        // Permission check if use case is provided
        if (checkPermissionUseCase != null) {
            val permissionResult = checkPermissionUseCase(createdBy, buildingId, Permission.MANAGE_FINANCIAL_DATA)
            if (permissionResult.isFailure) {
                return Result.failure(permissionResult.exceptionOrNull()!!)
            }
        }

        val period = ChargePeriod(
            id = UUID.randomUUID().toString(),
            buildingId = buildingId,
            title = title.trim(),
            periodStart = periodStart,
            periodEnd = periodEnd,
            dueDate = dueDate,
            status = ChargePeriodStatus.DRAFT,
            calculationMethodId = calculationMethodId,
            totalBuildingCost = totalBuildingCost,
            createdAt = System.currentTimeMillis(),
            createdBy = createdBy,
            finalizedAt = null
        )

        chargePeriodRepository.insertChargePeriod(period)

        auditRepository?.logEvent(
            AuditEvent(
                id = UUID.randomUUID().toString(),
                buildingId = buildingId,
                actorUserId = createdBy,
                action = "CREATE_CHARGE_PERIOD",
                details = "ایجاد دوره شارژ: $title (${period.id})",
                targetEntity = "ChargePeriod",
                targetEntityId = period.id
            )
        )

        return Result.success(period)
    }

    /**
     * Stage 3 Backwards-compatible signature
     */
    suspend operator fun invoke(
        userId: String,
        buildingId: String,
        title: String,
        startDate: Long,
        endDate: Long,
        totalAmount: Long,
        chargeRuleId: String
    ): Result<ChargePeriod> {
        if (totalAmount <= 0L) {
            return Result.failure(IllegalArgumentException("مبلغ کل دوره باید بیشتر از صفر باشد"))
        }
        val rule = chargeRuleRepository?.getChargeRuleById(chargeRuleId)
        val formulaVer = rule?.version ?: 1

        val result = invoke(
            buildingId = buildingId,
            title = title,
            periodStart = startDate,
            periodEnd = endDate,
            dueDate = endDate,
            calculationMethodId = chargeRuleId,
            totalBuildingCost = totalAmount,
            createdBy = userId
        )

        return result.map { it.copy(formulaVersion = formulaVer) }
    }
}

