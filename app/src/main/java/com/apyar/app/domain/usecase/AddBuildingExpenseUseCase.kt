package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.AuditEvent
import com.apyar.app.domain.model.BuildingExpense
import com.apyar.app.domain.model.ExpenseCategory
import com.apyar.app.domain.model.ExpenseStatus
import com.apyar.app.domain.model.Permission
import com.apyar.app.domain.repository.AuditRepository
import com.apyar.app.domain.repository.BuildingExpenseRepository
import com.apyar.app.domain.repository.BuildingRepository
import com.apyar.app.domain.repository.ChargePeriodRepository
import java.util.UUID

class AddBuildingExpenseUseCase(
    private val buildingExpenseRepository: BuildingExpenseRepository,
    private val buildingRepository: BuildingRepository? = null,
    private val chargePeriodRepository: ChargePeriodRepository? = null,
    private val checkPermissionUseCase: CheckPermissionUseCase? = null,
    private val auditRepository: AuditRepository? = null
) {
    suspend operator fun invoke(
        buildingId: String,
        title: String,
        amount: Long,
        category: ExpenseCategory = ExpenseCategory.GENERAL_MAINTENANCE,
        description: String? = null,
        chargePeriodId: String? = null,
        expenseDate: Long = System.currentTimeMillis(),
        createdBy: String,
        status: ExpenseStatus = ExpenseStatus.APPROVED
    ): Result<BuildingExpense> {
        if (title.isBlank()) {
            return Result.failure(IllegalArgumentException("عنوان هزینه نمی‌تواند خالی باشد"))
        }
        if (buildingId.isBlank()) {
            return Result.failure(IllegalArgumentException("شناسه ساختمان معتبر نیست"))
        }
        if (amount <= 0L) {
            return Result.failure(IllegalArgumentException("مبلغ هزینه باید بیشتر از صفر باشد"))
        }
        if (createdBy.isBlank()) {
            return Result.failure(IllegalArgumentException("ثبت‌کننده هزینه مشخص نشده است"))
        }

        // Validate building exists if buildingRepository provided
        if (buildingRepository != null) {
            val building = buildingRepository.getBuildingById(buildingId)
            if (building == null) {
                return Result.failure(IllegalArgumentException("ساختمان مورد نظر یافت نشد"))
            }
        }

        // Validate charge period belongs to building if linked
        if (!chargePeriodId.isNull correlationBlank() && chargePeriodRepository != null) {
            val period = chargePeriodRepository.getChargePeriodById(chargePeriodId)
            if (period == null) {
                return Result.failure(IllegalArgumentException("دوره شارژ مشخص‌شده یافت نشد"))
            }
            if (period.buildingId != buildingId) {
                return Result.failure(IllegalArgumentException("دوره شارژ به این ساختمان تعلق ندارد"))
            }
        }

        if (checkPermissionUseCase != null) {
            val permCheck = checkPermissionUseCase(createdBy, buildingId, Permission.MANAGE_FINANCIAL_DATA)
            if (permCheck.isFailure) {
                return Result.failure(permCheck.exceptionOrNull()!!)
            }
        }

        val expense = BuildingExpense(
            id = UUID.randomUUID().toString(),
            buildingId = buildingId,
            chargePeriodId = if (chargePeriodId.isNullOrBlank()) null else chargePeriodId,
            title = title.trim(),
            description = description?.trim(),
            category = category,
            amount = amount,
            expenseDate = expenseDate,
            createdBy = createdBy,
            status = status
        )

        buildingExpenseRepository.insertExpense(expense)

        auditRepository?.logEvent(
            AuditEvent(
                id = UUID.randomUUID().toString(),
                buildingId = buildingId,
                actorUserId = createdBy,
                action = "ADD_BUILDING_EXPENSE",
                details = "ثبت هزینه ساختمان: $title به مبلغ $amount ریال",
                targetEntity = "BuildingExpense",
                targetEntityId = expense.id
            )
        )

        return Result.success(expense)
    }

    private fun String?.isNull correlationBlank(): Boolean {
        return this == null || this.isBlank()
    }
}
