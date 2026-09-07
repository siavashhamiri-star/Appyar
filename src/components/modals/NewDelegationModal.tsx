import React, { useState } from 'react';
import { X, Key, Plus, ShieldCheck, AlertCircle, Calendar } from 'lucide-react';
import { AccessibilitySettings, Building, BuildingMember, DelegatedPermission, Permission } from '../../types';
import { AccessibleButton } from '../common/AccessibleButton';

interface NewDelegationModalProps {
  settings: AccessibilitySettings;
  isOpen: boolean;
  onClose: () => void;
  building: Building;
  members: BuildingMember[];
  onAddDelegation: (delegation: DelegatedPermission) => void;
}

export const NewDelegationModal: React.FC<NewDelegationModalProps> = ({
  settings,
  isOpen,
  onClose,
  building,
  members,
  onAddDelegation
}) => {
  if (!isOpen) return null;

  const [selectedMemberId, setSelectedMemberId] = useState(members[0]?.id || '');
  const [permission, setPermission] = useState<Permission>('MANAGE_FINANCIAL_DATA');
  const [durationDays, setDurationDays] = useState('30');
  const [note, setNote] = useState('');
  const [error, setError] = useState('');

  const permissionLabels: { value: Permission; label: string; desc: string }[] = [
    { value: 'MANAGE_FINANCIAL_DATA', label: 'مدیریت مالی و ثبت هزینه‌ها', desc: 'ثبت هزینه‌ها، فاکتورها و مشاهده تراز مالی' },
    { value: 'CALCULATE_CHARGE', label: 'محاسبه و تسهیم شارژ', desc: 'اجرای الگوریتم‌های تسهیم و صدور پیش‌نویس دوره' },
    { value: 'MANAGE_SERVICES', label: 'مدیریت خدمات و تعمیرات', desc: 'ثبت درخواست سرویس‌کار و تایید انجام سرویس آسانسور' },
    { value: 'MANAGE_PARKING_STORAGE', label: 'مدیریت پارکینگ و انباری', desc: 'تخصیص جای پارک و تنظیم فضاهای مهمان' },
    { value: 'VIEW_REPORTS', label: 'مشاهده کامل گزارشات و آمار', desc: 'دسترسی فقط‌خواندنی به تراز مالی و سوابق' }
  ];

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const targetMember = members.find(m => m.id === selectedMemberId) || members[0];
    if (!targetMember) {
      setError('لطفاً عضوی را برای تفویض انتخاب کنید.');
      return;
    }

    const days = parseInt(durationDays, 10) || 30;
    const delegation: DelegatedPermission = {
      id: `del-${Date.now()}`,
      buildingId: building.id,
      grantedByUserId: 'usr-admin',
      grantedToUserId: targetMember.userId,
      granteeName: targetMember.userName,
      permission,
      startDate: Date.now(),
      endDate: Date.now() + 86400000 * days,
      status: 'ACTIVE',
      note: note.trim() || undefined
    };

    onAddDelegation(delegation);
    onClose();
  };

  return (
    <div 
      className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50 backdrop-blur-xs overflow-y-auto"
      role="dialog"
      aria-modal="true"
      aria-labelledby="new-delegation-modal-title"
    >
      <div className="w-full max-w-lg rounded-3xl p-6 sm:p-8 bg-[#FAF8F5] border border-[#E6E1D8] text-[#1E2320] shadow-2xl my-8">
        <div className="flex items-center justify-between pb-4 border-b border-[#E6E1D8]">
          <div className="flex items-center gap-2">
            <Key className="w-5 h-5 text-[#C87D20]" />
            <h2 id="new-delegation-modal-title" className="text-xl font-bold text-[#1B4332]">
              تفویض هوشمند اختیار به اعضا
            </h2>
          </div>
          <button 
            onClick={onClose} 
            className="p-2 text-[#5E6660] hover:text-[#1E2320] cursor-pointer"
            aria-label="بستن"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {error && (
          <div className="mt-4 p-3 rounded-xl bg-[#FBEBE8] border border-[#A4422E]/30 text-[#A4422E] text-xs flex items-center gap-2">
            <AlertCircle className="w-4 h-4 flex-shrink-0" />
            <span>{error}</span>
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-4 mt-5">
          <div>
            <label className="block text-xs font-bold mb-1 text-[#5E6660]">انتخاب عضو دریافت‌کننده اختیار</label>
            <select
              value={selectedMemberId}
              onChange={(e) => setSelectedMemberId(e.target.value)}
              className="w-full p-2.5 rounded-xl border border-[#E6E1D8] bg-[#FFFFFF] text-sm focus:outline-none focus:border-[#1B4332]"
            >
              {members.map(m => (
                <option key={m.id} value={m.id}>
                  {m.userName} ({m.role}) - {m.userPhone}
                </option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-xs font-bold mb-1 text-[#5E6660]">نوع مجوز تفویضی</label>
            <select
              value={permission}
              onChange={(e) => setPermission(e.target.value as Permission)}
              className="w-full p-2.5 rounded-xl border border-[#E6E1D8] bg-[#FFFFFF] text-sm focus:outline-none focus:border-[#1B4332]"
            >
              {permissionLabels.map(p => (
                <option key={p.value} value={p.value}>
                  {p.label}
                </option>
              ))}
            </select>
            <span className="text-[11px] text-[#5E6660] block mt-1">
              {permissionLabels.find(p => p.value === permission)?.desc}
            </span>
          </div>

          <div>
            <label className="block text-xs font-bold mb-1 text-[#5E6660]">مدت اعتبار تفویض (روز)</label>
            <div className="grid grid-cols-4 gap-2">
              {[
                { label: '۷ روز', days: '7' },
                { label: '۱۵ روز', days: '15' },
                { label: '۱ ماه', days: '30' },
                { label: '۳ ماه', days: '90' }
              ].map(d => (
                <button
                  type="button"
                  key={d.days}
                  onClick={() => setDurationDays(d.days)}
                  className={`p-2 rounded-xl text-xs font-bold border transition-all cursor-pointer ${
                    durationDays === d.days
                      ? 'bg-[#1B4332] text-[#FFFFFF] border-[#1B4332]'
                      : 'bg-[#FFFFFF] border-[#E6E1D8] text-[#5E6660] hover:bg-[#FAF8F5]'
                  }`}
                >
                  {d.label}
                </button>
              ))}
            </div>
          </div>

          <div>
            <label className="block text-xs font-bold mb-1 text-[#5E6660]">علت یا یادداشت تفویض</label>
            <textarea
              rows={2}
              placeholder="مثلاً: تفویض موقت اختیارات به دلیل مرخصی مدیر یا پیگیری فاکتورها"
              value={note}
              onChange={(e) => setNote(e.target.value)}
              className="w-full p-2.5 rounded-xl border border-[#E6E1D8] bg-[#FFFFFF] text-sm focus:outline-none focus:border-[#1B4332]"
            />
          </div>

          <div className="mt-6 pt-4 border-t border-[#E6E1D8] flex items-center justify-end gap-3">
            <AccessibleButton
              settings={settings}
              variant="ghost"
              onClick={onClose}
            >
              انصراف
            </AccessibleButton>
            <AccessibleButton
              settings={settings}
              variant="accent"
              icon={<Plus className="w-4 h-4" />}
              onClick={() => {}}
            >
              ثبت تفویض اختیار
            </AccessibleButton>
          </div>
        </form>
      </div>
    </div>
  );
};
