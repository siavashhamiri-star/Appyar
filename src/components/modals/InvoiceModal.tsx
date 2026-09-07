import React, { useState } from 'react';
import { 
  X, 
  Printer, 
  CreditCard, 
  CheckCircle2, 
  Building2, 
  Calendar, 
  QrCode, 
  FileText,
  DollarSign,
  AlertCircle
} from 'lucide-react';
import { AccessibilitySettings, Building, ChargeItem, ChargePeriod, Unit, BuildingExpense } from '../../types';
import { AccessibleButton } from '../common/AccessibleButton';
import { AccessibleBadge } from '../common/AccessibleBadge';
import { formatPersianNumber, formatRial, formatPersianDate } from '../../theme/designSystem';

interface InvoiceModalProps {
  settings: AccessibilitySettings;
  isOpen: boolean;
  onClose: () => void;
  building: Building;
  period: ChargePeriod;
  item: ChargeItem;
  unit?: Unit;
  expenses: BuildingExpense[];
  onOpenPaymentGateway: (item: ChargeItem) => void;
}

export const InvoiceModal: React.FC<InvoiceModalProps> = ({
  settings,
  isOpen,
  onClose,
  building,
  period,
  item,
  unit,
  expenses,
  onOpenPaymentGateway
}) => {
  if (!isOpen) return null;

  const isHighContrast = settings.mode === 'high-contrast';
  const isEasy = settings.mode === 'easy';

  const invoiceNumber = `INV-${building.id.slice(0, 3).toUpperCase()}-${item.id.slice(-5).toUpperCase()}`;
  const billId = `10405${item.unitNumber}9`;
  const paymentId = `9088${Math.round(item.finalAmount / 1000)}7`;

  const handlePrint = () => {
    window.print();
  };

  const periodExpenses = expenses.filter(e => e.chargePeriodId === period.id);

  return (
    <div 
      className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-xs overflow-y-auto"
      role="dialog"
      aria-modal="true"
      aria-labelledby="invoice-title"
    >
      <div className={`w-full max-w-2xl rounded-3xl p-6 sm:p-8 border shadow-2xl transition-all my-8 ${
        isHighContrast 
          ? 'bg-[#121212] border-2 border-[#FFFFFF] text-[#FFFFFF]' 
          : 'bg-[#FFFFFF] border-[#E6E1D8] text-[#1E2320]'
      }`}>
        {/* Modal Action Header (Non-printable) */}
        <div className="flex items-center justify-between pb-4 border-b border-[#E6E1D8] print:hidden">
          <div className="flex items-center gap-2">
            <FileText className="w-5 h-5 text-[#1B4332]" />
            <h2 id="invoice-title" className="text-lg font-bold text-[#1B4332]">
              صورت‌حساب و فاکتور رسمی شارژ ساختمان
            </h2>
          </div>
          <div className="flex items-center gap-2">
            <button
              onClick={handlePrint}
              className="p-2 rounded-xl border border-[#E6E1D8] hover:bg-[#FAF8F5] text-[#1B4332] text-xs font-bold flex items-center gap-1.5 cursor-pointer"
              title="چاپ فاکتور یا ذخیره PDF"
            >
              <Printer className="w-4 h-4" />
              <span>چاپ / PDF</span>
            </button>
            <button 
              onClick={onClose} 
              className="p-2 rounded-xl hover:bg-black/5 text-[#5E6660] cursor-pointer"
              aria-label="بستن پنجره فاکتور"
            >
              <X className="w-5 h-5" />
            </button>
          </div>
        </div>

        {/* Printable Official Invoice Body */}
        <div className="mt-6 space-y-6 print:m-0 print:p-0">
          {/* Header with Seal & Building Title */}
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 p-5 rounded-2xl bg-[#FAF8F5] border border-[#E6E1D8]">
            <div className="flex items-center gap-3">
              <div className="w-12 h-12 rounded-2xl bg-[#1B4332] text-[#FFFFFF] flex items-center justify-center font-black">
                <Building2 className="w-6 h-6" />
              </div>
              <div>
                <h3 className="font-extrabold text-base sm:text-lg text-[#1B4332]">{building.name}</h3>
                <span className="text-xs text-[#5E6660] block">{building.address}</span>
              </div>
            </div>

            <div className="text-left sm:text-right border-t sm:border-t-0 sm:border-r border-[#E6E1D8] pt-2 sm:pt-0 sm:pr-4 text-xs space-y-1">
              <div>شماره فاکتور: <span className="font-mono font-bold">{invoiceNumber}</span></div>
              <div>دوره: <span className="font-bold text-[#1B4332]">{period.title}</span></div>
              <div>مهلت پرداخت: <span className="font-bold">{formatPersianDate(period.dueDate)}</span></div>
            </div>
          </div>

          {/* Unit & Resident Info Bar */}
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 text-xs">
            <div className="p-3 rounded-xl bg-[#FAF8F5] border border-[#E6E1D8]">
              <span className="text-[#858E87] block">شماره واحد:</span>
              <span className="font-extrabold text-sm text-[#1B4332]">واحد {formatPersianNumber(item.unitNumber)}</span>
            </div>
            <div className="p-3 rounded-xl bg-[#FAF8F5] border border-[#E6E1D8]">
              <span className="text-[#858E87] block">نام ساکن / مالک:</span>
              <span className="font-bold text-sm text-[#1E2320]">{unit?.tenantName || unit?.ownerName || 'نامشخص'}</span>
            </div>
            <div className="p-3 rounded-xl bg-[#FAF8F5] border border-[#E6E1D8]">
              <span className="text-[#858E87] block">متراژ واحد:</span>
              <span className="font-bold text-sm text-[#1E2320]">{formatPersianNumber(unit?.areaSquareMeters || 100)} م²</span>
            </div>
            <div className="p-3 rounded-xl bg-[#FAF8F5] border border-[#E6E1D8]">
              <span className="text-[#858E87] block">تعداد ساکنین:</span>
              <span className="font-bold text-sm text-[#1E2320]">{formatPersianNumber(unit?.residentCount || 2)} نفر</span>
            </div>
          </div>

          {/* Itemized Expenses Breakdown */}
          <div>
            <h4 className="font-bold text-sm mb-2.5 text-[#1B4332]">ریز هزینه‌های مشاع ساختمان در این دوره:</h4>
            <div className="overflow-x-auto rounded-xl border border-[#E6E1D8]">
              <table className="w-full text-right text-xs">
                <thead className="bg-[#FAF8F5] border-b border-[#E6E1D8] text-[#5E6660]">
                  <tr>
                    <th className="p-2.5">شرح هزینه</th>
                    <th className="p-2.5">دسته‌بندی</th>
                    <th className="p-2.5">مبلغ کل هزینه</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-[#E6E1D8]">
                  {periodExpenses.length > 0 ? (
                    periodExpenses.map(exp => (
                      <tr key={exp.id}>
                        <td className="p-2.5 font-medium">{exp.title}</td>
                        <td className="p-2.5 text-[#5E6660]">{exp.category}</td>
                        <td className="p-2.5 font-bold text-[#1E2320]">{formatRial(exp.amount)}</td>
                      </tr>
                    ))
                  ) : (
                    <tr>
                      <td colSpan={3} className="p-3 text-center text-[#5E6660]">
                        هزینه‌های عمومی و نگهداری ماهانه مصوب هیئت مدیره
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </div>

          {/* Financial Calculation Summary */}
          <div className="p-4 rounded-2xl bg-[#EAF0EC]/60 border border-[#1B4332]/20 space-y-2">
            <div className="flex items-center justify-between text-xs text-[#5E6660]">
              <span>مبلغ پایه تسهیم شده برای این واحد:</span>
              <span className="font-bold text-[#1E2320]">{formatRial(item.baseAmount)}</span>
            </div>
            {item.adjustments !== 0 && (
              <div className="flex items-center justify-between text-xs text-[#5E6660]">
                <span>تعدیلات / متفرقه:</span>
                <span className="font-bold">{formatRial(item.adjustments)}</span>
              </div>
            )}
            <div className="flex items-center justify-between pt-2 border-t border-[#1B4332]/20 text-sm font-extrabold text-[#1B4332]">
              <span>مبلغ نهایی قابل پرداخت:</span>
              <span className="text-base sm:text-lg">{formatRial(item.finalAmount)}</span>
            </div>
            <div className="flex items-center justify-between text-xs text-[#5E6660]">
              <span>معادل به تومان:</span>
              <span className="font-bold text-[#1B4332]">{formatPersianNumber(Math.round(item.finalAmount / 10))} تومان</span>
            </div>
          </div>

          {/* Payment Identifiers & Simulated Barcode */}
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 p-4 rounded-2xl bg-[#FAF8F5] border border-[#E6E1D8] text-xs">
            <div className="space-y-1.5">
              <div>شناسه قبض: <span className="font-mono font-bold text-sm">{billId}</span></div>
              <div>شناسه پرداخت: <span className="font-mono font-bold text-sm">{paymentId}</span></div>
              <div className="text-[#858E87] pt-1">
                پرداخت از طریق کلیه کارت‌های عضو شتاب، خودپرداز و اپلیکیشن اپیار مجاز است.
              </div>
            </div>

            <div className="flex items-center justify-end gap-3">
              <div className="text-left text-[11px] text-[#858E87]">
                <div>کد رهگیری هوشمند اپیار</div>
                <div className="font-mono">{invoiceNumber}</div>
              </div>
              <div className="w-16 h-16 bg-[#FFFFFF] p-1 rounded-lg border border-[#E6E1D8] flex items-center justify-center">
                <QrCode className="w-12 h-12 text-[#1B4332]" />
              </div>
            </div>
          </div>

          {/* Status Display */}
          <div className="flex items-center justify-between pt-2">
            <span className="text-xs text-[#5E6660]">وضعیت کنونی فاکتور:</span>
            {item.status === 'PAID' || item.paidAmount >= item.finalAmount ? (
              <span className="inline-flex items-center gap-1.5 text-xs font-bold text-[#1B4332] bg-[#EAF0EC] px-3 py-1 rounded-lg border border-[#1B4332]/30">
                <CheckCircle2 className="w-4 h-4" />
                <span>تسویه شده در تاریخ {formatPersianDate(Date.now())}</span>
              </span>
            ) : (
              <span className="inline-flex items-center gap-1.5 text-xs font-bold text-[#A4422E] bg-[#FBEBE8] px-3 py-1 rounded-lg border border-[#A4422E]/30">
                <AlertCircle className="w-4 h-4" />
                <span>در انتظار پرداخت و تسویه</span>
              </span>
            )}
          </div>
        </div>

        {/* Footer Actions (Non-printable) */}
        <div className="mt-8 pt-4 border-t border-[#E6E1D8] flex items-center justify-between gap-3 print:hidden flex-wrap">
          <AccessibleButton
            settings={settings}
            variant="ghost"
            onClick={onClose}
          >
            بستن
          </AccessibleButton>

          {item.paidAmount < item.finalAmount && (
            <AccessibleButton
              settings={settings}
              variant="primary"
              icon={<CreditCard className="w-4 h-4" />}
              onClick={() => {
                onClose();
                onOpenPaymentGateway(item);
              }}
            >
              پرداخت آنلاین شارژ ({formatRial(item.finalAmount)})
            </AccessibleButton>
          )}
        </div>
      </div>
    </div>
  );
};
