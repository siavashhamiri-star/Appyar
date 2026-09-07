package com.apyar.app.domain.model

/**
 * Types of financial transactions supported in the building ledger.
 */
enum class TransactionType(val titleFa: String, val isCredit: Boolean) {
    INCOME("درآمد / واریز متفرقه", true),
    EXPENSE("هزینه پرداختی ساختمان", false),
    CHARGE("ثبت شارژ دوره", false),
    PAYMENT("پرداخت شارژ توسط واحد", true),
    REFUND("استرداد وجه", false),
    ADJUSTMENT("تعدیل و اصلاحیه حساب", true)
}
