package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.FinancialSummary
import com.apyar.app.domain.model.Permission
import com.apyar.app.domain.model.TransactionType
import com.apyar.app.domain.repository.ChargePeriodRepository
import com.apyar.app.domain.repository.FinancialAccountRepository
import com.apyar.app.domain.repository.FinancialTransactionRepository
import com.apyar.app.domain.repository.UnitChargeRepository

class GetFinancialSummaryUseCase(
    private val financialAccountRepository: FinancialAccountRepository,
    private val financialTransactionRepository: FinancialTransactionRepository,
    private val chargePeriodRepository: ChargePeriodRepository,
    private val unitChargeRepository: UnitChargeRepository,
    private val checkPermissionUseCase: CheckPermissionUseCase
) {
    suspend operator fun invoke(
        userId: String,
        buildingId: String
    ): Result<FinancialSummary> {
        val permissionResult = checkPermissionUseCase(userId, buildingId, Permission.VIEW_FINANCIAL_DATA)
        if (permissionResult.isFailure) {
            return Result.failure(permissionResult.exceptionOrNull()!!)
        }

        val account = financialAccountRepository.getAccountByBuildingId(buildingId)
        val transactions = financialTransactionRepository.getTransactionsByBuildingId(buildingId)

        val balance = account?.balance ?: 0L
        val totalIncome = transactions.filter { it.type == TransactionType.INCOME || it.type == TransactionType.PAYMENT }
            .sumOf { it.amount }
        val totalExpense = transactions.filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }

        val periods = chargePeriodRepository.getChargePeriodsByBuildingId(buildingId)
        val latestPeriod = periods.maxByOrNull { it.createdAt }

        var currentPeriodCharge = 0L
        var totalPaid = 0L
        var totalRemaining = 0L
        var debtorUnits = 0
        var settledUnits = 0

        if (latestPeriod != null) {
            currentPeriodCharge = latestPeriod.totalAmount
            val charges = unitChargeRepository.getUnitChargesByPeriodId(latestPeriod.id)
            totalPaid = charges.sumOf { it.paidAmount }
            totalRemaining = charges.sumOf { it.remainingAmount }
            debtorUnits = charges.count { it.remainingAmount > 0L }
            settledUnits = charges.count { it.remainingAmount == 0L }
        }

        val summary = FinancialSummary(
            buildingId = buildingId,
            balance = balance,
            totalIncome = totalIncome,
            totalExpense = totalExpense,
            currentPeriodCharge = currentPeriodCharge,
            totalPaid = totalPaid,
            totalRemaining = totalRemaining,
            debtorUnitsCount = debtorUnits,
            settledUnitsCount = settledUnits
        )

        return Result.success(summary)
    }
}
