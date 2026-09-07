package com.apyar.app.presentation.financial

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.apyar.app.core.di.AppContainer
import com.apyar.app.domain.engine.UnitInclusionSpec
import com.apyar.app.domain.model.CalculationPreview
import com.apyar.app.domain.model.CalculationType
import com.apyar.app.domain.model.ChargePeriod
import com.apyar.app.domain.model.ChargePeriodStatus
import com.apyar.app.domain.model.ChargeRule
import com.apyar.app.domain.model.CustomFormula
import com.apyar.app.domain.model.InclusionStatus
import com.apyar.app.domain.model.OccupancyStatus
import com.apyar.app.domain.model.Unit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChargeCalculationUiState(
    val isLoading: Boolean = true,
    val isCalculating: Boolean = false,
    val isFinalizing: Boolean = false,
    val buildingId: String = "",
    val currentUserId: String = "user-1",
    val availableRules: List<ChargeRule> = emptyList(),
    val availableCustomFormulas: List<CustomFormula> = emptyList(),
    val selectedRule: ChargeRule? = null,
    val selectedCustomFormula: CustomFormula? = null,
    val title: String = "شارژ دوره جاری",
    val totalCostInput: String = "10000000",
    val unitSpecs: List<UnitInclusionSpec> = emptyList(),
    val currentPeriod: ChargePeriod? = null,
    val calculationPreview: CalculationPreview? = null,
    val error: String? = null,
    val successMessage: String? = null,
    val currentStep: Int = 1 // 1: Setup & Units, 2: Preview & Inspect, 3: Finalized
)

