package com.apyar.app.domain.model

/**
 * Calculation types supported by the Apyar Charge Calculation Engine.
 * Apyar does not force any single rule on buildings.
 */
enum class CalculationType(val titleFa: String, val descriptionFa: String) {
    EQUAL(
        "تسهیم مساوی",
        "تقسیم مساوی کل هزینه بین تمام واحدهای مشمول محاسبه"
    ),
    AREA_BASED(
        "بر اساس متراژ",
        "محاسبه سهم هر واحد به نسبت متراژ به مجموع متراژ واحدهای مشمول"
    ),
    RESIDENT_BASED(
        "بر اساس تعداد نفرات",
        "محاسبه سهم هر واحد بر اساس تعداد ساکنین مشمول"
    ),
    AREA_AND_RESIDENT(
        "ترکیب متراژ و نفرات",
        "محاسبه سهم با تعیین وزن‌های سفارشی برای متراژ و نفرات (مثلاً ۶۰٪ متراژ و ۴۰٪ نفرات)"
    ),
    CUSTOM(
        "فرمول اختصاصی ساختمان",
        "اجرای فرمول تصویب‌شده و اختصاصی ساختمان"
    )
}
