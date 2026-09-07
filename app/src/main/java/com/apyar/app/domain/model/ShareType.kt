package com.apyar.app.domain.model

/**
 * Responsibility share type for future owner/tenant breakdowns.
 * Extensible for building policies without imposing rigid legal presumptions.
 */
enum class ShareType(val titleFa: String) {
    OWNER_SHARE("سهم مالک"),
    TENANT_SHARE("سهم مستأجر"),
    SHARED("مشترک / توافقی")
}
