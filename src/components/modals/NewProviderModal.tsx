import React, { useState } from 'react';
import { X, Wrench, Plus, Phone, Star, AlertCircle } from 'lucide-react';
import { AccessibilitySettings, ServiceProvider } from '../../types';
import { AccessibleButton } from '../common/AccessibleButton';

interface NewProviderModalProps {
  settings: AccessibilitySettings;
  isOpen: boolean;
  onClose: () => void;
  onAddProvider: (provider: ServiceProvider) => void;
}

export const NewProviderModal: React.FC<NewProviderModalProps> = ({
  settings,
  isOpen,
  onClose,
  onAddProvider
}) => {
  if (!isOpen) return null;

  const [name, setName] = useState('');
  const [specialty, setSpecialty] = useState('سرویس و نگهداری آسانسور');
  const [phone, setPhone] = useState('');
  const [rating, setRating] = useState('4.8');
  const [error, setError] = useState('');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!name.trim() || !phone.trim()) {
      setError('لطفاً نام سرویس‌کار و شماره تماس را وارد کنید.');
      return;
    }

    const provider: ServiceProvider = {
      id: `sp-${Date.now()}`,
      name: name.trim(),
      specialty,
      phone: phone.trim(),
      rating: parseFloat(rating) || 4.5,
      status: 'ACTIVE'
    };

    onAddProvider(provider);
    onClose();
  };

  return (
    <div 
      className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50 backdrop-blur-xs overflow-y-auto"
      role="dialog"
      aria-modal="true"
      aria-labelledby="new-provider-modal-title"
    >
      <div className="w-full max-w-lg rounded-3xl p-6 sm:p-8 bg-[#FAF8F5] border border-[#E6E1D8] text-[#1E2320] shadow-2xl my-8">
        <div className="flex items-center justify-between pb-4 border-b border-[#E6E1D8]">
          <div className="flex items-center gap-2">
            <Wrench className="w-5 h-5 text-[#1B4332]" />
            <h2 id="new-provider-modal-title" className="text-xl font-bold text-[#1B4332]">
              معرفی سرویس‌کار و کارشناس فنی جدید
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
            <label className="block text-xs font-bold mb-1 text-[#5E6660]">نام و نام خانوادگی / عنوان شرکت *</label>
            <input
              type="text"
              placeholder="مثلاً: مهندس رضایی یا شرکت خدمات تاسیساتی پارس"
              value={name}
              onChange={(e) => {
                setName(e.target.value);
                setError('');
              }}
              className="w-full p-2.5 rounded-xl border border-[#E6E1D8] bg-[#FFFFFF] text-sm focus:outline-none focus:border-[#1B4332]"
              required
            />
          </div>

          <div>
            <label className="block text-xs font-bold mb-1 text-[#5E6660]">حوزه تخصص و مهارت</label>
            <select
              value={specialty}
              onChange={(e) => setSpecialty(e.target.value)}
              className="w-full p-2.5 rounded-xl border border-[#E6E1D8] bg-[#FFFFFF] text-sm focus:outline-none focus:border-[#1B4332]"
            >
              <option value="سرویس و نگهداری آسانسور">سرویس و نگهداری آسانسور</option>
              <option value="تاسیسات، موتورخانه و پمپ آب">تاسیسات، موتورخانه و پمپ آب</option>
              <option value="برق، روشنایی و آیفون تصویری">برق، روشنایی و آیفون تصویری</option>
              <option value="درب ریموت، جک پارکینگ و کرکره">درب ریموت، جک پارکینگ و کرکره</option>
              <option value="خدمات نظافت و شستشوی مشاعات">خدمات نظافت و شستشوی مشاعات</option>
              <option value="باغبانی و نگهداری فضای سبز">باغبانی و نگهداری فضای سبز</option>
              <option value="ایزوگام و عایق‌کاری پشت‌بام">ایزوگام و عایق‌کاری پشت‌بام</option>
            </select>
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-xs font-bold mb-1 text-[#5E6660]">شماره تلفن تماس *</label>
              <input
                type="tel"
                placeholder="0912xxxxxxx"
                value={phone}
                onChange={(e) => {
                  setPhone(e.target.value);
                  setError('');
                }}
                className="w-full p-2.5 rounded-xl border border-[#E6E1D8] bg-[#FFFFFF] text-sm font-mono focus:outline-none focus:border-[#1B4332]"
                required
              />
            </div>

            <div>
              <label className="block text-xs font-bold mb-1 text-[#5E6660]">امتیاز رضایت اولیه (از ۵)</label>
              <input
                type="number"
                step="0.1"
                min="1"
                max="5"
                value={rating}
                onChange={(e) => setRating(e.target.value)}
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
              ثبت در دفترچه سرویس‌کاران
            </AccessibleButton>
          </div>
        </form>
      </div>
    </div>
  );
};