class ChargeCalculationViewModel(
    private val appContainer: AppContainer,
    private val buildingId: String,
    private val currentUserId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ChargeCalculationUiState(buildingId = buildingId, currentUserId = currentUserId)
    )
    val uiState: StateFlow<ChargeCalculationUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val rulesResult = appContainer.getChargeRulesUseCase(currentUserId, buildingId)
                val customFormulasResult = appContainer.getCustomFormulasUseCase(currentUserId, buildingId)
                val units = appContainer.getUnitsByBuildingUseCase(buildingId)

                var rules = rulesResult.getOrDefault(emptyList())
                if (rules.isEmpty()) {
                    // Seed default standard rules for convenience
                    seedDefaultRules()
                    rules = appContainer.getChargeRulesUseCase(currentUserId, buildingId).getOrDefault(emptyList())
                }

                val customFormulas = customFormulasResult.getOrDefault(emptyList())
                val specs = units.map { UnitInclusionSpec(unit = it, inclusionStatus = InclusionStatus.INCLUDED) }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        availableRules = rules,
                        availableCustomFormulas = customFormulas,
                        selectedRule = rules.firstOrNull(),
                        unitSpecs = specs
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private suspend fun seedDefaultRules() {
        appContainer.createChargeRuleUseCase(
            userId = currentUserId,
            buildingId = buildingId,
            name = "تسهیم مساوی",
            description = "تقسیم مساوی کل هزینه بین تمام واحدهای مشمول",
            calculationType = CalculationType.EQUAL
        )
        appContainer.createChargeRuleUseCase(
            userId = currentUserId,
            buildingId = buildingId,
            name = "تسهیم بر اساس متراژ",
            description = "محاسبه سهم هر واحد به نسبت متراژ اختصاصی",
            calculationType = CalculationType.AREA_BASED
        )
        appContainer.createChargeRuleUseCase(
            userId = currentUserId,
            buildingId = buildingId,
            name = "تسهیم بر اساس نفرات",
            description = "محاسبه سهم هر واحد به نسبت تعداد نفرات ساکن",
            calculationType = CalculationType.RESIDENT_BASED
        )
        appContainer.createChargeRuleUseCase(
            userId = currentUserId,
            buildingId = buildingId,
            name = "تسهیم ترکیبی (۶۰٪ متراژ - ۴۰٪ نفرات)",
            description = "محاسبه سهم بر اساس ترکیب ۶۰ درصد متراژ و ۴۰ درصد نفرات",
            calculationType = CalculationType.AREA_AND_RESIDENT,
            formulaDefinition = """{"areaWeight":0.6, "residentWeight":0.4}"""
        )
    }

    fun selectRule(rule: ChargeRule) {
        _uiState.update { it.copy(selectedRule = rule, calculationPreview = null) }
    }

    fun selectCustomFormula(formula: CustomFormula) {
        _uiState.update { it.copy(selectedCustomFormula = formula) }
    }

    fun setTitle(title: String) {
        _uiState.update { it.copy(title = title) }
    }

    fun setTotalCost(cost: String) {
        _uiState.update { it.copy(totalCostInput = cost) }
    }

    fun toggleUnitInclusion(unitId: String) {
        _uiState.update { state ->
            val updatedSpecs = state.unitSpecs.map { spec ->
                if (spec.unit.id == unitId) {
                    val nextStatus = if (spec.inclusionStatus == InclusionStatus.INCLUDED) {
                        InclusionStatus.EXCLUDED
                    } else {
                        InclusionStatus.INCLUDED
                    }
                    spec.copy(inclusionStatus = nextStatus)
                } else spec
            }
            state.copy(unitSpecs = updatedSpecs, calculationPreview = null)
        }
    }

    fun executeCalculation() {
        val state = _uiState.value
        val rule = state.selectedRule ?: return
        val totalCost = state.totalCostInput.toLongOrNull() ?: 0L

        if (totalCost <= 0L) {
            _uiState.update { it.copy(error = "لطفاً مبلغ معتبری برای هزینه کل دوره وارد کنید") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isCalculating = true, error = null) }
            try {
                // Create charge period in draft
                val periodResult = appContainer.createChargePeriodUseCase(
                    userId = currentUserId,
                    buildingId = buildingId,
                    title = state.title.ifBlank { "شارژ دوره" },
                    startDate = System.currentTimeMillis() - (30L * 86400000L),
                    endDate = System.currentTimeMillis(),
                    totalAmount = totalCost,
                    chargeRuleId = rule.id
                )

                if (periodResult.isFailure) {
                    _uiState.update {
                        it.copy(isCalculating = false, error = periodResult.exceptionOrNull()?.message)
                    }
                    return@launch
                }

                val period = periodResult.getOrThrow()

                // Execute calculation on the period
                val previewResult = appContainer.calculateChargePeriodUseCase(
                    userId = currentUserId,
                    buildingId = buildingId,
                    periodId = period.id,
                    unitSpecs = state.unitSpecs
                )

                if (previewResult.isSuccess) {
                    _uiState.update {
                        it.copy(
                            isCalculating = false,
                            currentPeriod = period,
                            calculationPreview = previewResult.getOrThrow(),
                            currentStep = 2,
                            successMessage = "محاسبه با موفقیت انجام شد؛ لطفاً سهم واحدها و موازنه مالی را بررسی کنید."
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(isCalculating = false, error = previewResult.exceptionOrNull()?.message)
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isCalculating = false, error = e.message) }
            }
        }
    }

    fun finalizePeriod() {
        val period = _uiState.value.currentPeriod ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isFinalizing = true, error = null) }
            try {
                val result = appContainer.finalizeChargePeriodUseCase(
                    userId = currentUserId,
                    buildingId = buildingId,
                    periodId = period.id
                )

                if (result.isSuccess) {
                    _uiState.update {
                        it.copy(
                            isFinalizing = false,
                            currentPeriod = result.getOrThrow(),
                            currentStep = 3,
                            successMessage = "دوره شارژ با موفقیت نهایی و قطعی شد. صورت‌حساب‌ها در دفتر مالی ثبت گردیدند."
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(isFinalizing = false, error = result.exceptionOrNull()?.message)
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isFinalizing = false, error = e.message) }
            }
        }
    }

    fun resetWizard() {
        _uiState.update {
            it.copy(
                currentStep = 1,
                currentPeriod = null,
                calculationPreview = null,
                error = null,
                successMessage = null
            )
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(error = null, successMessage = null) }
    }

    companion object {
        fun provideFactory(
            appContainer: AppContainer,
            buildingId: String,
            currentUserId: String
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ChargeCalculationViewModel(appContainer, buildingId, currentUserId) as T
            }
        }
    }
}
