package com.apyar.app.domain.usecase

import com.apyar.app.domain.engine.ChargeCalculationEngine
import com.apyar.app.domain.model.AuditEvent
import com.apyar.app.domain.model.ChargeCalculationMethod
import com.apyar.app.domain.model.ChargeItem
import com.apyar.app.domain.model.ChargePeriodStatus
import com.apyar.app.domain.model.MethodType
import com.apyar.app.domain.model.Permission
import com.apyar.app.domain.repository.AuditRepository
import com.apyar.app.domain.repository.BuildingExpenseRepository
import com.apyar.app.domain.repository.ChargeCalculationMethodRepository
import com.apyar.app.domain.repository.ChargeItemRepository
import com.apyar.app.domain.repository.ChargePeriodRepository
import com.apyar.app.domain.repository.UnitRepository
import java.util.UUID

class CalculateChargesUseCase(
    private val chargePeriodRepository: ChargePeriodRepository,
    private val calculationMethodRepository: ChargeCalculationMethodRepository,
    private val unitRepository: UnitRepository,
    private val chargeItemRepository: ChargeItemRepository,
    private val buildingExpenseRepository: BuildingExpenseRepository? = null,
    private val checkPermissionUseCase: CheckPermissionUseCase? = null,
    private val auditRepository: AuditRepository? = null,
    private val calculationEngine: ChargeCalculationEngine = ChargeCalculationEngine()
) {
    suspend operator fun invoke(
        buildingId: String,
        chargePeriodId: String,
        calculationMethodId: String? = null,
        unitAdjustments: Map<String, Long> = emptyMap(),
        userId: String? = null
    ): Result<List<ChargeItem>> {
        val period = chargePeriodRepository.getChargePeriodById(chargePeriodId)
            ?: return Result.failure(IllegalArgumentException("دوره شارژ مورد نظر یافت نشد"))

        if (period.buildingId != buildingId) {
            return Result.failure(IllegalArgumentException("دوره شارژ به این ساختمان تعلق ندارد"))
        }

        if (period.status == ChargePeriodStatus.FINALIZED || period.status == ChargePeriodStatus.SETTLED) {
            return Result.failure(IllegalStateException("این دوره شارژ قطعی/تسویه شده است و امکان محاسبه مجدد ندارد"))
        }

        if (userId != null && checkPermissionUseCase != null) {
            val permResult = checkPermissionUseCase(userId, buildingId, Permission.MANAGE_FINANCIAL_DATA)
            if (permResult.isFailure) {
                return Result.failure(permResult.exceptionOrNull()!!)
            }
        }

        // Resolve calculation method
        val targetMethodId = calculationMethodId?.takeIf { it.isNotBlank() }
            ?: period.calculationMethodId.takeIf { it.isNotBlank() }

        val method: ChargeCalculationMethod = if (targetMethodId != null) {
            val found = calculationMethodRepository.getCalculationMethodById(targetMethodId)
                ?: return Result.failure(IllegalArgumentException("روش محاسبه مورد نظر یافت نشد"))
            if (found.buildingId != buildingId) {
                return Result.failure(IllegalArgumentException("روش محاسبه به ساختمان دیگری تعلق دارد"))
            }
            found
        } else {
            calculationMethodRepository.getDefaultCalculationMethod(buildingId)
                ?: ChargeCalculationMethod(
                    id = "default-equal-$buildingId",
                    buildingId = buildingId,
                    name = "تسهیم مساوی پیش‌فرض",
                    methodType = MethodType.EQUAL,
                    createdBy = userId ?: period.createdBy
                )
        }

        // Calculate total cost: from period cost or sum of allocated building expenses
        var totalCost = period.totalBuildingCost
        if (totalCost <= 0L && buildingExpenseRepository != null) {
            val expenses = buildingExpenseRepository.getExpensesByChargePeriodId(period.id)
            val sumOfExpenses = expenses.sumOf { it.amount }
            if (sumOfExpenses > 0L) {
                totalCost = sumOfExpenses
            }
        }

        // Retrieve active units for building
        val units = unitRepository.getUnitsByBuildingId(buildingId)
        if (units.isEmpty()) {
            return Result.failure(IllegalStateException("هیچ واحد فعالی برای این ساختمان ثبت نشده است"))
        }

        // Execute deterministic financial engine
        val chargeItems = calculationEngine.calculateCharges(
            periodId = period.id,
            buildingId = buildingId,
            method = method,
            units = units,
            totalCost = totalCost,
            adjustments = unitAdjustments
        )

        // Store immutable items
        chargeItemRepository.deleteChargeItemsByPeriodId(period.id)
        chargeItemRepository.insertChargeItems(chargeItems)

        // Update charge period
        val updatedPeriod = period.copy(
            status = ChargePeriodStatus.CALCULATED,
            calculationMethodId = method.id,
            totalBuildingCost = totalCost
        )
        chargePeriodRepository.updateChargePeriod(updatedPeriod)

        auditRepository?.logEvent(
            AuditEvent(
                id = UUID.randomUUID().toString(),
                buildingId = buildingId,
                actorUserId = userId ?: period.createdBy,
                action = "CALCULATE_CHARGES",
                details = "محاسبه شارژ دوره ${period.title} با روش ${method.name} برای ${chargeItems.size} واحد (مبلغ کل: $totalCost ریال)",
                targetEntity = "ChargePeriod",
                targetEntityId = period.id
            )
        )

        return Result.success(chargeItems)
    }
}
