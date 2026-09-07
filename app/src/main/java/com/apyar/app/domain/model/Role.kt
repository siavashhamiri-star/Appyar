package com.apyar.app.domain.model

/**
 * Roles in the Apyar building management ecosystem.
 * A user does NOT have a static global role. Roles are assigned per building via BuildingMember.
 *
 * Notice on APYAR_EXECUTIVE:
 * APYAR_EXECUTIVE is NOT a legal or judicial building manager.
 * This role only has executable powers within specifically delegated permissions.
 */
enum class Role(val titleFa: String, val descriptionFa: String) {
    PLATFORM_ADMIN(
        titleFa = "مدیر کل پلتفرم",
        descriptionFa = "دسترسی کامل به تمامی امکانات پلتفرم در سطح کلان"
    ),
    BUILDING_ADMIN(
        titleFa = "مدیر ساختمان",
        descriptionFa = "مدیریت جامع ساختمان، واحدها، اعضا و تنظیمات"
    ),
    BOARD_MEMBER(
        titleFa = "عضو هیئت مدیره",
        descriptionFa = "نظارت بر امور ساختمان، مشاهده اطلاعات و تصمیم‌گیری‌های عمومی"
    ),
    OWNER(
        titleFa = "مالک واحد",
        descriptionFa = "مشاهده وضعیت ساختمان، واحدهای تحت مالکیت و اطلاعات مالی پایه"
    ),
    TENANT(
        titleFa = "مستأجر",
        descriptionFa = "مشاهده وضعیت ساختمان و واحد استیجاری"
    ),
    RESIDENT(
        titleFa = "ساکن",
        descriptionFa = "مشاهده عمومی ساختمان و واحد محل سکونت"
    ),
    ACCOUNTANT(
        titleFa = "حسابدار ساختمان",
        descriptionFa = "مدیریت امور مالی، ثبت درآمدها و هزینه‌ها و مشاهده اطلاعات مربوطه"
    ),
    CARETAKER(
        titleFa = "سرایدار / نگهبان",
        descriptionFa = "مشاهده امور پایه و ثبت رویدادهای روزمره ساختمان"
    ),
    APYAR_EXECUTIVE(
        titleFa = "مجری اجرایی اپیار",
        descriptionFa = "مجری خدمات؛ فقط در محدوده اختیاراتی که صراحتاً به او تفویض شده دسترسی دارد"
    )
}
