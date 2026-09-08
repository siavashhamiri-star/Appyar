package com.apyar.app

import android.app.Application
import com.apyar.app.core.di.AppContainer
import com.apyar.app.core.di.DefaultAppContainer
import com.apyar.app.core.security.SecurityConfig

/**
 * Main Application class for Apyar holding the application-level dependency container.
 */
class ApyarApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        // Validate TLS & HTTPS security standards on startup
        SecurityConfig.validateSecurityPolicy()
        container = DefaultAppContainer(this)
    }
}
