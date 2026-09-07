package com.apyar.app.domain.engine

import com.apyar.app.domain.model.CalculationType
import com.apyar.app.domain.model.ChargeCalculationMethod
import com.apyar.app.domain.model.ChargeItem
import com.apyar.app.domain.model.ChargeItemStatus
import com.apyar.app.domain.model.ChargePeriod
import com.apyar.app.domain.model.ChargeRule
import com.apyar.app.domain.model.ChargeStatus
import com.apyar.app.domain.model.ComponentType
import com.apyar.app.domain.model.InclusionStatus
import com.apyar.app.domain.model.MethodType
import com.apyar.app.domain.model.OccupancyStatus
import com.apyar.app.domain.model.Unit
import com.apyar.app.domain.model.UnitCharge
import java.util.UUID

/**
 * Data class representing a unit paired with its specified inclusion status for a period.
 */
data class UnitInclusionSpec(
    val unit: Unit,
    val inclusionStatus: InclusionStatus = InclusionStatus.INCLUDED
)

/**
 * Extensible Charge Calculation Engine.
 * Apyar does not impose an inflexible calculation logic; it strictly executes the specified ChargeRule.
 */
class ChargeCalculationEngine {

    /**
     * Executes the calculation for a list of units based on the given rule and total cost.
     *
     * @param periodId The ID of the ChargePeriod.
     * @param chargeRule The rule to execute.
     * @param unitSpecs The list of units and their inclusion status for this calculation.
     * @param totalCost The total cost to distribute in smallest currency units.
     * @return List of computed [UnitCharge] items guaranteeing sum(calculatedAmount) == totalCost.
     */
    fun calculate(
        periodId: String,
        chargeRule: ChargeRule,
        unitSpecs: List<UnitInclusionSpec>,
        totalCost: Long
    ): List<UnitCharge> {
        if (unitSpecs.isEmpty()) return emptyList()

        val includedSpecs = unitSpecs.filter { it.inclusionStatus == InclusionStatus.INCLUDED }
        val excludedSpecs = unitSpecs.filter { it.inclusionStatus == InclusionStatus.EXCLUDED }

        // Excluded units automatically receive 0 charge
        val excludedCharges = excludedSpecs.map { spec ->
            UnitCharge(
                id = UUID.randomUUID().toString(),
                chargePeriodId = periodId,
                unitId = spec.unit.id,
                inclusionStatus = InclusionStatus.EXCLUDED,
                calculatedAmount = 0L,
                adjustmentAmount = 0L,
                finalAmount = 0L,
                paidAmount = 0L,
                remainingAmount = 0L,
                status = ChargeStatus.PAID,
                calculationNotes = "خارج از محاسبات دوره طبق تصمیم ثبت‌شده"
            )
        }

        if (includedSpecs.isEmpty() || totalCost <= 0L) {
            val zeroIncluded = includedSpecs.map { spec ->
                UnitCharge(
                    id = UUID.randomUUID().toString(),
                    chargePeriodId = periodId,
                    unitId = spec.unit.id,
                    inclusionStatus = InclusionStatus.INCLUDED,
                    calculatedAmount = 0L,
                    adjustmentAmount = 0L,
                    finalAmount = 0L,
                    paidAmount = 0L,
                    remainingAmount = 0L,
                    status = ChargeStatus.PAID,
                    calculationNotes = "مبلغ دوره صفر است یا واحد مشمولی ثبت نشده است"
                )
            }
            return (zeroIncluded + excludedCharges).sortedBy { specId(it.unitId, unitSpecs) }
        }

        val calculatedAmounts = when (chargeRule.calculationType) {
            CalculationType.EQUAL -> {
                FinancialRoundingService.distributeEqually(totalCost, includedSpecs.size)
            }

            CalculationType.AREA_BASED -> {
                val areaWeights = includedSpecs.map { it.unit.areaSquareMeters }
                FinancialRoundingService.distributeProportionally(totalCost, areaWeights)
            }

            CalculationType.RESIDENT_BASED -> {
                val residentWeights = includedSpecs.map { it.unit.residentCount.toDouble() }
                val totalResidents = residentWeights.sum()
                if (totalResidents <= 0.0) {
                    // Fallback when all included units have 0 residents (e.g. all vacant)
                    FinancialRoundingService.distributeEqually(totalCost, includedSpecs.size)
                } else {
                    FinancialRoundingService.distributeProportionally(totalCost, residentWeights)
                }
            }

            CalculationType.AREA_AND_RESIDENT -> {
                val weights = parseAreaAndResidentWeights(chargeRule.formulaDefinition)
                val areaPool = (totalCost * weights.first).toLong()
                val residentPool = totalCost - areaPool

                val areaWeights = includedSpecs.map { it.unit.areaSquareMeters }
                val areaAllocations = FinancialRoundingService.distributeProportionally(areaPool, areaWeights)

                val residentWeights = includedSpecs.map { it.unit.residentCount.toDouble() }
                val totalResidents = residentWeights.sum()
                val residentAllocations = if (totalResidents <= 0.0) {
                    FinancialRoundingService.distributeEqually(residentPool, includedSpecs.size)
                } else {
                    FinancialRoundingService.distributeProportionally(residentPool, residentWeights)
                }

                includedSpecs.indices.map { i ->
                    areaAllocations[i] + residentAllocations[i]
                }
            }

            CalculationType.CUSTOM -> {
                executeCustomFormula(totalCost, includedSpecs, chargeRule.formulaDefinition)
            }
        }

        val includedCharges = includedSpecs.mapIndexed { index, spec ->
            val calculated = calculatedAmounts[index]
            val note = generateCalculationNote(chargeRule.calculationType, spec.unit)

            UnitCharge(
                id = UUID.randomUUID().toString(),
                chargePeriodId = periodId,
                unitId = spec.unit.id,
                inclusionStatus = InclusionStatus.INCLUDED,
                calculatedAmount = calculated,
                adjustmentAmount = 0L,
                finalAmount = calculated,
                paidAmount = 0L,
                remainingAmount = calculated,
                status = if (calculated > 0L) ChargeStatus.UNPAID else ChargeStatus.PAID,
                calculationNotes = note
            )
        }

        return (includedCharges + excludedCharges).sortedBy { specId(it.unitId, unitSpecs) }
    }

