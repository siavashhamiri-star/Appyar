import React from 'react';
import { 
  AlertTriangle, 
  Clock, 
  CheckCircle2, 
  Plus, 
  ArrowLeft, 
  FileText, 
  Wrench, 
  DollarSign, 
  Bell, 
  ShieldCheck, 
  Sparkles, 
  Home, 
  Layers, 
  PhoneCall, 
  AlertCircle
} from 'lucide-react';
import { AccessibilitySettings, Building, Role } from '../../types';
import { AccessibleCard } from '../common/AccessibleCard';
import { AccessibleButton } from '../common/AccessibleButton';
import { AccessibleBadge } from '../common/AccessibleBadge';
import { formatPersianNumber, formatRial } from '../../theme/designSystem';

interface OverviewDashboardProps {
  settings: AccessibilitySettings;
  activeBuilding: Building;
  userRole: Role;
  onNavigateTab: (tabId: 'dashboard' | 'charges' | 'services' | 'parking' | 'units' | 'members') => void;
  onOpenNewRequestModal: () => void;
  onOpenNewExpenseModal: () => void;
}

export const OverviewDashboard: React.FC<OverviewDashboardProps> = ({
  settings,
  activeBuilding,
  userRole,
  onNavigateTab,
  onOpenNewRequestModal,
  onOpenNewExpenseModal
}) => {
  const isHighContrast = settings.mode === 'high-contrast';
  const isEasy = settings.mode === 'easy';

  // Key stats at a glance (calm, structured, no heavy SaaS metrics)
  const dashboardStats = [
    {
      title: 'درخواست‌های باز',
      count: 2,
      subtitle: '۱ مورد نیاز به تایید مدیر',
      variant: 'amber' as const,
      icon: <Clock className="w-5 h-5 text-[#C87D20]" />,
      actionText: 'مشاهده درخواست‌ها',
      onClick: () => onNavigateTab('services')
    },
    {
      title: 'کارهای در حال انجام',
      count: 3,
      subtitle: 'سرویس آسانسور و باغبانی',
      variant: 'sage' as const,
      icon: <Wrench className="w-5 h-5 text-[#1B4332]" />,
      actionText: 'پیگیری وضعیت',
      onClick: () => onNavigateTab('services')
    },
    {
      title: 'موارد نیازمند رسیدگی',
      count: 1,
      subtitle: 'قبض شارژ معوقه واحد ۱۰۲',
      variant: 'alert' as const,
      icon: <AlertTriangle className="w-5 h-5 text-[#A4422E]" />,
      actionText: 'بررسی شارژها',
      onClick: () => onNavigateTab('charges')
    },
    {
      title: 'صندوق و موجودی',
      countText: '۴۲٬۵۰۰٬۰۰۰ ریال',
      subtitle: 'وضعیت مالی پایدار',
      variant: 'default' as const,
      icon: <DollarSign className="w-5 h-5 text-[#1B4332]" />,
      actionText: 'گزارش مالی',
      onClick: () => onNavigateTab('charges')
    }
  ];

  // Urgent Notices & Open Items
  const urgentItems = [
    {
      id: 'urg-1',
      title: 'سرویس ماهانه آسانسور بلوک اصلی',
      date: 'امروز، ساعت ۱۶:۰۰',
      type: 'سرویس دوره‌ای',
      badgeVariant: 'warning' as const,
      badgeLabel: 'در نوبت اجرا',
      desc: 'حضور کارشناس فنی شرکت آسانسور برای بازرسی کابل‌ها و روغن‌کاری موتورخانه.'
    },
    {
      id: 'urg-2',
      title: 'شارژ معوقه دوره تیر ماه واحد ۱۰۲',
      date: '۳ روز گذشته',
      type: 'مالی',
      badgeVariant: 'alert' as const,
      badgeLabel: 'پرداخت نشده',
      desc: 'مبلغ ۲٬۵۰۰٬۰۰۰ ریال شارژ محاسبه‌شده هنوز واریز نشده است.'
    }
  ];

  // Recent Building Activities
  const recentActivities = [
    { id: 'act-1', text: 'محاسبه شارژ دوره مرداد ۱۴۰۵ نهایی شد', time: '۲ ساعت پیش', icon: <CheckCircle2 className="w-4 h-4 text-[#1B4332]" /> },
    { id: 'act-2', text: 'ثبت هزینه تعویض لامپ‌های راه‌پله به مبلغ ۳۵۰٬۰۰۰ ریال', time: 'دیروز', icon: <FileText className="w-4 h-4 text-[#C87D20]" /> },
    { id: 'act-3', text: 'واحد ۲۰۱: تغییر وضعیت سکونت به «تخلیه»', time: '۳ روز پیش', icon: <Home className="w-4 h-4 text-[#5E6660]" /> },
  ];

  return (
    <div className="space-y-8" role="main" aria-label="پیشخوان وضعیت ساختمان">
      {/* Top Welcome & Quick Actions */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-2">
        <div>
          <h1 className={`font-extrabold ${isEasy ? 'text-3xl sm:text-4xl text-[#1B4332]' : 'text-2xl sm:text-3xl'}`}>
            پیشخوان {activeBuilding.name}
          </h1>
          <p className={`mt-1 text-[#5E6660] ${isEasy ? 'text-lg font-medium' : 'text-sm'}`}>
            خلاصه وضعیت، درخواست‌های جاری و کارهای دارای اولویت ساختمان
          </p>
        </div>

        {/* Big accessible action buttons */}
        <div className="flex items-center gap-3 flex-wrap">
          <AccessibleButton
            settings={settings}
            variant="primary"
            icon={<Plus className="w-5 h-5" />}
            onClick={onOpenNewRequestModal}
          >
            افزودن درخواست جدید
          </AccessibleButton>
          
          <AccessibleButton
            settings={settings}
            variant="secondary"
            icon={<DollarSign className="w-5 h-5" />}
            onClick={onOpenNewExpenseModal}
          >
            ثبت هزینه ساختمان
          </AccessibleButton>
        </div>
      </div>

      {/* 4 Focused Pillars (Open requests, Active tasks, Overdue, Balance) */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 sm:gap-5">
        {dashboardStats.map((stat, idx) => (
          <AccessibleCard
            key={idx}
            settings={settings}
            variant={stat.variant === 'sage' ? 'sage' : stat.variant === 'amber' ? 'amber' : 'default'}
            className="flex flex-col justify-between"
          >
            <div>
              <div className="flex items-center justify-between mb-3">
                <span className={`font-bold ${isEasy ? 'text-lg' : 'text-sm text-[#5E6660]'}`}>
                  {stat.title}
                </span>
                <div className="p-2 rounded-xl bg-black/5 flex-shrink-0">
                  {stat.icon}
                </div>
              </div>
              <div className={`font-extrabold tracking-tight ${isEasy ? 'text-3xl text-[#1B4332]' : 'text-2xl'}`}>
                {stat.count !== undefined ? formatPersianNumber(stat.count) : stat.countText}
              </div>
              <p className={`mt-1.5 text-[#5E6660] ${isEasy ? 'text-base font-medium' : 'text-xs'}`}>
                {stat.subtitle}
              </p>
            </div>

            <div className="mt-5 pt-3 border-t border-black/5">
              <button
                onClick={stat.onClick}
                className="w-full flex items-center justify-between text-xs sm:text-sm font-bold text-[#1B4332] hover:text-[#2D5A43] group cursor-pointer"
              >
                <span>{stat.actionText}</span>
                <ArrowLeft className="w-4 h-4 transition-transform group-hover:-translate-x-1" />
              </button>
            </div>
          </AccessibleCard>
        ))}
      </div>

      {/* Primary Section: Urgent Matters & Priorities */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Left 2 Cols: Actionable Items */}
        <div className="lg:col-span-2 space-y-4">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <AlertCircle className="w-5 h-5 text-[#A4422E]" />
              <h2 className={`font-bold ${isEasy ? 'text-2xl text-[#1B4332]' : 'text-lg'}`}>
                موارد نیازمند توجه فوری
              </h2>
            </div>
            <span className="text-xs text-[#5E6660] font-medium">۲ مورد فعال</span>
          </div>

          <div className="space-y-3">
            {urgentItems.map((item) => (
              <AccessibleCard
                key={item.id}
                settings={settings}
                className="hover:border-[#D3CDC2] transition-colors"
              >
                <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3">
                  <div className="space-y-1">
                    <div className="flex items-center gap-2.5 flex-wrap">
                      <h3 className={`font-bold ${isEasy ? 'text-xl text-[#1B4332]' : 'text-base'}`}>
                        {item.title}
                      </h3>
                      <AccessibleBadge
                        settings={settings}
                        variant={item.badgeVariant}
                        label={item.badgeLabel}
                        size="sm"
                      />
                    </div>
                    <p className={`text-[#5E6660] ${isEasy ? 'text-base' : 'text-xs sm:text-sm'}`}>
                      {item.desc}
                    </p>
                    <span className="inline-block text-xs font-semibold text-[#858E87] pt-1">
                      زمان: {item.date}
                    </span>
                  </div>

                  <AccessibleButton
                    settings={settings}
                    variant="secondary"
                    onClick={() => onNavigateTab('services')}
                    className="self-start sm:self-center flex-shrink-0"
                  >
                    پیگیری کار
                  </AccessibleButton>
                </div>
              </AccessibleCard>
            ))}
          </div>
        </div>

        {/* Right 1 Col: Recent Building Timeline */}
        <div className="space-y-4">
          <div className="flex items-center gap-2">
            <Bell className="w-5 h-5 text-[#1B4332]" />
            <h2 className={`font-bold ${isEasy ? 'text-2xl text-[#1B4332]' : 'text-lg'}`}>
              رویدادهای اخیر ساختمان
            </h2>
          </div>

          <AccessibleCard settings={settings} className="space-y-4">
            <div className="space-y-4 divide-y divide-[#E6E1D8]">
              {recentActivities.map((act, i) => (
                <div key={act.id} className={`flex items-start gap-3 ${i !== 0 ? 'pt-3' : ''}`}>
                  <div className="mt-1 p-1.5 rounded-lg bg-[#EAF0EC] flex-shrink-0">
                    {act.icon}
                  </div>
                  <div>
                    <p className={`font-medium ${isEasy ? 'text-base' : 'text-sm'}`}>
                      {act.text}
                    </p>
                    <span className="text-xs text-[#858E87] font-normal block mt-0.5">
                      {act.time}
                    </span>
                  </div>
                </div>
              ))}
            </div>

            <div className="pt-2">
              <AccessibleButton
                settings={settings}
                variant="ghost"
                fullWidth
                onClick={() => onNavigateTab('members')}
              >
                مشاهده گزارش کامل لاگ و رویدادها
              </AccessibleButton>
            </div>
          </AccessibleCard>
        </div>
      </div>
    </div>
  );
};
