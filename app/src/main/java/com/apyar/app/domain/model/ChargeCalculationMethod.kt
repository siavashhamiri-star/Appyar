package com.apyar.app.domain.model

/**
 * MethodType for charge calculation.
 * EQUAL, AREA_BASED, RESIDENT_BASED, MIXED, CUSTOM
 */
enum class MethodType(val titleFa: String) {
    EQUAL("تقسیم مساوی"),
    AREA_BASED("بر اساس متراژ"),
    RESIDENT_BASED("بر اساس تعداد نفرات"),
    MIXED("ترکیبی (متراژ و نفرات)"),
    CUSTOM("فرمول سفارشی مجاز")
}

/**
 * ComponentType for FormulaComponent.
 * AREA, RESIDENT_COUNT, UNIT_COUNT, FIXED_AMOUNT, OCCUPANCY, CUSTOM_RULE
 */
enum class ComponentType(val titleFa: String) {
    AREA("متراژ واحد"),
    RESIDENT_COUNT("تعداد ساکنین"),
    UNIT_COUNT("تعداد واحدها (سهم مساوی)"),
    FIXED_AMOUNT("مبلغ ثابت"),
    OCCUPANCY("وضعیت سکونت"),
    CUSTOM_RULE("قاعده پارامتریک سفارشی")
}

/**
 * FormulaComponent representing parametric components of a calculation method.
 * Guaranteed safe: only uses mathematical weights and config maps, no executable scripts.
 */
data class FormulaComponent(
    val id: String,
    val calculationMethodId: String,
    val componentType: ComponentType,
    val weight: Double = 1.0,
    val enabled: Boolean = true,
    val configuration: Map<String, String> = emptyMap()
)

/**
 * ChargeCalculationMethod represents the formula or method defined for a building.
 */
data class ChargeCalculationMethod(
    val id: String,
    val buildingId: String,
    val name: String,
    val description: String? = null,
    val methodType: MethodType = MethodType.EQUAL,
    val isDefault: Boolean = false,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val createdBy: String,
    val components: List<FormulaComponent> = emptyList()
)
