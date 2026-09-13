#!/usr/bin/env bash
set -e

# ==============================================================================
# اسکریپت ساخت خودکار خروجی‌های AAB و APK اپلیکیشن اپیار (Apyar)
# ==============================================================================

echo "========================================================="
echo "   🚀 شروع فرآیند اتوماسیون بیلد APYAR (APK & AAB)   "
echo "========================================================="

# ۱. اعطای مجوز اجرایی به gradlew
chmod +x ./gradlew

# ۲. پاک‌سازی بیلدهای قدیمی
echo "🧹 پاک‌سازی مسیرهای بیلد قبلی..."
./gradlew clean

# ۳. بررسی وضعیت امنیتی و پروتکل‌های HTTPS
echo "🔒 بررسی تنظیمات امنیتی API..."
./gradlew verifyReleaseSecurity

# ۴. ساخت پکیج‌های نصبی APK نهایی برای کافه‌بازار و مایکت
echo "📦 در حال تولید Release APK برای کافه‌بازار، مایکت و گوگل‌پلی..."
./gradlew assembleBazaarRelease
./gradlew assembleMyketRelease
./gradlew assembleGoogleplayRelease

# ۵. ساخت بسته‌های انتشار AAB
echo "📦 در حال تولید Release AAB (App Bundle) برای مارکت‌ها..."
./gradlew bundleBazaarRelease
./gradlew bundleMyketRelease
./gradlew bundleGoogleplayRelease

echo "========================================================="
echo "   ✅ بیلد با موفقیت به پایان رسید!                      "
echo "========================================================="
echo "مسیر فایل‌های تولید شده:"
echo " 📁 بازار APK: app/build/outputs/apk/bazaar/release/"
echo " 📁 مایکت APK: app/build/outputs/apk/myket/release/"
echo " 📁 بازار AAB: app/build/outputs/bundle/bazaarRelease/"
echo " 📁 مایکت AAB: app/build/outputs/bundle/myketRelease/"
echo "========================================================="
