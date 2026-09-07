package com.apyar.app.domain.model

/**
 * Granular permissions within the Apyar system.
 * Financial, documents, and event log permissions are declared here for future stages.
 */
enum class Permission(val titleFa: String, val categoryFa: String) {
    // Building
    VIEW_BUILDING("مشاهده اطلاعات ساختمان", "ساختمان"),
    EDIT_BUILDING("ویرایش مشخصات ساختمان", "ساختمان"),
    VIEW_BUILDING_SETTINGS("مشاهده تنظیمات ساختمان", "تنظیمات"),
    MANAGE_BUILDING_SETTINGS("مدیریت و تغییر تنظیمات ساختمان", "تنظیمات"),

    // Units
    VIEW_UNIT("مشاهده مشخصات و ساکنین واحدها", "واحدها"),
    EDIT_UNIT("ویرایش و ثبت واحدها", "واحدها"),

    // Members
    VIEW_MEMBERS("مشاهده فهرست اعضای ساختمان", "اعضا و نقش‌ها"),
    MANAGE_MEMBERS("مدیریت و تغییر نقش اعضای ساختمان", "اعضا و نقش‌ها"),
    INVITE_MEMBER("دعوت و افزودن عضو جدید", "اعضا و نقش‌ها"),
    REMOVE_MEMBER("حذف و غیرفعال‌سازی عضو", "اعضا و نقش‌ها"),

    // Financial (Declared for future stage)
    VIEW_FINANCIAL_DATA("مشاهده گزارش‌ها و صورت‌حساب‌های مالی", "مالی"),
    MANAGE_FINANCIAL_DATA("مدیریت امور مالی، شارژ و تراکنش‌ها", "مالی"),

    // Documents (Declared for future stage)
    VIEW_DOCUMENTS("مشاهده اسناد و آیین‌نامه‌های ساختمان", "اسناد"),
    MANAGE_DOCUMENTS("بارگذاری و مدیریت اسناد و مدارک", "اسناد"),

    // Events & Audit (Declared for future stage)
    VIEW_EVENT_LOG("مشاهده دفتر رویدادها و لاگ عملیات", "رویدادها"),
    CREATE_EVENT("ثبت رویداد و گزارش جدید", "رویدادها"),

    // Parking (Stage 4)
    VIEW_PARKING_DATA("مشاهده اطلاعات و تخصیص پارکینگ‌ها", "پارکینگ"),
    MANAGE_PARKING_DATA("مدیریت، تخصیص و تغییرات پارکینگ", "پارکینگ"),

    // Storage (Stage 4)
    VIEW_STORAGE_DATA("مشاهده اطلاعات و تخصیص انباری‌ها", "انباری"),
    MANAGE_STORAGE_DATA("مدیریت، تخصیص و تغییرات انباری", "انباری"),

    // Agreements (Stage 4)
    VIEW_PRIVATE_AGREEMENTS("مشاهده توافقات و قراردادهای واحدها", "توافقات"),
    MANAGE_PRIVATE_AGREEMENTS("ثبت، ویرایش و لغو توافقات", "توافقات"),

    // Services & Providers (Stage 6)
    VIEW_SERVICE_PROVIDERS("مشاهده تأمین‌کنندگان و سرویس‌کاران", "خدمات و تأمین‌کنندگان"),
    MANAGE_SERVICE_PROVIDERS("مدیریت و ثبت تأمین‌کنندگان و سرویس‌کاران", "خدمات و تأمین‌کنندگان"),
    VIEW_SERVICE_RECORDS("مشاهده سوابق و درخواست‌های خدمات", "خدمات و تأمین‌کنندگان"),
    CREATE_SERVICE_RECORD("ثبت درخواست خدمت جدید", "خدمات و تأمین‌کنندگان"),
    MANAGE_SERVICE_RECORDS("مدیریت و تغییر وضعیت سوابق خدمات", "خدمات و تأمین‌کنندگان"),

    // Maintenance (Stage 6)
    VIEW_MAINTENANCE("مشاهده سوابق تعمیر و نگهداری تجهیزات", "تعمیر و نگهداری"),
    MANAGE_MAINTENANCE("ثبت و مدیریت تعمیر و نگهداری تجهیزات", "تعمیر و نگهداری"),

    // Invoices & Payments (Stage 6)
    VIEW_INVOICES("مشاهده فاکتورهای خدمات", "فاکتور و مالی خدمات"),
    CREATE_INVOICE("ثبت فاکتور خدمت", "فاکتور و مالی خدمات"),
    MANAGE_INVOICES("مدیریت و تغییر وضعیت فاکتورها", "فاکتور و مالی خدمات"),
    VIEW_BUILDING_PAYMENTS("مشاهده پرداخت‌های ساختمان", "فاکتور و مالی خدمات"),
    CREATE_BUILDING_PAYMENT("ثبت پرداخت جدید برای ساختمان", "فاکتور و مالی خدمات"),
    MANAGE_BUILDING_PAYMENTS("مدیریت و ویرایش پرداخت‌ها", "فاکتور و مالی خدمات"),

    // Public Utility Bills (Stage 6)
    VIEW_BUILDING_BILLS("مشاهده و آرشیو قبوض عمومی ساختمان", "قبوض عمومی"),
    MANAGE_BUILDING_BILLS("ثبت و مدیریت قبوض عمومی ساختمان", "قبوض عمومی"),

    // Executive Tasks
    MANAGE_EXECUTIVE_TASKS("اجرای امور محوله و وظایف اجرایی", "امور اجرایی")
}
