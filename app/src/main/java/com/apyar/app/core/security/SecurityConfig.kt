package com.apyar.app.core.security

import com.apyar.app.BuildConfig

/**
 * Security provider for API endpoints and client tokens.
 *
 * Mandates:
 * 1. Secret backend API keys or master credentials must NEVER reside in Android client source code.
 * 2. Client uses secure HTTPS endpoints and dynamic session-based Bearer tokens.
 * 3. In production, cleartext traffic is rejected by NetworkSecurityConfig and this validator.
 */
object SecurityConfig {

    /**
     * Base URL for the official Apyar API.
     */
    val apiBaseUrl: String
        get() = BuildConfig.APYAR_API_URL

    /**
     * Payment gateway landing URL for banking transactions.
     */
    val paymentGatewayUrl: String
        get() = BuildConfig.PAYMENT_GATEWAY_URL

    /**
     * Unique client application identifier.
     */
    val clientId: String
        get() = BuildConfig.CLIENT_ID

    /**
     * Indicates whether this binary is compiled for production.
     */
    val isProduction: Boolean
        get() = BuildConfig.IS_PRODUCTION

    /**
     * Whether verbose network logging is permitted.
     */
    val enableNetworkLogs: Boolean
        get() = BuildConfig.ENABLE_NETWORK_LOGS

    /**
     * Target market store (bazaar, myket, googleplay)
     */
    val marketStore: String
        get() = BuildConfig.MARKET_STORE

    /**
     * Validates that all communication endpoints adhere to TLS/HTTPS security standards.
     * Throws [SecurityException] if an insecure HTTP endpoint is detected in production.
     */
    fun validateSecurityPolicy() {
        if (isProduction) {
            if (!apiBaseUrl.startsWith("https://", ignoreCase = true)) {
                throw SecurityException("Security violation: Production API endpoint must strictly use HTTPS. Found: $apiBaseUrl")
            }
            if (!paymentGatewayUrl.startsWith("https://", ignoreCase = true)) {
                throw SecurityException("Security violation: Payment gateway endpoint must strictly use HTTPS. Found: $paymentGatewayUrl")
            }
        }
    }
}
