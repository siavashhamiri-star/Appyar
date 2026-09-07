import React from 'react';
import { 
  Eye, 
  Sparkles, 
  Type, 
  ZapOff, 
  Check, 
  Volume2, 
  Maximize2,
  Sliders
} from 'lucide-react';
import { AccessibilitySettings, AccessibilityMode, TextSize } from '../../types';
import { AccessibleButton } from './AccessibleButton';

interface AccessibilityControlPanelProps {
  settings: AccessibilitySettings;
  onUpdateSettings: (newSettings: AccessibilitySettings) => void;
  isOpen: boolean;
  onClose: () => void;
}

export const AccessibilityControlPanel: React.FC<AccessibilityControlPanelProps> = ({
  settings,
  onUpdateSettings,
  isOpen,
  onClose
}) => {
  if (!isOpen) return null;

  const isHighContrast = settings.mode === 'high-contrast';

  const modes: { id: AccessibilityMode; title: string; desc: string; icon: React.ReactNode }[] = [
    {
      id: 'standard',
      title: 'حالت استاندارد',
      desc: 'ظاهر آرام و مدرن با پالت زمردی و عاج گرم',
      icon: <Sparkles className="w-5 h-5" />
    },
    {
      id: 'high-contrast',
      title: 'کنتراست بالا',
      desc: 'حداکثر تمایز دیداری، خطوط مرزی واضح و خوانایی شدید',
      icon: <Eye className="w-5 h-5" />
    },
    {
      id: 'easy',
      title: 'نمای ساده و خوانا',
      desc: 'دکمه‌های بسیار بزرگ، متن درشت و حذف گزینه‌های اضافی',
      icon: <Maximize2 className="w-5 h-5" />
    }
  ];

  const textSizes: { id: TextSize; title: string }[] = [
    { id: 'normal', title: 'عادی' },
    { id: 'large', title: 'بزرگ (+۲۰٪)' },
    { id: 'extra', title: 'خیلی بزرگ (+۴۰٪)' }
  ];

  return (
    <div 
      className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/40 backdrop-blur-xs"
      role="dialog"
      aria-modal="true"
      aria-label="تنظیمات دسترسی‌پذیری و راحتی بصری"
    >
      <div 
        className={`w-full max-w-xl rounded-3xl p-6 sm:p-8 shadow-2xl transition-all ${
          isHighContrast
            ? 'bg-[#121212] border-2 border-[#FFFFFF] text-[#FFFFFF]'
            : 'bg-[#FAF8F5] border border-[#E6E1D8] text-[#1E2320]'
        }`}
      >
        <div className="flex items-center justify-between pb-4 border-b border-[#E6E1D8]">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-xl bg-[#1B4332] text-[#FFFFFF] flex items-center justify-center">
              <Sliders className="w-5 h-5" />
            </div>
            <div>
              <h2 className="text-xl font-bold">تنظیمات راحتی دیداری و دسترس‌پذیری</h2>
              <p className="text-xs sm:text-sm text-[#5E6660]">سفارشی‌سازی ظاهر برنامه برای استفاده آسوده‌تر</p>
            </div>
          </div>
        </div>

        {/* Visual Modes */}
        <div className="mt-6">
          <label className="block text-sm font-bold mb-3">حالت نمایشی رابط کاربری</label>
          <div className="grid grid-cols-1 gap-3">
            {modes.map((m) => {
              const active = settings.mode === m.id;
              return (
                <button
                  key={m.id}
                  onClick={() => onUpdateSettings({ ...settings, mode: m.id })}
                  className={`flex items-start gap-4 p-4 rounded-2xl border text-right transition-all cursor-pointer ${
                    active
                      ? isHighContrast
                        ? 'bg-[#FFFFFF] text-[#000000] border-[#FFFFFF] font-bold ring-2 ring-[#FFFFFF]'
                        : 'bg-[#EAF0EC] text-[#1B4332] border-[#1B4332] ring-2 ring-[#1B4332]/20 font-bold'
                      : isHighContrast
                        ? 'bg-[#1E1E1E] text-[#FFFFFF] border-[#333333] hover:border-[#666666]'
                        : 'bg-[#FFFFFF] text-[#1E2320] border-[#E6E1D8] hover:bg-[#F4EFEA]'
                  }`}
                >
                  <div className={`mt-0.5 p-2 rounded-xl flex-shrink-0 ${
                    active ? 'bg-[#1B4332] text-white' : 'bg-[#EAF0EC] text-[#1B4332]'
                  }`}>
                    {m.icon}
                  </div>
                  <div className="flex-1">
                    <div className="flex items-center justify-between">
                      <span className="text-base font-bold">{m.title}</span>
                      {active && <Check className="w-5 h-5 text-[#1B4332]" />}
                    </div>
                    <p className={`text-xs sm:text-sm mt-1 ${active ? 'text-[#1B4332]/80' : 'text-[#5E6660]'}`}>
                      {m.desc}
                    </p>
                  </div>
                </button>
              );
            })}
          </div>
        </div>

        {/* Font Scaling */}
        <div className="mt-6">
          <label className="block text-sm font-bold mb-3 flex items-center gap-2">
            <Type className="w-4 h-4 text-[#1B4332]" />
            <span>اندازه متون و نوشته‌ها</span>
          </label>
          <div className="grid grid-cols-3 gap-2">
            {textSizes.map((ts) => {
              const active = settings.textSize === ts.id;
              return (
                <button
                  key={ts.id}
                  onClick={() => onUpdateSettings({ ...settings, textSize: ts.id })}
                  className={`py-3 px-3 rounded-xl border text-center font-bold text-sm cursor-pointer transition-all ${
                    active
                      ? isHighContrast
                        ? 'bg-[#FFFFFF] text-[#000000] border-[#FFFFFF]'
                        : 'bg-[#1B4332] text-[#FFFFFF] border-[#1B4332]'
                      : isHighContrast
                        ? 'bg-[#1E1E1E] text-[#FFFFFF] border-[#333333]'
                        : 'bg-[#FFFFFF] text-[#1E2320] border-[#E6E1D8] hover:bg-[#F4EFEA]'
                  }`}
                >
                  {ts.title}
                </button>
              );
            })}
          </div>
        </div>

        {/* Motion & Assistance Toggles */}
        <div className="mt-6 space-y-3">
          <label 
            className={`flex items-center justify-between p-3.5 rounded-2xl border cursor-pointer ${
              isHighContrast
                ? 'bg-[#1E1E1E] border-[#333333]'
                : 'bg-[#FFFFFF] border-[#E6E1D8]'
            }`}
          >
            <div className="flex items-center gap-3">
              <ZapOff className="w-5 h-5 text-[#C87D20]" />
              <div>
                <span className="text-sm font-bold block">کاهش انیمیشن‌ها و حرکت‌ها</span>
                <span className="text-xs text-[#5E6660]">توقف جلوه‌های حرکتی برای جلوگیری از حواس‌پرتی و خستگی چشم</span>
              </div>
            </div>
            <input
              type="checkbox"
              checked={settings.reduceMotion}
              onChange={(e) => onUpdateSettings({ ...settings, reduceMotion: e.target.checked })}
              className="w-5 h-5 accent-[#1B4332] cursor-pointer"
            />
          </label>

          <label 
            className={`flex items-center justify-between p-3.5 rounded-2xl border cursor-pointer ${
              isHighContrast
                ? 'bg-[#1E1E1E] border-[#333333]'
                : 'bg-[#FFFFFF] border-[#E6E1D8]'
            }`}
          >
            <div className="flex items-center gap-3">
              <Volume2 className="w-5 h-5 text-[#1B4332]" />
              <div>
                <span className="text-sm font-bold block">بهینه‌سازی برای صفحه‌خوان (TalkBack)</span>
                <span className="text-xs text-[#5E6660]">افزودن توضیحات معنایی صوتی برای تمام کارت‌ها و دکمه‌ها</span>
              </div>
            </div>
            <input
              type="checkbox"
              checked={settings.screenReaderDescriptions}
              onChange={(e) => onUpdateSettings({ ...settings, screenReaderDescriptions: e.target.checked })}
              className="w-5 h-5 accent-[#1B4332] cursor-pointer"
            />
          </label>
        </div>

        <div className="mt-8 pt-4 border-t border-[#E6E1D8] flex justify-end">
          <AccessibleButton
            settings={settings}
            variant="primary"
            onClick={onClose}
          >
            تایید و بستن تنظیمات
          </AccessibleButton>
        </div>
      </div>
    </div>
  );
};
