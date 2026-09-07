import React, { useState } from 'react';
import { 
  Calculator, 
  Plus, 
  CheckCircle2, 
  FileSpreadsheet, 
  Scale, 
  Sliders, 
  AlertCircle, 
  DollarSign, 
  Calendar, 
  Receipt, 
  ArrowLeft,
  PieChart,
  Edit2
} from 'lucide-react';
import { 
  AccessibilitySettings, 
  Building, 
  ChargePeriod, 
  ChargeCalculationMethod, 
  ChargeItem, 
  BuildingExpense, 
  MethodType 
} from '../../types';
import { AccessibleCard } from '../common/AccessibleCard';
import { AccessibleButton } from '../common/AccessibleButton';
import { AccessibleBadge } from '../common/AccessibleBadge';
import { formatPersianNumber, formatRial, formatPersianDate } from '../../theme/designSystem';

interface ChargeCalculationSectionProps {
  settings: AccessibilitySettings;
  activeBuilding: Building;
  periods: ChargePeriod[];
  methods: ChargeCalculationMethod[];
  expenses: BuildingExpense[];
  chargeItems: ChargeItem[];
  onCalculateCharges: (periodId: string, methodId: string) => void;
  onFinalizePeriod: (periodId: string) => void;
  onOpenNewPeriodModal: () => void;
  onOpenNewExpenseModal: () => void;
  onOpenNewMethodModal: () => void;
  onViewInvoice?: (item: ChargeItem, period: ChargePeriod) => void;
  onOpenPayment?: (item: ChargeItem, period: ChargePeriod) => void;
}

