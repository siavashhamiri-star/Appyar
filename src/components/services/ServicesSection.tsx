import React, { useState } from 'react';
import { 
  Wrench, 
  Plus, 
  Phone, 
  Star, 
  CheckCircle2, 
  Clock, 
  AlertTriangle, 
  FileText, 
  Search,
  Filter,
  ShieldCheck
} from 'lucide-react';
import { 
  AccessibilitySettings, 
  Building, 
  ServiceProvider, 
  ServiceRecord, 
  MaintenanceRecord 
} from '../../types';
import { AccessibleCard } from '../common/AccessibleCard';
import { AccessibleButton } from '../common/AccessibleButton';
import { AccessibleBadge } from '../common/AccessibleBadge';
import { formatPersianNumber, formatRial, formatPersianDate } from '../../theme/designSystem';

interface ServicesSectionProps {
  settings: AccessibilitySettings;
  activeBuilding: Building;
  providers: ServiceProvider[];
  records: ServiceRecord[];
  maintenance: MaintenanceRecord[];
  onOpenNewRecordModal: () => void;
  onOpenNewProviderModal: () => void;
}

export const ServicesSection: React.FC<ServicesSectionProps> = ({
  settings,
  activeBuilding,
  providers,
  records,
  maintenance,
  onOpenNewRecordModal,
  onOpenNewProviderModal
}) => {
  const isHighContrast = settings.mode === 'high-contrast';
  const isEasy = settings.mode === 'easy';

  const [activeTab, setActiveTab] = useState<'records' | 'equipment' | 'providers'>('records');
  const [searchQuery, setSearchQuery] = useState('');

  const filteredRecords = records.filter(r => 
    r.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
    r.category.toLowerCase().includes(searchQuery.toLowerCase()) ||
    r.providerName.toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <div className="space-y-8" role="main" aria-label="مدیریت خدمات، تعمیرات و سرویس‌کاران">
      {/* Header & Quick Add */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className={`font-extrabold ${isEasy ? 'text-3xl sm:text-4xl text-[#1B4332]' : 'text-2xl sm:text-3xl'}`}>
            خدمات، تعمیرات و نگهداری ساختمان
          </h1>
          <p className={`mt-1 text-[#5E6660] ${isEasy ? 'text-lg font-medium' : 'text-sm'}`}>
            پیگیری سرویس‌های دوره‌ای، بایگانی سوابق تعمیرات و ارتباط با سرویس‌کاران معتمد
          </p>
        </div>

        <div className="flex items-center gap-3 flex-wrap">
          <AccessibleButton
            settings={settings}
            variant="primary"
            icon={<Plus className="w-5 h-5" />}
            onClick={onOpenNewRecordModal}
          >
            ثبت درخواست سرویس جدید
          </AccessibleButton>
          <AccessibleButton
            settings={settings}
            variant="secondary"
            icon={<Plus className="w-5 h-5" />}
            onClick={onOpenNewProviderModal}
          >
            معرفی سرویس‌کار جدید
          </AccessibleButton>
        </div>
      </div>

      {/* Subtabs Selector */}
      <div className="flex items-center gap-2 border-b border-[#E6E1D8] pb-1">
        <button
          onClick={() => setActiveTab('records')}
          className={`pb-3 px-4 text-sm font-bold border-b-2 transition-all cursor-pointer ${
            activeTab === 'records'
              ? isHighContrast
                ? 'border-[#FFFFFF] text-[#FFFFFF]'
                : 'border-[#1B4332] text-[#1B4332]'
              : 'border-transparent text-[#5E6660] hover:text-[#1E2320]'
          }`}
        >
          سوابق و درخواست‌های سرویس ({records.length})
        </button>
        <button
          onClick={() => setActiveTab('equipment')}
          className={`pb-3 px-4 text-sm font-bold border-b-2 transition-all cursor-pointer ${
            activeTab === 'equipment'
              ? isHighContrast
                ? 'border-[#FFFFFF] text-[#FFFFFF]'
                : 'border-[#1B4332] text-[#1B4332]'
              : 'border-transparent text-[#5E6660] hover:text-[#1E2320]'
          }`}
        >
          تجهیزات و چک‌لیست دوره‌ای ({maintenance.length})
        </button>
        <button
          onClick={() => setActiveTab('providers')}
          className={`pb-3 px-4 text-sm font-bold border-b-2 transition-all cursor-pointer ${
            activeTab === 'providers'
              ? isHighContrast
                ? 'border-[#FFFFFF] text-[#FFFFFF]'
                : 'border-[#1B4332] text-[#1B4332]'
              : 'border-transparent text-[#5E6660] hover:text-[#1E2320]'
          }`}
        >
          فهرست سرویس‌کاران ({providers.length})
        </button>
      </div>

      {/* Tab Content: Records */}
      {activeTab === 'records' && (
        <div className="space-y-4">
          <div className="relative max-w-md">
            <Search className="w-4 h-4 absolute right-3.5 top-1/2 -translate-y-1/2 text-[#858E87]" />
            <input
              type="text"
              placeholder="جستجو در عنوان، دسته یا سرویس‌کار..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="w-full pr-10 pl-4 py-2.5 rounded-xl border border-[#E6E1D8] bg-[#FFFFFF] text-sm focus:outline-none"
            />
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {filteredRecords.map((rec) => (
              <AccessibleCard key={rec.id} settings={settings} className="space-y-3">
                <div className="flex items-start justify-between gap-3">
                  <div>
                    <h3 className="font-bold text-base">{rec.title}</h3>
                    <span className="text-xs text-[#5E6660] block mt-0.5">
                      دسته: {rec.category} | سرویس‌کار: {rec.providerName}
                    </span>
                  </div>
                  {rec.status === 'COMPLETED' ? (
                    <AccessibleBadge settings={settings} variant="success" label="انجام شد" size="sm" />
                  ) : rec.status === 'IN_PROGRESS' ? (
                    <AccessibleBadge settings={settings} variant="amber" label="در حال انجام" size="sm" />
                  ) : (
                    <AccessibleBadge settings={settings} variant="neutral" label="در انتظار نوبت" size="sm" />
                  )}
                </div>

                {rec.description && (
                  <p className="text-xs sm:text-sm text-[#5E6660] bg-[#FAF8F5] p-2.5 rounded-xl">
                    {rec.description}
                  </p>
                )}

                <div className="flex items-center justify-between text-xs pt-2 border-t border-[#E6E1D8] text-[#5E6660]">
                  <span>تاریخ: {formatPersianDate(rec.serviceDate)}</span>
                  <span className="font-bold text-[#1B4332]">هزینه: {formatRial(rec.cost)}</span>
                </div>
              </AccessibleCard>
            ))}
          </div>
        </div>
      )}

      {/* Tab Content: Equipment */}
      {activeTab === 'equipment' && (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
          {maintenance.map((m) => (
            <AccessibleCard key={m.id} settings={settings} className="space-y-3">
              <div className="flex items-center justify-between">
                <h3 className="font-bold text-base">{m.equipmentName}</h3>
                {m.status === 'HEALTHY' ? (
                  <AccessibleBadge settings={settings} variant="success" label="سالم و فعال" size="sm" />
                ) : m.status === 'NEEDS_INSPECTION' ? (
                  <AccessibleBadge settings={settings} variant="warning" label="نیازمند بازرسی" size="sm" />
                ) : (
                  <AccessibleBadge settings={settings} variant="alert" label="هشدار نقص فنی" size="sm" />
                )}
              </div>

              <div className="space-y-1 text-xs text-[#5E6660]">
                <div>آخرین سرویس: {formatPersianDate(m.lastCheckDate)}</div>
                <div className="font-semibold text-[#1B4332]">موعد سرویس بعدی: {formatPersianDate(m.nextCheckDate)}</div>
              </div>

              {m.notes && (
                <p className="text-xs text-[#858E87] italic">یادداشت: {m.notes}</p>
              )}
            </AccessibleCard>
          ))}
        </div>
      )}

      {/* Tab Content: Providers */}
      {activeTab === 'providers' && (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
          {providers.map((p) => (
            <AccessibleCard key={p.id} settings={settings} className="space-y-3">
              <div className="flex items-center justify-between">
                <h3 className="font-bold text-base">{p.name}</h3>
                <div className="flex items-center gap-1 text-xs font-bold text-[#C87D20]">
                  <Star className="w-3.5 h-3.5 fill-current" />
                  <span>{formatPersianNumber(p.rating)}</span>
                </div>
              </div>

              <span className="inline-block text-xs font-medium text-[#1B4332] bg-[#EAF0EC] px-2.5 py-1 rounded-lg">
                تخصص: {p.specialty}
              </span>

              <div className="pt-2 border-t border-[#E6E1D8] flex items-center justify-between">
                <a
                  href={`tel:${p.phone}`}
                  className="inline-flex items-center gap-1.5 text-xs font-bold text-[#1B4332] hover:underline"
                >
                  <Phone className="w-3.5 h-3.5" />
                  <span>{p.phone}</span>
                </a>
                <AccessibleBadge settings={settings} variant="neutral" label={p.status === 'ACTIVE' ? 'فعال' : 'غیرفعال'} size="sm" />
              </div>
            </AccessibleCard>
          ))}
        </div>
      )}
    </div>
  );
};