    private fun specId(unitId: String, specs: List<UnitInclusionSpec>): Int {
        return specs.indexOfFirst { it.unit.id == unitId }
    }

    private fun parseAreaAndResidentWeights(formulaDefinition: String): Pair<Double, Double> {
        // Look for custom weight patterns like "areaWeight=0.6,residentWeight=0.4" or JSON
        return try {
            if (formulaDefinition.contains("areaWeight") && formulaDefinition.contains("residentWeight")) {
                val areaRegex = """"areaWeight"\s*:\s*([0-9.]+)""".toRegex()
                val residentRegex = """"residentWeight"\s*:\s*([0-9.]+)""".toRegex()

                val areaMatch = areaRegex.find(formulaDefinition)?.groupValues?.get(1)?.toDoubleOrNull()
                val resMatch = residentRegex.find(formulaDefinition)?.groupValues?.get(1)?.toDoubleOrNull()

                if (areaMatch != null && resMatch != null && (areaMatch + resMatch) > 0.0) {
                    val total = areaMatch + resMatch
                    return Pair(areaMatch / total, resMatch / total)
                }
            }
            // Default 50% area / 50% residents
            Pair(0.5, 0.5)
        } catch (_: Exception) {
            Pair(0.5, 0.5)
        }
    }

    private fun executeCustomFormula(
        totalCost: Long,
        includedSpecs: List<UnitInclusionSpec>,
        formulaDef: String
    ): List<Long> {
        // Custom extensible formula processor
        // Example format: {"fixedBasePerUnit": 100000, "areaFactor": 0.7, "residentFactor": 0.3}
        return try {
            val fixedBaseRegex = """"fixedBasePerUnit"\s*:\s*([0-9]+)""".toRegex()
            val fixedBase = fixedBaseRegex.find(formulaDef)?.groupValues?.get(1)?.toLongOrNull() ?: 0L

            val totalFixed = fixedBase * includedSpecs.size
            if (totalFixed < totalCost && totalCost > 0L) {
                val variablePool = totalCost - totalFixed
                val areaWeights = includedSpecs.map { it.unit.areaSquareMeters }
                val variableShares = FinancialRoundingService.distributeProportionally(variablePool, areaWeights)
                includedSpecs.indices.map { i -> fixedBase + variableShares[i] }
            } else {
                // Fallback to proportional area if custom expression is not parameterized with fixed base
                val weights = includedSpecs.map {
                    val occupancyMultiplier = if (it.unit.occupancyStatus == OccupancyStatus.VACANT) 0.5 else 1.0
                    it.unit.areaSquareMeters * occupancyMultiplier
                }
                FinancialRoundingService.distributeProportionally(totalCost, weights)
            }
        } catch (_: Exception) {
            FinancialRoundingService.distributeEqually(totalCost, includedSpecs.size)
        }
    }