export const ChargeCalculationSection: React.FC<ChargeCalculationSectionProps> = ({
  settings,
  activeBuilding,
  periods,
  methods,
  expenses,
  chargeItems,
  onCalculateCharges,
  onFinalizePeriod,
  onOpenNewPeriodModal,
  onOpenNewExpenseModal,
  onOpenNewMethodModal,
  onViewInvoice,
  onOpenPayment
}) => {
  const isHighContrast = settings.mode === 'high-contrast';
  const isEasy = settings.mode === 'easy';

  const [selectedPeriodId, setSelectedPeriodId] = useState<string>(periods[0]?.id || '');
  const [selectedMethodId, setSelectedMethodId] = useState<string>(methods[0]?.id || '');

  const currentPeriod = periods.find(p => p.id === selectedPeriodId) || periods[0];
  const currentItems = chargeItems.filter(item => item.chargePeriodId === selectedPeriodId);
  const currentExpenses = expenses.filter(e => e.chargePeriodId === selectedPeriodId);

  const totalCalculated = currentItems.reduce((sum, item) => sum + item.finalAmount, 0);
  const totalPaid = currentItems.reduce((sum, item) => sum + item.paidAmount, 0);

  const getMethodTypeTitle = (type: MethodType) => {
    switch (type) {
      case 'EQUAL': return 'تسهیم مساوی';
      case 'AREA_BASED': return 'بر مبنای متراژ واحدها';
      case 'RESIDENT_BASED': return 'بر مبنای تعداد ساکنین';
      case 'MIXED': return 'ترکیبی (متراژ + نفرات)';
      case 'CUSTOM': return 'فرمول سفارشی';
    }
  };

  const getStatusBadge = (status: ChargePeriod['status']) => {
    switch (status) {
      case 'DRAFT': return <AccessibleBadge settings={settings} variant="neutral" label="پیش‌نویس" />;
      case 'CALCULATED': return <AccessibleBadge settings={settings} variant="amber" label="محاسبه‌شده" />;
      case 'FINALIZED':
      case 'SETTLED': return <AccessibleBadge settings={settings} variant="success" label="قطعی / تسویه‌شده" />;
      case 'PUBLISHED': return <AccessibleBadge settings={settings} variant="sage" label="منتشرشده" />;
      default: return <AccessibleBadge settings={settings} variant="neutral" label={status} />;
    }
  };

  return (
    <div className="space-y-8" role="main" aria-label="مدیریت مالی و محاسبه شارژ ساختمان">
      {/* Header & Quick Action */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className={`font-extrabold ${isEasy ? 'text-3xl sm:text-4xl text-[#1B4332]' : 'text-2xl sm:text-3xl'}`}>
            محاسبه و تسهیم شارژ ساختمان
          </h1>
          <p className={`mt-1 text-[#5E6660] ${isEasy ? 'text-lg font-medium' : 'text-sm'}`}>
            دوره‌های شارژ، ثبت هزینه‌ها، انتخاب فرمول‌های محاسبه و تسهیم دقیق ریالی
          </p>
        </div>

        <div className="flex items-center gap-3 flex-wrap">
          <AccessibleButton
            settings={settings}
            variant="primary"
            icon={<Plus className="w-5 h-5" />}
            onClick={onOpenNewPeriodModal}
          >
            تعریف دوره شارژ جدید
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

      {/* Period Selection & Period Details */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Active Periods List */}
        <div className="space-y-4">
          <h2 className={`font-bold ${isEasy ? 'text-2xl text-[#1B4332]' : 'text-lg'}`}>
            دوره‌های مالی ساختمان
          </h2>
          <div className="space-y-2.5">
            {periods.map((period) => {
              const isSelected = period.id === selectedPeriodId;
              return (
                <button
                  key={period.id}
                  onClick={() => setSelectedPeriodId(period.id)}
                  className={`w-full text-right p-4 rounded-2xl border transition-all cursor-pointer ${
                    isSelected
                      ? isHighContrast
                        ? 'bg-[#FFFFFF] text-[#000000] border-2 border-[#FFFFFF] font-bold'
                        : 'bg-[#EAF0EC] text-[#1B4332] border-[#1B4332] ring-2 ring-[#1B4332]/20 font-bold shadow-xs'
                      : isHighContrast
                        ? 'bg-[#181818] text-[#FFFFFF] border-[#333333]'
                        : 'bg-[#FFFFFF] text-[#1E2320] border-[#E6E1D8] hover:bg-[#FAF8F5]'
                  }`}
                >
                  <div className="flex items-center justify-between">
                    <span className="text-base font-bold">{period.title}</span>
                    {getStatusBadge(period.status)}
                  </div>
                  <div className="mt-2 flex items-center justify-between text-xs text-[#5E6660]">
                    <span>هزینه کل: {formatRial(period.totalBuildingCost)}</span>
                    <span>سررسید: {formatPersianDate(period.dueDate)}</span>
                  </div>
                </button>
              );
            })}
          </div>
        </div>

        {/* Calculation Control & Method Selection */}
        {currentPeriod && (
          <div className="lg:col-span-2 space-y-6">
            <AccessibleCard settings={settings} className="space-y-6">
              <div className="flex flex-col sm:flex-row sm:items-center justify-between pb-4 border-b border-[#E6E1D8] gap-3">
                <div>
                  <h3 className={`font-extrabold ${isEasy ? 'text-2xl text-[#1B4332]' : 'text-xl'}`}>
                    مشخصات دوره: {currentPeriod.title}
                  </h3>
                  <span className="text-xs text-[#5E6660] block mt-1">
                    بازه زمانی: {formatPersianDate(currentPeriod.periodStart)} تا {formatPersianDate(currentPeriod.periodEnd)}
                  </span>
                </div>
                <div className="flex items-center gap-2">
                  {getStatusBadge(currentPeriod.status)}
                </div>
              </div>

              {/* Method Selector */}
              <div>
                <label className="block text-sm font-bold mb-2">روش تسهیم و فرمول محاسبه</label>
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                  {methods.map((m) => {
                    const isSelected = m.id === (selectedMethodId || currentPeriod.calculationMethodId);
                    return (
                      <button
                        key={m.id}
                        onClick={() => setSelectedMethodId(m.id)}
                        disabled={currentPeriod.status === 'FINALIZED'}
                        className={`p-3.5 rounded-xl border text-right transition-all cursor-pointer ${
                          isSelected
                            ? isHighContrast
                              ? 'bg-[#FFFFFF] text-[#000000] border-2 border-[#FFFFFF] font-bold'
                              : 'bg-[#EAF0EC] text-[#1B4332] border-[#1B4332] font-bold'
                            : isHighContrast
                              ? 'bg-[#1E1E1E] text-[#FFFFFF] border-[#333333]'
                              : 'bg-[#FFFFFF] text-[#1E2320] border-[#E6E1D8] hover:bg-[#FAF8F5]'
                        }`}
                      >
                        <div className="flex items-center justify-between">
                          <span className="text-sm font-bold">{m.name}</span>
                          <span className="text-xs text-[#5E6660]">{getMethodTypeTitle(m.methodType)}</span>
                        </div>
                        {m.description && (
                          <p className="text-xs text-[#5E6660] mt-1 line-clamp-1">{m.description}</p>
                        )}
                      </button>
                    );
                  })}
                </div>
              </div>

              {/* Period Expenses Breakdown */}
              <div>
                <div className="flex items-center justify-between mb-3">
                  <h4 className="text-sm font-bold">هزینه‌های ثبت‌شده در این دوره ({currentExpenses.length} فقره)</h4>
                  <span className="text-xs text-[#5E6660]">
                    مجموع: {formatRial(currentExpenses.reduce((s, e) => s + e.amount, 0))}
                  </span>
                </div>

                {currentExpenses.length === 0 ? (
                  <p className="text-xs text-[#5E6660] py-3 text-center bg-[#FAF8F5] rounded-xl border border-dashed border-[#E6E1D8]">
                    هزینه‌ای مستقیماً به این دوره الصاق نشده است؛ مبلغ کل دوره مبنای محاسبه خواهد بود.
                  </p>
                ) : (
                  <div className="space-y-2">
                    {currentExpenses.map((exp) => (
                      <div
                        key={exp.id}
                        className="flex items-center justify-between p-3 rounded-xl bg-[#FAF8F5] border border-[#E6E1D8] text-xs"
                      >
                        <div>
                          <span className="font-bold block">{exp.title}</span>
                          <span className="text-[#858E87]">{exp.category}</span>
                        </div>
                        <span className="font-bold text-[#1B4332]">{formatRial(exp.amount)}</span>
                      </div>
                    ))}
                  </div>
                )}
              </div>

              {/* Action Buttons */}
              <div className="flex items-center justify-end gap-3 pt-4 border-t border-[#E6E1D8]">
                {currentPeriod.status !== 'FINALIZED' && (
                  <>
                    <AccessibleButton
                      settings={settings}
                      variant="primary"
                      icon={<Calculator className="w-4 h-4" />}
                      onClick={() => onCalculateCharges(currentPeriod.id, selectedMethodId)}
                    >
                      محاسبه و تسهیم مجدد شارژ
                    </AccessibleButton>
                    <AccessibleButton
                      settings={settings}
                      variant="accent"
                      icon={<CheckCircle2 className="w-4 h-4" />}
                      onClick={() => onFinalizePeriod(currentPeriod.id)}
                    >
                      قطعی‌سازی و صدور قبوض
                    </AccessibleButton>
                  </>
                )}
              </div>
            </AccessibleCard>
          </div>
        )}
      </div>

      {/* Charge Items Table / Breakdown */}
      {currentItems.length > 0 && (
        <div className="space-y-4">
          <div className="flex items-center justify-between">
            <h2 className={`font-bold ${isEasy ? 'text-2xl text-[#1B4332]' : 'text-lg'}`}>
              جدول تسهیم شارژ واحدها ({currentItems.length} واحد)
            </h2>
            <span className="text-xs sm:text-sm font-bold text-[#1B4332]">
              جمع کل شارژ صادرشده: {formatRial(totalCalculated)}
            </span>
          </div>

          <div className="overflow-x-auto rounded-2xl border border-[#E6E1D8] bg-[#FFFFFF]">
            <table className="w-full text-right text-sm">
              <thead className="bg-[#FAF8F5] border-b border-[#E6E1D8] text-[#5E6660]">
                <tr>
                  <th className="p-3.5 font-bold">واحد</th>
                  <th className="p-3.5 font-bold">مبلغ پایه تسهیم</th>
                  <th className="p-3.5 font-bold">تعدیلات و متفرقه</th>
                  <th className="p-3.5 font-bold">مبلغ نهایی شارژ</th>
                  <th className="p-3.5 font-bold">پرداخت شده</th>
                  <th className="p-3.5 font-bold">وضعیت تسویه</th>
                  <th className="p-3.5 font-bold text-center">عملیات و پرداخت</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-[#E6E1D8]">
                {currentItems.map((item) => (
                  <tr key={item.id} className="hover:bg-[#FAF8F5]/60 transition-colors">
                    <td className="p-3.5 font-bold text-[#1B4332]">
                      واحد {formatPersianNumber(item.unitNumber)}
                    </td>
                    <td className="p-3.5 text-[#5E6660]">
                      {formatRial(item.baseAmount)}
                    </td>
                    <td className="p-3.5 text-[#5E6660]">
                      {item.adjustments > 0 ? `+${formatRial(item.adjustments)}` : '۰'}
                    </td>
                    <td className="p-3.5 font-bold text-[#1E2320]">
                      {formatRial(item.finalAmount)}
                    </td>
                    <td className="p-3.5 text-[#1B4332] font-semibold">
                      {formatRial(item.paidAmount)}
                    </td>
                    <td className="p-3.5">
                      {item.paidAmount >= item.finalAmount ? (
                        <AccessibleBadge settings={settings} variant="success" label="تسویه کامل" size="sm" />
                      ) : item.paidAmount > 0 ? (
                        <AccessibleBadge settings={settings} variant="warning" label="پرداخت ناقص" size="sm" />
                      ) : (
                        <AccessibleBadge settings={settings} variant="alert" label="پرداخت نشده" size="sm" />
                      )}
                    </td>
                    <td className="p-3.5">
                      <div className="flex items-center justify-center gap-2">
                        <button
                          onClick={() => onViewInvoice && onViewInvoice(item, currentPeriod)}
                          className="px-2.5 py-1.5 rounded-lg border border-[#E6E1D8] text-xs font-bold text-[#1B4332] hover:bg-[#FAF8F5] flex items-center gap-1 cursor-pointer"
                          title="مشاهده و چاپ فاکتور رسمی"
                        >
                          <Receipt className="w-3.5 h-3.5" />
                          <span>فاکتور</span>
                        </button>
                        {item.paidAmount < item.finalAmount && (
                          <button
                            onClick={() => onOpenPayment && onOpenPayment(item, currentPeriod)}
                            className="px-2.5 py-1.5 rounded-lg bg-[#1B4332] text-xs font-bold text-[#FFFFFF] hover:bg-[#133024] flex items-center gap-1 shadow-xs cursor-pointer"
                            title="پرداخت آنلاین از طریق درگاه بانکی"
                          >
                            <DollarSign className="w-3.5 h-3.5" />
                            <span>پرداخت آنلاین</span>
                          </button>
                        )}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  );
};
