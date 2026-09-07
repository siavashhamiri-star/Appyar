import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

// Load local.properties safely if present
val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        load(FileInputStream(localPropertiesFile))
    }
}

// Helper function to resolve config keys from local.properties or system environment
fun getSecureConfig(key: String, default: String = ""): String {
    return (localProperties.getProperty(key)
        ?: System.getenv(key)
        ?: project.findProperty(key) as? String
        ?: default).replace("\"", "\\\"")
}

val apyarApiUrl = getSecureConfig("APYAR_API_URL", "https://api.apyar.ir/v1")
val apyarDevApiUrl = getSecureConfig("APYAR_DEV_API_URL", "https://dev-api.apyar.ir/v1")
val paymentGatewayUrl = getSecureConfig("PAYMENT_GATEWAY_URL", "https://gateway.apyar.ir/pay")
val clientId = getSecureConfig("APYAR_CLIENT_ID", "com.apyar.app")

android {
    namespace = "com.apyar.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.apyar.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        // Automated BuildConfig injection for client endpoints (Client-safe configuration only)
        buildConfigField("String", "APYAR_API_URL", "\"$apyarApiUrl\"")
        buildConfigField("String", "PAYMENT_GATEWAY_URL", "\"$paymentGatewayUrl\"")
        buildConfigField("String", "CLIENT_ID", "\"$clientId\"")
    }

    signingConfigs {
        create("release") {
            val keystorePath = getSecureConfig("KEYSTORE_PATH", "")
            val keystoreFile = if (keystorePath.isNotEmpty()) file(keystorePath) else rootProject.file("keystore/release.jks")
            if (keystoreFile.exists()) {
                storeFile = keystoreFile
                storePassword = getSecureConfig("KEYSTORE_PASSWORD", "")
                keyAlias = getSecureConfig("KEY_ALIAS", "")
                keyPassword = getSecureConfig("KEY_PASSWORD", "")
            } else {
                // Safe fallback to debug signing for local development/testing without breaking build
                initWith(getByName("debug"))
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
            
            // Production flags & API endpoints
            buildConfigField("boolean", "IS_PRODUCTION", "true")
            buildConfigField("boolean", "ENABLE_NETWORK_LOGS", "false")
            buildConfigField("String", "APYAR_API_URL", "\"$apyarApiUrl\"")
        }
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            isDebuggable = true
            
            // Development flags & API endpoints
            buildConfigField("boolean", "IS_PRODUCTION", "false")
            buildConfigField("boolean", "ENABLE_NETWORK_LOGS", "true")
            buildConfigField("String", "APYAR_API_URL", "\"$apyarDevApiUrl\"")
        }
    }

    // Android App Bundle (AAB) configuration
    bundle {
        language {
            // Keep Persian (fa) localization intact across all device locales
            enableSplit = false
        }
        density {
            enableSplit = true
        }
        abi {
            enableSplit = true
        }
    }

    // Automated naming for built APK artifacts
    applicationVariants.all {
        val variant = this
        variant.outputs.all {
            val output = this as? com.android.build.gradle.internal.api.BaseVariantOutputImpl
            val buildTypeName = variant.buildType.name
            val versionName = variant.versionName
            output?.outputFileName = "apyar-v${versionName}-${buildTypeName}.apk"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Android Core & Lifecycle
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)

    // Jetpack Compose with BOM
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Navigation Compose
    implementation(libs.androidx.navigation.compose)

    // Room Database
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Compose Tooling
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.androidx.arch.core.testing)
    testImplementation(libs.androidx.test.core)
}

// -----------------------------------------------------------------------------
// Automated Build Verification & Information Tasks
// -----------------------------------------------------------------------------
tasks.register("printBuildInfo") {
    group = "help"
    description = "Displays the current automated Apyar build and API configuration safely."
    doLast {
        val isKeystoreConfigured = getSecureConfig("KEYSTORE_PATH", "").isNotEmpty() ||
                rootProject.file("keystore/release.jks").exists()

        println("=========================================================")
        println(" APYAR ANDROID AUTOMATED BUILD CONFIGURATION (v1.0.0)")
        println("=========================================================")
        println(" - Application ID (Release) : com.apyar.app")
        println(" - Application ID (Debug)   : com.apyar.app.debug")
        println(" - Min SDK / Target SDK     : 26 / 35")
        println(" - Release API Endpoint     : $apyarApiUrl")
        println(" - Debug API Endpoint       : $apyarDevApiUrl")
        println(" - Payment Gateway URL      : $paymentGatewayUrl")
        println(" - Release Keystore Config  : " + if (isKeystoreConfigured) "CONFIGURED" else "FALLBACK TO DEBUG KEY")
        println(" - AAB Split Language       : DISABLED (Persian fa resources preserved)")
        println("---------------------------------------------------------")
        println(" Commands:")
        println("   * Build Release AAB : ./gradlew bundleRelease")
        println("   * Build Release APK : ./gradlew assembleRelease")
        println("   * Build Debug APK   : ./gradlew assembleDebug")
        println("=========================================================")
    }
}

tasks.register("verifyReleaseSecurity") {
    group = "verification"
    description = "Verifies that production API endpoints enforce secure HTTPS transport."
    doLast {
        if (!apyarApiUrl.startsWith("https://")) {
            throw GradleException("SECURITY VIOLATION: Production APYAR_API_URL must use HTTPS protocol! Found: $apyarApiUrl")
        }
        if (!paymentGatewayUrl.startsWith("https://")) {
            throw GradleException("SECURITY VIOLATION: Production PAYMENT_GATEWAY_URL must use HTTPS protocol! Found: $paymentGatewayUrl")
        }
        println("Security Check Passed: All production endpoints are secured via HTTPS.")
    }
}