    private fun generateCalculationNote(type: CalculationType, unit: Unit): String {
        return when (type) {
            CalculationType.EQUAL -> "تسهیم مساوی بین واحدهای مشمول"
            CalculationType.AREA_BASED -> "محاسبه بر اساس متراژ (${unit.areaSquareMeters} م² - وضعیت: ${unit.occupancyStatus.titleFa})"
            CalculationType.RESIDENT_BASED -> "محاسبه بر اساس نفرات (${unit.residentCount} نفر ساکن)"
            CalculationType.AREA_AND_RESIDENT -> "محاسبه ترکیبی (${unit.areaSquareMeters} م² و ${unit.residentCount} نفر)"
            CalculationType.CUSTOM -> "محاسبه بر اساس فرمول اختصاصی ساختمان"
        }
    }

    /**
     * Stage 7.1 Charge calculation method executor.
     * Computes integer base amounts for all units and produces immutable ChargeItem records.
     * Guarantees sum(baseAmount) == totalCost without floating-point error.
     */
    fun calculateCharges(
        periodId: String,
        buildingId: String,
        method: ChargeCalculationMethod,
        units: List<Unit>,
        totalCost: Long,
        adjustments: Map<String, Long> = emptyMap()
    ): List<ChargeItem> {
        val activeUnits = units.filter { it.isActive }
        if (activeUnits.isEmpty()) return emptyList()
        if (totalCost <= 0L) {
            return activeUnits.map { unit ->
                val adj = adjustments[unit.id] ?: 0L
                ChargeItem(
                    id = UUID.randomUUID().toString(),
                    chargePeriodId = periodId,
                    buildingId = buildingId,
                    unitId = unit.id,
                    calculationMethodId = method.id,
                    baseAmount = 0L,
                    adjustments = adj,
                    finalAmount = adj,
                    calculatedAt = System.currentTimeMillis(),
                    status = ChargeItemStatus.CALCULATED
                )
            }
        }

        val baseAmounts: List<Long> = when (method.methodType) {
            MethodType.EQUAL -> {
                FinancialRoundingService.distributeEqually(totalCost, activeUnits.size)
            }
            MethodType.AREA_BASED -> {
                val areaWeights = activeUnits.map { it.areaSquareMeters }
                val totalArea = areaWeights.sum()
                if (totalArea <= 0.0) {
                    FinancialRoundingService.distributeEqually(totalCost, activeUnits.size)
                } else {
                    FinancialRoundingService.distributeProportionally(totalCost, areaWeights)
                }
            }
            MethodType.RESIDENT_BASED -> {
                val residentWeights = activeUnits.map { it.residentCount.toDouble() }
                val totalResidents = residentWeights.sum()
                if (totalResidents <= 0.0) {
                    FinancialRoundingService.distributeEqually(totalCost, activeUnits.size)
                } else {
                    FinancialRoundingService.distributeProportionally(totalCost, residentWeights)
                }
            }
            MethodType.MIXED -> {
                val areaComp = method.components.find { it.componentType == ComponentType.AREA && it.enabled }
                val resComp = method.components.find { it.componentType == ComponentType.RESIDENT_COUNT && it.enabled }
                val areaWeight = areaComp?.weight ?: 0.5
                val resWeight = resComp?.weight ?: 0.5
                val totalW = (areaWeight + resWeight).coerceAtLeast(0.0001)
                val normAreaW = areaWeight / totalW
                val normResW = resWeight / totalW

                val areaPool = (totalCost * normAreaW).toLong()
                val resPool = totalCost - areaPool

                val areaWeights = activeUnits.map { it.areaSquareMeters }
                val areaAllocations = if (areaWeights.sum() <= 0.0) {
                    FinancialRoundingService.distributeEqually(areaPool, activeUnits.size)
                } else {
                    FinancialRoundingService.distributeProportionally(areaPool, areaWeights)
                }

                val resWeights = activeUnits.map { it.residentCount.toDouble() }
                val resAllocations = if (resWeights.sum() <= 0.0) {
                    FinancialRoundingService.distributeEqually(resPool, activeUnits.size)
                } else {
                    FinancialRoundingService.distributeProportionally(resPool, resWeights)
                }

                activeUnits.indices.map { i -> areaAllocations[i] + resAllocations[i] }
            }
            MethodType.CUSTOM -> {
                val fixedComp = method.components.find { it.componentType == ComponentType.FIXED_AMOUNT && it.enabled }
                val fixedBasePerUnit = fixedComp?.configuration?.get("fixedAmount")?.toLongOrNull()
                    ?: fixedComp?.weight?.toLong() ?: 0L

                val totalFixed = fixedBasePerUnit * activeUnits.size
                if (totalFixed < totalCost && totalFixed > 0L) {
                    val variablePool = totalCost - totalFixed
                    val variableWeights = activeUnits.map { unit ->
                        val occMultiplier = if (unit.occupancyStatus == OccupancyStatus.VACANT) {
                            val occComp = method.components.find { it.componentType == ComponentType.OCCUPANCY && it.enabled }
                            occComp?.configuration?.get("vacantMultiplier")?.toDoubleOrNull() ?: 0.5
                        } else 1.0
                        unit.areaSquareMeters * occMultiplier
                    }
                    val variableShares = FinancialRoundingService.distributeProportionally(variablePool, variableWeights)
                    activeUnits.indices.map { i -> fixedBasePerUnit + variableShares[i] }
                } else {
                    val weights = activeUnits.map { unit ->
                        var w = 0.0
                        method.components.filter { it.enabled }.forEach { comp ->
                            when (comp.componentType) {
                                ComponentType.AREA -> w += unit.areaSquareMeters * comp.weight
                                ComponentType.RESIDENT_COUNT -> w += unit.residentCount * comp.weight
                                ComponentType.UNIT_COUNT -> w += 1.0 * comp.weight
                                ComponentType.OCCUPANCY -> {
                                    if (unit.occupancyStatus == OccupancyStatus.VACANT) {
                                        val vacantFactor = comp.configuration["vacantMultiplier"]?.toDoubleOrNull() ?: 0.5
                                        w *= vacantFactor
                                    }
                                }
                                ComponentType.CUSTOM_RULE, ComponentType.FIXED_AMOUNT -> {
                                    w += 1.0 * comp.weight
                                }
                            }
                        }
                        if (w <= 0.0) unit.areaSquareMeters.coerceAtLeast(1.0) else w
                    }
                    FinancialRoundingService.distributeProportionally(totalCost, weights)
                }
            }
        }

        return activeUnits.mapIndexed { index, unit ->
            val base = baseAmounts[index]
            val adj = adjustments[unit.id] ?: 0L
            ChargeItem(
                id = UUID.randomUUID().toString(),
                chargePeriodId = periodId,
                buildingId = buildingId,
                unitId = unit.id,
                calculationMethodId = method.id,
                baseAmount = base,
                adjustments = adj,
                finalAmount = base + adj,
                calculatedAt = System.currentTimeMillis(),
                status = ChargeItemStatus.CALCULATED
            )
        }
    }
}
