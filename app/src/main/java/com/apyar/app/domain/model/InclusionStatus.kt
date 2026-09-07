package com.apyar.app.domain.model

/**
 * Inclusion status of a specific unit in a given ChargePeriod.
 * Determined per period (e.g., vacant unit included in area rule or excluded in resident rule).
 */
enum class InclusionStatus(val titleFa: String) {
    INCLUDED("مشمول در محاسبه"),
    EXCLUDED("خارج از محاسبه دوره")
}
