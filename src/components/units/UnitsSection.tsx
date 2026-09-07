import React, { useState } from 'react';
import { 
  Home, 
  Plus, 
  Users, 
  CheckCircle2, 
  Search, 
  Maximize2, 
  Car, 
  Archive,
  UserCheck
} from 'lucide-react';
import { AccessibilitySettings, Building, Unit } from '../../types';
import { AccessibleCard } from '../common/AccessibleCard';
import { AccessibleButton } from '../common/AccessibleButton';
import { AccessibleBadge } from '../common/AccessibleBadge';
import { formatPersianNumber } from '../../theme/designSystem';

interface UnitsSectionProps {
  settings: AccessibilitySettings;
  activeBuilding: Building;
  units: Unit[];
  onOpenNewUnitModal: () => void;
}

export const UnitsSection: React.FC<UnitsSectionProps> = ({
  settings,
  activeBuilding,
  units,
  onOpenNewUnitModal
}) => {
  const isHighContrast = settings.mode === 'high-contrast';
  const isEasy = settings.mode === 'easy';

  const [search, setSearch] = useState('');

  const filteredUnits = units.filter(u => 
    u.unitNumber.includes(search) || 
    (u.ownerName && u.ownerName.toLowerCase().includes(search.toLowerCase())) ||
    (u.tenantName && u.tenantName.toLowerCase().includes(search.toLowerCase()))
  );

  return (
    <div className="space-y-8" role="main" aria-label="مدیریت واحدهای مسکونی و مالکین">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className={`font-extrabold ${isEasy ? 'text-3xl sm:text-4xl text-[#1B4332]' : 'text-2xl sm:text-3xl'}`}>
            واحدهای ساختمان {activeBuilding.name}
          </h1>
          <p className={`mt-1 text-[#5E6660] ${isEasy ? 'text-lg font-medium' : 'text-sm'}`}>
            اطلاعات متراژ، طبقه، ساکنین، پارکینگ‌ها و وضعیت سکونت
          </p>
        </div>

        <AccessibleButton
          settings={settings}
          variant="primary"
          icon={<Plus className="w-5 h-5" />}
          onClick={onOpenNewUnitModal}
        >
          افزودن واحد جدید
        </AccessibleButton>
      </div>

      {/* Search Bar */}
      <div className="relative max-w-md">
        <Search className="w-4 h-4 absolute right-3.5 top-1/2 -translate-y-1/2 text-[#858E87]" />
        <input
          type="text"
          placeholder="جستجو بر اساس شماره واحد یا نام ساکن..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          className="w-full pr-10 pl-4 py-2.5 rounded-xl border border-[#E6E1D8] bg-[#FFFFFF] text-sm focus:outline-none"
        />
      </div>

      {/* Units Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
        {filteredUnits.map((u) => (
          <AccessibleCard key={u.id} settings={settings} className="space-y-4">
            <div className="flex items-center justify-between pb-3 border-b border-[#E6E1D8]">
              <div className="flex items-center gap-2.5">
                <div className="w-10 h-10 rounded-xl bg-[#EAF0EC] text-[#1B4332] flex items-center justify-center font-extrabold text-base">
                  {formatPersianNumber(u.unitNumber)}
                </div>
                <div>
                  <h3 className="font-bold text-base">واحد شماره {formatPersianNumber(u.unitNumber)}</h3>
                  <span className="text-xs text-[#5E6660]">طبقه {formatPersianNumber(u.floor)}</span>
                </div>
              </div>

              {u.occupancyStatus === 'OCCUPIED' ? (
                <AccessibleBadge settings={settings} variant="success" label="سکونت دارد" size="sm" />
              ) : (
                <AccessibleBadge settings={settings} variant="neutral" label="خالی / تخلیه" size="sm" />
              )}
            </div>

            {/* Metrics Chips */}
            <div className="grid grid-cols-2 gap-2 text-xs">
              <div className="bg-[#FAF8F5] p-2 rounded-lg border border-[#E6E1D8]">
                <span className="text-[#858E87] block">متراژ واحد:</span>
                <span className="font-bold text-[#1E2320]">{formatPersianNumber(u.areaSquareMeters)} مترمربع</span>
              </div>
              <div className="bg-[#FAF8F5] p-2 rounded-lg border border-[#E6E1D8]">
                <span className="text-[#858E87] block">تعداد ساکنین:</span>
                <span className="font-bold text-[#1E2320]">{formatPersianNumber(u.residentCount)} نفر</span>
              </div>
              <div className="bg-[#FAF8F5] p-2 rounded-lg border border-[#E6E1D8]">
                <span className="text-[#858E87] block">پارکینگ:</span>
                <span className="font-bold text-[#1E2320]">{formatPersianNumber(u.parkingCount)} جای پارک</span>
              </div>
              <div className="bg-[#FAF8F5] p-2 rounded-lg border border-[#E6E1D8]">
                <span className="text-[#858E87] block">انباری:</span>
                <span className="font-bold text-[#1E2320]">{formatPersianNumber(u.storageCount)} واحد</span>
              </div>
            </div>

            {/* Resident / Owner Info */}
            <div className="text-xs text-[#5E6660] space-y-1 pt-1">
              <div>مالک: <span className="font-semibold text-[#1E2320]">{u.ownerName || 'ثبت نشده'}</span></div>
              {u.tenantName && (
                <div>مستأجر: <span className="font-semibold text-[#1E2320]">{u.tenantName}</span></div>
              )}
            </div>
          </AccessibleCard>
        ))}
      </div>
    </div>
  );
};
