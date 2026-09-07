# Proguard & R8 rules for Apyar Release Build

# -----------------------------------------------------------------------------
# 1. Security & Anti-Leak Rules: Strip Android debug and info logs in production
# -----------------------------------------------------------------------------
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
}

# -----------------------------------------------------------------------------
# 2. BuildConfig & Constants Protection
# -----------------------------------------------------------------------------
-keep class com.apyar.app.BuildConfig { *; }

# -----------------------------------------------------------------------------
# 3. Kotlin Coroutines & Jetpack Compose Rules
# -----------------------------------------------------------------------------
-keepattributes *Annotation*, Signature, InnerClasses, EnclosingMethod
-keepclassmembers class * {
    @androidx.compose.runtime.Composable *;
}

# -----------------------------------------------------------------------------
# 4. Room Database Entity & DAO preservation
# -----------------------------------------------------------------------------
-keep class androidx.room.** { *; }
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }

# -----------------------------------------------------------------------------
# 5. Network Data Models / DTOs Preservation
# -----------------------------------------------------------------------------
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
    @kotlinx.serialization.SerialName <fields>;
}

