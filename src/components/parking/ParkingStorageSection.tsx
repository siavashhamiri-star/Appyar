import React, { useState } from 'react';
import { 
  Car, 
  Archive, 
  Plus, 
  CheckCircle2, 
  Clock, 
  ShieldCheck, 
  Layers, 
  Home, 
  UserCheck 
} from 'lucide-react';
import { 
  AccessibilitySettings, 
  Building, 
  ParkingSpace, 
  StorageUnit 
} from '../../types';
import { AccessibleCard } from '../common/AccessibleCard';
import { AccessibleButton } from '../common/AccessibleButton';
import { AccessibleBadge } from '../common/AccessibleBadge';
import { formatPersianNumber } from '../../theme/designSystem';

interface ParkingStorageSectionProps {
  settings: AccessibilitySettings;
  activeBuilding: Building;
  parkingSpaces: ParkingSpace[];
  storageUnits: StorageUnit[];
  onOpenNewParkingModal: () => void;
  onOpenNewStorageModal: () => void;
}

export const ParkingStorageSection: React.FC<ParkingStorageSectionProps> = ({
  settings,
  activeBuilding,
  parkingSpaces,
  storageUnits,
  onOpenNewParkingModal,
  onOpenNewStorageModal
}) => {
  const isHighContrast = settings.mode === 'high-contrast';
  const isEasy = settings.mode === 'easy';

  const [activeTab, setActiveTab] = useState<'parking' | 'storage'>('parking');

  const totalParking = parkingSpaces.length;
  const occupiedParking = parkingSpaces.filter(p => p.status === 'OCCUPIED').length;
  const guestParking = parkingSpaces.filter(p => p.isGuestSpace).length;

  const totalStorage = storageUnits.length;
  const occupiedStorage = storageUnits.filter(s => s.status === 'OCCUPIED').length;

  return (
    <div className="space-y-8" role="main" aria-label="مدیریت پارکینگ و انباری ساختمان">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className={`font-extrabold ${isEasy ? 'text-3xl sm:text-4xl text-[#1B4332]' : 'text-2xl sm:text-3xl'}`}>
            پارکینگ‌ها و انباری‌های مشاع
          </h1>
          <p className={`mt-1 text-[#5E6660] ${isEasy ? 'text-lg font-medium' : 'text-sm'}`}>
            تخصیص فضاها به واحدها، مدیریت فضاهای مهمان و رصد وضعیت اشغال
          </p>
        </div>

        <div className="flex items-center gap-3 flex-wrap">
          <AccessibleButton
            settings={settings}
            variant="primary"
            icon={<Plus className="w-5 h-5" />}
            onClick={onOpenNewParkingModal}
          >
            تعریف جای پارک جدید
          </AccessibleButton>
          <AccessibleButton
            settings={settings}
            variant="secondary"
            icon={<Plus className="w-5 h-5" />}
            onClick={onOpenNewStorageModal}
          >
            تعریف انباری جدید
          </AccessibleButton>
        </div>
      </div>

      {/* Overview Stats */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <AccessibleCard settings={settings} variant="sage" className="space-y-1">
          <span className="text-xs font-bold text-[#5E6660]">کل جای پارک‌ها</span>
          <div className="text-2xl font-extrabold">{formatPersianNumber(totalParking)} جای پارک</div>
          <span className="text-xs text-[#1B4332] block">
            {formatPersianNumber(occupiedParking)} اشغال شده | {formatPersianNumber(guestParking)} پارکینگ مهمان
          </span>
        </AccessibleCard>

        <AccessibleCard settings={settings} variant="amber" className="space-y-1">
          <span className="text-xs font-bold text-[#5E6660]">کل انباری‌ها</span>
          <div className="text-2xl font-extrabold">{formatPersianNumber(totalStorage)} واحد انباری</div>
          <span className="text-xs text-[#8B570A] block">
            {formatPersianNumber(occupiedStorage)} تخصیص‌یافته به واحدها
          </span>
        </AccessibleCard>

        <AccessibleCard settings={settings} className="space-y-1">
          <span className="text-xs font-bold text-[#5E6660]">وضعیت تفکیک مشاعات</span>
          <div className="text-2xl font-extrabold text-[#1B4332]">سازمان‌یافته</div>
          <span className="text-xs text-[#5E6660] block">
            بدون تداخل در شماره‌گذاری طبقات منفی
          </span>
        </AccessibleCard>
      </div>

      {/* Tab Switcher */}
      <div className="flex items-center gap-2 border-b border-[#E6E1D8] pb-1">
        <button
          onClick={() => setActiveTab('parking')}
          className={`pb-3 px-4 text-sm font-bold border-b-2 transition-all cursor-pointer ${
            activeTab === 'parking'
              ? isHighContrast
                ? 'border-[#FFFFFF] text-[#FFFFFF]'
                : 'border-[#1B4332] text-[#1B4332]'
              : 'border-transparent text-[#5E6660] hover:text-[#1E2320]'
          }`}
        >
          فهرست پارکینگ‌ها ({parkingSpaces.length})
        </button>
        <button
          onClick={() => setActiveTab('storage')}
          className={`pb-3 px-4 text-sm font-bold border-b-2 transition-all cursor-pointer ${
            activeTab === 'storage'
              ? isHighContrast
                ? 'border-[#FFFFFF] text-[#FFFFFF]'
                : 'border-[#1B4332] text-[#1B4332]'
              : 'border-transparent text-[#5E6660] hover:text-[#1E2320]'
          }`}
        >
          فهرست انباری‌ها ({storageUnits.length})
        </button>
      </div>

      {/* Content: Parking Grid */}
      {activeTab === 'parking' && (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
          {parkingSpaces.map((p) => (
            <AccessibleCard key={p.id} settings={settings} className="space-y-3">
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-2">
                  <div className="w-8 h-8 rounded-lg bg-[#EAF0EC] text-[#1B4332] flex items-center justify-center font-bold text-sm">
                    P
                  </div>
                  <div>
                    <h3 className="font-bold text-base">پارکینگ شماره {formatPersianNumber(p.spaceNumber)}</h3>
                    <span className="text-xs text-[#5E6660]">طبقه {formatPersianNumber(p.floor)}</span>
                  </div>
                </div>
                {p.status === 'OCCUPIED' ? (
                  <AccessibleBadge settings={settings} variant="success" label="اشغال / تخصیص" size="sm" />
                ) : p.status === 'RESERVED' ? (
                  <AccessibleBadge settings={settings} variant="amber" label="رزرو مهمان" size="sm" />
                ) : (
                  <AccessibleBadge settings={settings} variant="neutral" label="خالی" size="sm" />
                )}
              </div>

              <div className="pt-2 border-t border-[#E6E1D8] flex items-center justify-between text-xs">
                <span className="text-[#5E6660]">
                  {p.assignedUnitNumber ? `تخصیص به: واحد ${formatPersianNumber(p.assignedUnitNumber)}` : 'تخصیص نیافته'}
                </span>
                {p.isGuestSpace && (
                  <span className="font-bold text-[#C87D20] bg-[#FDF3E7] px-2 py-0.5 rounded-md">
                    مهمان
                  </span>
                )}
              </div>
            </AccessibleCard>
          ))}
        </div>
      )}

      {/* Content: Storage Grid */}
      {activeTab === 'storage' && (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
          {storageUnits.map((s) => (
            <AccessibleCard key={s.id} settings={settings} className="space-y-3">
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-2">
                  <div className="w-8 h-8 rounded-lg bg-[#FAF8F5] border border-[#E6E1D8] text-[#1B4332] flex items-center justify-center font-bold text-sm">
                    S
                  </div>
                  <div>
                    <h3 className="font-bold text-base">انباری شماره {formatPersianNumber(s.storageNumber)}</h3>
                    <span className="text-xs text-[#5E6660]">طبقه {formatPersianNumber(s.floor)} | متراژ: {formatPersianNumber(s.areaSquareMeters)} م²</span>
                  </div>
                </div>
                {s.status === 'OCCUPIED' ? (
                  <AccessibleBadge settings={settings} variant="success" label="تخصیص‌یافته" size="sm" />
                ) : (
                  <AccessibleBadge settings={settings} variant="neutral" label="خالی" size="sm" />
                )}
              </div>

              <div className="pt-2 border-t border-[#E6E1D8] text-xs text-[#5E6660]">
                {s.assignedUnitNumber ? `تخصیص به: واحد ${formatPersianNumber(s.assignedUnitNumber)}` : 'تخصیص نیافته'}
              </div>
            </AccessibleCard>
          ))}
        </div>
      )}
    </div>
  );
};
