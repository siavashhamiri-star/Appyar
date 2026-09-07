import React, { useState } from 'react';
import { Mic, MicOff, Sparkles, Volume2, ArrowLeft, CheckCircle2 } from 'lucide-react';
import { AccessibilitySettings } from '../../types';

interface VoiceAssistantBarProps {
  settings: AccessibilitySettings;
  onExecuteIntent: (intentKey: string) => void;
}

export const VoiceAssistantBar: React.FC<VoiceAssistantBarProps> = ({
  settings,
  onExecuteIntent
}) => {
  const [isListening, setIsListening] = useState(false);
  const [lastSpokenFeedback, setLastSpokenFeedback] = useState<string | null>(null);

  const isHighContrast = settings.mode === 'high-contrast';
  const isEasy = settings.mode === 'easy';

  const quickVoiceIntents = [
    { label: 'ساختمانم را نشان بده', actionKey: 'VIEW_BUILDING', desc: 'نمایش اطلاعات کلی مجتمع' },
    { label: 'درخواست جدید ثبت کن', actionKey: 'NEW_REQUEST', desc: 'ثبت خرابی یا سرویس' },
    { label: 'وضعیت آسانسور را ببین', actionKey: 'ELEVATOR_STATUS', desc: 'بررسی وضعیت موتورخانه و آسانسور' },
    { label: 'کارهای عقب‌افتاده را نشان بده', actionKey: 'OVERDUE_TASKS', desc: 'اقلام و وظایف معوقه' },
    { label: 'قبض جدید ثبت کن', actionKey: 'NEW_INVOICE', desc: 'صدور صورتحساب یا هزینه' },
  ];

  const handleSimulateVoice = (phrase: string, actionKey: string) => {
    setIsListening(true);
    setLastSpokenFeedback(`در حال پردازش فرمان صوتی: «${phrase}» ...`);
    setTimeout(() => {
      setIsListening(false);
      setLastSpokenFeedback(`فرمان «${phrase}» با موفقیت اجرا شد.`);
      onExecuteIntent(actionKey);
      setTimeout(() => setLastSpokenFeedback(null), 4000);
    }, 900);
  };

  return (
    <div
      className={`rounded-2xl p-4 transition-all mb-6 ${
        isHighContrast
          ? 'bg-[#181818] border-2 border-[#FFFFFF] text-[#FFFFFF]'
          : 'bg-[#EAF0EC] border border-[#DCE7DF] text-[#1B4332]'
      }`}
      role="region"
      aria-label="دستیار فرمان صوتی آماده‌به‌کار"
    >
      <div className="flex flex-col lg:flex-row lg:items-center justify-between gap-4">
        {/* Voice Trigger Header */}
        <div className="flex items-center gap-3">
          <button
            onClick={() => handleSimulateVoice('ساختمانم را نشان بده', 'VIEW_BUILDING')}
            className={`w-12 h-12 rounded-2xl flex items-center justify-center transition-transform cursor-pointer ${
              isListening
                ? 'bg-[#A4422E] text-white animate-pulse'
                : isHighContrast
                  ? 'bg-[#FFFFFF] text-[#000000] border-2 border-[#FFFFFF]'
                  : 'bg-[#1B4332] text-[#FFFFFF] hover:bg-[#2D5A43]'
            }`}
            title="فرمان صوتی (کلیک برای نمونه‌سازی صوتی)"
            aria-label="شبیه‌ساز فرمان صوتی"
          >
            {isListening ? <MicOff className="w-6 h-6" /> : <Mic className="w-6 h-6" />}
          </button>
          <div>
            <div className="flex items-center gap-2">
              <span className="font-bold text-sm sm:text-base">فرمان صوتی (Voice-Ready UI)</span>
              <span className="text-xs px-2 py-0.5 rounded-full bg-[#1B4332] text-white font-medium">نمای آزمایشی UI</span>
            </div>
            <p className="text-xs text-[#5E6660] mt-0.5">
              شبیه‌ساز فرامین سریع صوتی جهت تسهیل ارگونومی و دسترسی‌پذیری کاربری در نسخه ۱.۰
            </p>
          </div>
        </div>

        {/* Quick Voice Chips */}
        <div className="flex items-center gap-2 overflow-x-auto pb-1 max-w-full">
          {quickVoiceIntents.map((item, idx) => (
            <button
              key={idx}
              onClick={() => handleSimulateVoice(item.label, item.actionKey)}
              className={`flex items-center gap-1.5 px-3 py-2 rounded-xl text-xs font-bold whitespace-nowrap transition-all cursor-pointer ${
                isHighContrast
                  ? 'bg-[#000000] text-[#FFFFFF] border border-[#FFFFFF] hover:bg-[#222222]'
                  : 'bg-[#FFFFFF] text-[#1B4332] border border-[#DCE7DF] hover:bg-[#FAF8F5] shadow-xs'
              }`}
            >
              <Sparkles className="w-3.5 h-3.5 text-[#C87D20]" />
              <span>«{item.label}»</span>
            </button>
          ))}
        </div>
      </div>

      {lastSpokenFeedback && (
        <div className="mt-3 pt-3 border-t border-[#DCE7DF] flex items-center gap-2 text-xs font-semibold text-[#1B4332]">
          <CheckCircle2 className="w-4 h-4 text-[#1B4332]" />
          <span>{lastSpokenFeedback}</span>
        </div>
      )}
    </div>
  );
};
