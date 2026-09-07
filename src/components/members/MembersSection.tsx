import React, { useState } from 'react';
import { 
  Users, 
  ShieldCheck, 
  Key, 
  History, 
  Plus, 
  CheckCircle2, 
  AlertCircle, 
  Clock, 
  ShieldAlert,
  UserCheck
} from 'lucide-react';
import { 
  AccessibilitySettings, 
  Building, 
  BuildingMember, 
  DelegatedPermission, 
  Role 
} from '../../types';
import { AccessibleCard } from '../common/AccessibleCard';
import { AccessibleButton } from '../common/AccessibleButton';
import { AccessibleBadge } from '../common/AccessibleBadge';
import { formatPersianNumber, formatPersianDate } from '../../theme/designSystem';

interface MembersSectionProps {
  settings: AccessibilitySettings;
  activeBuilding: Building;
  members: BuildingMember[];
  delegations: DelegatedPermission[];
  onOpenNewMemberModal: () => void;
  onOpenNewDelegationModal: () => void;
}

export const MembersSection: React.FC<MembersSectionProps> = ({
  settings,
  activeBuilding,
  members,
  delegations,
  onOpenNewMemberModal,
  onOpenNewDelegationModal
}) => {
  const isHighContrast = settings.mode === 'high-contrast';
  const isEasy = settings.mode === 'easy';

  const [activeTab, setActiveTab] = useState<'members' | 'delegations' | 'audit'>('members');

  const getRoleTitle = (role: Role) => {
    switch (role) {
      case 'BUILDING_ADMIN': return 'مدیر ارشد ساختمان';
      case 'BOARD_MEMBER': return 'عضو هیئت مدیره';
      case 'OWNER': return 'مالک واحد';
      case 'TENANT': return 'مستأجر';
      case 'RESIDENT': return 'ساکن عادی';
      case 'APYAR_EXECUTIVE': return 'نماینده اجرایی اپیار';
    }
  };

  const auditEvents = [
    { id: '1', actor: 'علی محمدی (مدیر)', action: 'محاسبه شارژ دوره مرداد ۱۴۰۵', time: 'امروز، ساعت ۱۰:۳۰' },
    { id: '2', actor: 'سارا احمدی (هیئت مدیره)', action: 'تفویض مجوز مدیریت خدمات به سرایداری', time: 'دیروز، ساعت ۱۵:۲۰' },
    { id: '3', actor: 'علی محمدی (مدیر)', action: 'ثبت فاکتور سرویس آسانسور', time: '۳ روز پیش' },
  ];

  return (
    <div className="space-y-8" role="main" aria-label="مدیریت اعضا، نقش‌ها و مجوزها">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className={`font-extrabold ${isEasy ? 'text-3xl sm:text-4xl text-[#1B4332]' : 'text-2xl sm:text-3xl'}`}>
            اعضای ساختمان و تفویض اختیارات
          </h1>
          <p className={`mt-1 text-[#5E6660] ${isEasy ? 'text-lg font-medium' : 'text-sm'}`}>
            کنترل سطح دسترسی نقش‌ها، تفویض موقت مجوزها و بررسی سوابق رویدادها
          </p>
        </div>

        <div className="flex items-center gap-3 flex-wrap">
          <AccessibleButton
            settings={settings}
            variant="primary"
            icon={<Plus className="w-5 h-5" />}
            onClick={onOpenNewMemberModal}
          >
            دعوت عضو جدید
          </AccessibleButton>
          <AccessibleButton
            settings={settings}
            variant="secondary"
            icon={<Key className="w-5 h-5" />}
            onClick={onOpenNewDelegationModal}
          >
            تفویض اختیار موقت
          </AccessibleButton>
        </div>
      </div>

      {/* Tabs Switcher */}
      <div className="flex items-center gap-2 border-b border-[#E6E1D8] pb-1">
        <button
          onClick={() => setActiveTab('members')}
          className={`pb-3 px-4 text-sm font-bold border-b-2 transition-all cursor-pointer ${
            activeTab === 'members'
              ? isHighContrast
                ? 'border-[#FFFFFF] text-[#FFFFFF]'
                : 'border-[#1B4332] text-[#1B4332]'
              : 'border-transparent text-[#5E6660] hover:text-[#1E2320]'
          }`}
        >
          اعضا و مسئولین ساختمان ({members.length})
        </button>
        <button
          onClick={() => setActiveTab('delegations')}
          className={`pb-3 px-4 text-sm font-bold border-b-2 transition-all cursor-pointer ${
            activeTab === 'delegations'
              ? isHighContrast
                ? 'border-[#FFFFFF] text-[#FFFFFF]'
                : 'border-[#1B4332] text-[#1B4332]'
              : 'border-transparent text-[#5E6660] hover:text-[#1E2320]'
          }`}
        >
          تفویض‌های فعال ({delegations.length})
        </button>
        <button
          onClick={() => setActiveTab('audit')}
          className={`pb-3 px-4 text-sm font-bold border-b-2 transition-all cursor-pointer ${
            activeTab === 'audit'
              ? isHighContrast
                ? 'border-[#FFFFFF] text-[#FFFFFF]'
                : 'border-[#1B4332] text-[#1B4332]'
              : 'border-transparent text-[#5E6660] hover:text-[#1E2320]'
          }`}
        >
          دفتر ثبت رویدادها و امنیت
        </button>
      </div>

      {/* Tab 1: Members */}
      {activeTab === 'members' && (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {members.map((m) => (
            <AccessibleCard key={m.id} settings={settings} className="space-y-3">
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-2.5">
                  <div className="w-10 h-10 rounded-full bg-[#EAF0EC] text-[#1B4332] flex items-center justify-center font-bold text-sm">
                    {m.userName.slice(0, 1)}
                  </div>
                  <div>
                    <h3 className="font-bold text-base">{m.userName}</h3>
                    <span className="text-xs text-[#5E6660]">{m.userPhone}</span>
                  </div>
                </div>
                <AccessibleBadge
                  settings={settings}
                  variant={m.role === 'BUILDING_ADMIN' ? 'sage' : 'neutral'}
                  label={getRoleTitle(m.role)}
                  size="sm"
                />
              </div>

              <div className="pt-2 border-t border-[#E6E1D8] flex items-center justify-between text-xs text-[#5E6660]">
                <span>تاریخ عضویت: {formatPersianDate(m.startDate)}</span>
                <span className="text-[#1B4332] font-semibold">احراز هویت شده</span>
              </div>
            </AccessibleCard>
          ))}
        </div>
      )}

      {/* Tab 2: Delegations */}
      {activeTab === 'delegations' && (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {delegations.map((d) => (
            <AccessibleCard key={d.id} settings={settings} variant="amber" className="space-y-3">
              <div className="flex items-center justify-between">
                <div>
                  <h3 className="font-bold text-base">{d.granteeName}</h3>
                  <span className="text-xs text-[#8B570A] font-semibold block mt-0.5">
                    مجوز: {d.permission}
                  </span>
                </div>
                <AccessibleBadge settings={settings} variant="warning" label="تفویض معتبر" size="sm" />
              </div>

              {d.note && (
                <p className="text-xs text-[#5E6660] bg-[#FFFFFF] p-2 rounded-lg border border-[#E6E1D8]">
                  یادداشت: {d.note}
                </p>
              )}

              <div className="text-xs text-[#8B570A] flex items-center justify-between pt-2 border-t border-[#E6A85C]/40">
                <span>از: {formatPersianDate(d.startDate)}</span>
                <span>تا: {formatPersianDate(d.endDate)}</span>
              </div>
            </AccessibleCard>
          ))}
        </div>
      )}

      {/* Tab 3: Audit Log */}
      {activeTab === 'audit' && (
        <AccessibleCard settings={settings} className="space-y-4">
          <h3 className="font-bold text-base">گزارش غیرقابل تغییر رخدادهای سیستمی (Audit Trail)</h3>
          <div className="divide-y divide-[#E6E1D8] space-y-3">
            {auditEvents.map((evt) => (
              <div key={evt.id} className="pt-3 flex items-start justify-between text-xs">
                <div>
                  <span className="font-bold text-[#1E2320] block">{evt.action}</span>
                  <span className="text-[#5E6660]">توسط: {evt.actor}</span>
                </div>
                <span className="text-[#858E87]">{evt.time}</span>
              </div>
            ))}
          </div>
        </AccessibleCard>
      )}
    </div>
  );
};
