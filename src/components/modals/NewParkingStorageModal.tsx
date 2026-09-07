import React, { useState } from 'react';
import { X, Car, Archive, Plus, AlertCircle } from 'lucide-react';
import { AccessibilitySettings, Building, ParkingSpace, StorageUnit, Unit } from '../../types';
import { AccessibleButton } from '../common/AccessibleButton';

interface NewParkingStorageModalProps {
  settings: AccessibilitySettings;
  isOpen: boolean;
  onClose: () => void;
  building: Building;
  units: Unit[];
  initialType?: 'parking' | 'storage';
  onAddParking: (space: ParkingSpace) => void;
  onAddStorage: (unit: StorageUnit) => void;
}

export const NewParkingStorageModal: React.FC<NewParkingStorageModalProps> = ({
  settings,
  isOpen,
  onClose,
  building,
  units,
  initialType = 'parking',
  onAddParking,
  onAddStorage
}) => {
  if (!isOpen) return null;

  const [type, setType] = useState<'parking' | 'storage'>(initialType);
  const [number, setNumber] = useState('');
  const [floor, setFloor] = useState('-1');
  const [area, setArea] = useState('4.5');
  const [isGuest, setIsGuest] = useState(false);
  const [assignedUnitId, setAssignedUnitId] = useState<string>('');
  const [error, setError] = useState('');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!number.trim()) {
      setError('لطفاً شماره فضا را وارد کنید.');
      return;
    }

    const assignedUnit = units.find(u => u.id === assignedUnitId);

    if (type === 'parking') {
      const space: ParkingSpace = {
        id: `ps-${Date.now()}`,
        buildingId: building.id,
        spaceNumber: number.trim(),
        floor: parseInt(floor, 10) || -1,
        assignedUnitId: isGuest ? undefined : assignedUnit?.id,
        assignedUnitNumber: isGuest ? undefined : assignedUnit?.unitNumber,
        isGuestSpace: isGuest,
        status: isGuest ? 'VACANT' : (assignedUnit ? 'OCCUPIED' : 'VACANT')
      };
      onAddParking(space);
    } else {
      const storage: StorageUnit = {
        id: `su-${Date.now()}`,
        buildingId: building.id,
        storageNumber: number.trim(),
        floor: parseInt(floor, 10) || -1,
        areaSquareMeters: parseFloat(area) || 4.0,
        assignedUnitId: assignedUnit?.id,
        assignedUnitNumber: assignedUnit?.unitNumber,
        status: assignedUnit ? 'OCCUPIED' : 'VACANT'
      };
      onAddStorage(storage);
    }

    onClose();
  };

  return (
    <div 
      className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50 backdrop-blur-xs overflow-y-auto"
      role="dialog"
      aria-modal="true"
      aria-labelledby="new-space-modal-title"
    >
      <div className="w-full max-w-lg rounded-3xl p-6 sm:p-8 bg-[#FAF8F5] border border-[#E6E1D8] text-[#1E2320] shadow-2xl my-8">
        <div className="flex items-center justify-between pb-4 border-b border-[#E6E1D8]">
          <div className="flex items-center gap-2">
            {type === 'parking' ? <Car className="w-5 h-5 text-[#1B4332]" /> : <Archive className="w-5 h-5 text-[#C87D20]" />}
            <h2 id="new-space-modal-title" className="text-xl font-bold text-[#1B4332]">
              تعریف و تخصیص {type === 'parking' ? 'جای پارک جدید' : 'انباری جدید'}
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

        {/* Type toggle */}
        <div className="flex items-center gap-2 mt-4 p-1 bg-[#EAF0EC] rounded-xl">
          <button
            type="button"
            onClick={() => setType('parking')}
            className={`flex-1 py-2 rounded-lg text-xs font-bold transition-all cursor-pointer ${
              type === 'parking' ? 'bg-[#1B4332] text-[#FFFFFF]' : 'text-[#5E6660]'
            }`}
          >
            جای پارک خودرو
          </button>
          <button
            type="button"
            onClick={() => setType('storage')}
            className={`flex-1 py-2 rounded-lg text-xs font-bold transition-all cursor-pointer ${
              type === 'storage' ? 'bg-[#1B4332] text-[#FFFFFF]' : 'text-[#5E6660]'
            }`}
          >
            واحد انباری
          </button>
        </div>

        {error && (
          <div className="mt-4 p-3 rounded-xl bg-[#FBEBE8] border border-[#A4422E]/30 text-[#A4422E] text-xs flex items-center gap-2">
            <AlertCircle className="w-4 h-4 flex-shrink-0" />
            <span>{error}</span>
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-4 mt-4">
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-xs font-bold mb-1 text-[#5E6660]">
                {type === 'parking' ? 'شماره جای پارک *' : 'شماره انباری *'}
              </label>
              <input
                type="text"
                placeholder="مثلاً: ۷ یا P-08"
                value={number}
                onChange={(e) => {
                  setNumber(e.target.value);
                  setError('');
                }}
                className="w-full p-2.5 rounded-xl border border-[#E6E1D8] bg-[#FFFFFF] text-sm focus:outline-none focus:border-[#1B4332]"
                required
              />
            </div>

            <div>
              <label className="block text-xs font-bold mb-1 text-[#5E6660]">شماره طبقه</label>
              <input
                type="number"
                value={floor}
                onChange={(e) => setFloor(e.target.value)}
                placeholder="-1"
                className="w-full p-2.5 rounded-xl border border-[#E6E1D8] bg-[#FFFFFF] text-sm focus:outline-none focus:border-[#1B4332]"
              />
            </div>
          </div>

          {type === 'storage' && (
            <div>
              <label className="block text-xs font-bold mb-1 text-[#5E6660]">متراژ مساحت انباری (مترمربع)</label>
              <input
                type="number"
                step="0.1"
                value={area}
                onChange={(e) => setArea(e.target.value)}
                className="w-full p-2.5 rounded-xl border border-[#E6E1D8] bg-[#FFFFFF] text-sm focus:outline-none focus:border-[#1B4332]"
              />
            </div>
          )}

          {type === 'parking' && (
            <div className="flex items-center gap-2 p-3 bg-[#FFFFFF] rounded-xl border border-[#E6E1D8]">
              <input
                type="checkbox"
                id="isGuestCheckbox"
                checked={isGuest}
                onChange={(e) => {
                  setIsGuest(e.target.checked);
                  if (e.target.checked) setAssignedUnitId('');
                }}
                className="w-4 h-4 text-[#1B4332] rounded cursor-pointer"
              />
              <label htmlFor="isGuestCheckbox" className="text-xs font-bold text-[#1E2320] cursor-pointer">
                این فضا پارکینگ مشترک مهمان است (بدون تخصیص دائم به واحد)
              </label>
            </div>
          )}

          {!isGuest && (
            <div>
              <label className="block text-xs font-bold mb-1 text-[#5E6660]">تخصیص به واحد</label>
              <select
                value={assignedUnitId}
                onChange={(e) => setAssignedUnitId(e.target.value)}
                className="w-full p-2.5 rounded-xl border border-[#E6E1D8] bg-[#FFFFFF] text-sm focus:outline-none focus:border-[#1B4332]"
              >
                <option value="">-- بدون تخصیص (فعلاً خالی) --</option>
                {units.map(u => (
                  <option key={u.id} value={u.id}>
                    واحد {u.unitNumber} (طبقه {u.floor}) - {u.ownerName || 'ساکن'}
                  </option>
                ))}
              </select>
            </div>
          )}

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
              ثبت فضا
            </AccessibleButton>
          </div>
        </form>
      </div>
    </div>
  );
};
