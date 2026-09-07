import React, { useState } from 'react';
import { X, Home, Plus, Users, Car, Archive, AlertCircle } from 'lucide-react';
import { AccessibilitySettings, Building, Unit } from '../../types';
import { AccessibleButton } from '../common/AccessibleButton';

interface NewUnitModalProps {
  settings: AccessibilitySettings;
  isOpen: boolean;
  onClose: () => void;
  building: Building;
  onAddUnit: (unit: Unit) => void;
}

export const NewUnitModal: React.FC<NewUnitModalProps> = ({
  settings,
  isOpen,
  onClose,
  building,
  onAddUnit
}) => {
  if (!isOpen) return null;

  const [unitNumber, setUnitNumber] = useState('');
  const [floor, setFloor] = useState('1');
  const [area, setArea] = useState('100');
  const [residentCount, setResidentCount] = useState('2');
  const [parkingCount, setParkingCount] = useState('1');
  const [storageCount, setStorageCount] = useState('1');
  const [occupancyStatus, setOccupancyStatus] = useState<'OCCUPIED' | 'VACANT'>('OCCUPIED');
  const [ownerName, setOwnerName] = useState('');
  const [tenantName, setTenantName] = useState('');
  const [error, setError] = useState('');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!unitNumber.trim()) {
      setError('لطفاً شماره واحد را وارد نمایید.');
      return;
    }

    const newUnit: Unit = {
      id: `u-${Date.now()}`,
      buildingId: building.id,
      unitNumber: unitNumber.trim(),
      floor: parseInt(floor, 10) || 1,
      areaSquareMeters: parseFloat(area) || 100,
      residentCount: occupancyStatus === 'VACANT' ? 0 : (parseInt(residentCount, 10) || 0),
      parkingCount: parseInt(parkingCount, 10) || 0,
      storageCount: parseInt(storageCount, 10) || 0,
      occupancyStatus,
      ownerName: ownerName.trim() || undefined,
      tenantName: occupancyStatus === 'OCCUPIED' && tenantName.trim() ? tenantName.trim() : undefined,
      isActive: true
    };

    onAddUnit(newUnit);
    onClose();
  };

  return (
    <div 
      className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50 backdrop-blur-xs overflow-y-auto"
      role="dialog"
      aria-modal="true"
      aria-labelledby="new-unit-modal-title"
    >
      <div className="w-full max-w-lg rounded-3xl p-6 sm:p-8 bg-[#FAF8F5] border border-[#E6E1D8] text-[#1E2320] shadow-2xl my-8">
        <div className="flex items-center justify-between pb-4 border-b border-[#E6E1D8]">
          <div className="flex items-center gap-2">
            <Home className="w-5 h-5 text-[#1B4332]" />
            <h2 id="new-unit-modal-title" className="text-xl font-bold text-[#1B4332]">
              افزودن واحد جدید به {building.name}
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
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-xs font-bold mb-1 text-[#5E6660]">شماره واحد *</label>
              <input
                type="text"
                placeholder="مثلاً: ۱۰۳ یا ۴۰۲"
                value={unitNumber}
                onChange={(e) => {
                  setUnitNumber(e.target.value);
                  setError('');
                }}
                className="w-full p-2.5 rounded-xl border border-[#E6E1D8] bg-[#FFFFFF] text-sm focus:outline-none focus:border-[#1B4332]"
                required
              />
            </div>
            <div>
              <label className="block text-xs font-bold mb-1 text-[#5E6660]">طبقه</label>
              <input
                type="number"
                value={floor}
                onChange={(e) => setFloor(e.target.value)}
                className="w-full p-2.5 rounded-xl border border-[#E6E1D8] bg-[#FFFFFF] text-sm focus:outline-none focus:border-[#1B4332]"
              />
            </div>
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-xs font-bold mb-1 text-[#5E6660]">متراژ مساحت (مترمربع)</label>
              <input
                type="number"
                value={area}
                onChange={(e) => setArea(e.target.value)}
                placeholder="۱۰۰"
                className="w-full p-2.5 rounded-xl border border-[#E6E1D8] bg-[#FFFFFF] text-sm focus:outline-none focus:border-[#1B4332]"
              />
            </div>
            <div>
              <label className="block text-xs font-bold mb-1 text-[#5E6660]">وضعیت سکونت</label>
              <select
                value={occupancyStatus}
                onChange={(e) => setOccupancyStatus(e.target.value as 'OCCUPIED' | 'VACANT')}
                className="w-full p-2.5 rounded-xl border border-[#E6E1D8] bg-[#FFFFFF] text-sm focus:outline-none focus:border-[#1B4332]"
              >
                <option value="OCCUPIED">دارای سکونت (فعال)</option>
                <option value="VACANT">خالی از سکونت</option>
              </select>
            </div>
          </div>

          {occupancyStatus === 'OCCUPIED' && (
            <div>
              <label className="block text-xs font-bold mb-1 text-[#5E6660]">تعداد ساکنین مقیم</label>
              <input
                type="number"
                min="1"
                value={residentCount}
                onChange={(e) => setResidentCount(e.target.value)}
                className="w-full p-2.5 rounded-xl border border-[#E6E1D8] bg-[#FFFFFF] text-sm focus:outline-none focus:border-[#1B4332]"
              />
            </div>
          )}

          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-xs font-bold mb-1 text-[#5E6660]">تعداد پارکینگ اختصاصی</label>
              <input
                type="number"
                value={parkingCount}
                onChange={(e) => setParkingCount(e.target.value)}
                className="w-full p-2.5 rounded-xl border border-[#E6E1D8] bg-[#FFFFFF] text-sm focus:outline-none focus:border-[#1B4332]"
              />
            </div>
            <div>
              <label className="block text-xs font-bold mb-1 text-[#5E6660]">تعداد انباری</label>
              <input
                type="number"
                value={storageCount}
                onChange={(e) => setStorageCount(e.target.value)}
                className="w-full p-2.5 rounded-xl border border-[#E6E1D8] bg-[#FFFFFF] text-sm focus:outline-none focus:border-[#1B4332]"
              />
            </div>
          </div>

          <div className="grid grid-cols-2 gap-3 pt-2 border-t border-[#E6E1D8]">
            <div>
              <label className="block text-xs font-bold mb-1 text-[#5E6660]">نام مالک واحد</label>
              <input
                type="text"
                placeholder="مثلاً: احمد رضایی"
                value={ownerName}
                onChange={(e) => setOwnerName(e.target.value)}
                className="w-full p-2.5 rounded-xl border border-[#E6E1D8] bg-[#FFFFFF] text-sm focus:outline-none focus:border-[#1B4332]"
              />
            </div>
            <div>
              <label className="block text-xs font-bold mb-1 text-[#5E6660]">نام مستأجر (اختیاری)</label>
              <input
                type="text"
                placeholder="در صورت استیجاری بودن"
                value={tenantName}
                onChange={(e) => setTenantName(e.target.value)}
                className="w-full p-2.5 rounded-xl border border-[#E6E1D8] bg-[#FFFFFF] text-sm focus:outline-none focus:border-[#1B4332]"
              />
            </div>
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
              variant="primary"
              icon={<Plus className="w-4 h-4" />}
              onClick={() => {}}
            >
              ثبت و ایجاد واحد
            </AccessibleButton>
          </div>
        </form>
      </div>
    </div>
  );
};
