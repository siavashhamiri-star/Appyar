import React, { useState, useEffect } from 'react';
import { 
  X, 
  ShieldCheck, 
  CreditCard, 
  Lock, 
  CheckCircle2, 
  AlertCircle, 
  ArrowRight, 
  RefreshCw, 
  Timer,
  Building,
  Receipt
} from 'lucide-react';
import { AccessibilitySettings, Building as BuildingType, ChargeItem, ChargePeriod } from '../../types';
import { AccessibleButton } from '../common/AccessibleButton';
import { formatPersianNumber, formatRial } from '../../theme/designSystem';

interface PaymentGatewayModalProps {
  settings: AccessibilitySettings;
  isOpen: boolean;
  onClose: () => void;
  building: BuildingType;
  period: ChargePeriod;
  item: ChargeItem;
  onPaymentSuccess: (itemId: string, paidAmount: number, trackingCode: string) => void;
}

export const PaymentGatewayModal: React.FC<PaymentGatewayModalProps> = ({
  settings,
  isOpen,
  onClose,
  building,
  period,
  item,
  onPaymentSuccess
}) => {
  if (!isOpen) return null;

  const [cardNumber, setCardNumber] = useState('6037991823456789');
  const [cvv2, setCvv2] = useState('458');
  const [expMonth, setExpMonth] = useState('08');
  const [expYear, setExpYear] = useState('06');
  const [dynamicPin, setDynamicPin] = useState('');
  const [securityCode, setSecurityCode] = useState('74291');
  const [enteredSecurityCode, setEnteredSecurityCode] = useState('74291');
  
  // Timer for dynamic PIN
  const [otpTimer, setOtpTimer] = useState(120);
  const [isOtpSent, setIsOtpSent] = useState(false);

  // Payment status state
  const [isProcessing, setIsProcessing] = useState(false);
  const [paymentResult, setPaymentResult] = useState<{
    success: boolean;
    trackingCode: string;
    refNumber: string;
    date: string;
  } | null>(null);

  useEffect(() => {
    let interval: NodeJS.Timeout;
    if (isOtpSent && otpTimer > 0) {
      interval = setInterval(() => {
        setOtpTimer(prev => prev - 1);
      }, 1000);
    }
    return () => clearInterval(interval);
  }, [isOtpSent, otpTimer]);

  const handleRequestOtp = () => {
    setIsOtpSent(true);
    setOtpTimer(120);
    // Auto-fill simulated dynamic PIN after 1 second for user convenience
    setTimeout(() => {
      setDynamicPin('829401');
    }, 1000);
  };

  const handlePay = () => {
    setIsProcessing(true);
    setTimeout(() => {
      const tracking = `APYAR-${Math.floor(100000 + Math.random() * 900000)}`;
      const ref = `SHP-${Math.floor(10000000 + Math.random() * 90000000)}`;
      const result = {
        success: true,
        trackingCode: tracking,
        refNumber: ref,
        date: new Date().toLocaleDateString('fa-IR', {
          year: 'numeric',
          month: 'long',
          day: 'numeric',
          hour: '2-digit',
          minute: '2-digit'
        })
      };
      setPaymentResult(result);
      setIsProcessing(false);
      onPaymentSuccess(item.id, item.finalAmount, tracking);
    }, 1500);
  };

  const formatCardNumberDisplay = (val: string) => {
    const clean = val.replace(/\D/g, '').slice(0, 16);
    return clean.replace(/(\d{4})(?=\d)/g, '$1 - ');
  };

  return (
    <div 
      className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-xs overflow-y-auto"
      role="dialog"
      aria-modal="true"
      aria-labelledby="gateway-modal-title"
    >
      <div className="w-full max-w-xl rounded-3xl bg-[#FFFFFF] border border-[#E6E1D8] text-[#1E2320] shadow-2xl overflow-hidden my-8">
        {/* Gateway Brand Header */}
        <div className="bg-[#1B4332] text-[#FFFFFF] p-5 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-xl bg-[#FFFFFF]/10 flex items-center justify-center">
              <ShieldCheck className="w-6 h-6 text-[#A3E635]" />
            </div>
            <div>
              <div className="flex items-center gap-2">
                <span className="font-extrabold text-base">درگاه پرداخت الکترونیک شاپرک</span>
                <span className="text-[10px] font-bold bg-[#C87D20] text-white px-2 py-0.5 rounded">محیط آزمایشی (Sandbox)</span>
              </div>
              <span className="text-xs text-[#A7D7B5] block">
                شبیه‌ساز پرداخت شارژ سامانه اپیار (نسخه ۱.۰ محلی)
              </span>
            </div>
          </div>

          <button 
            onClick={onClose}
            className="p-1.5 rounded-lg text-[#FFFFFF]/80 hover:text-[#FFFFFF] hover:bg-[#FFFFFF]/10 cursor-pointer"
            aria-label="انصراف و بستن درگاه"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Merchant & Order Details Bar */}
        <div className="bg-[#FAF8F5] p-4 border-b border-[#E6E1D8] text-xs grid grid-cols-2 gap-3">
          <div>
            <span className="text-[#858E87] block">پذیرنده:</span>
            <span className="font-bold text-[#1B4332]">{building.name} (واحد {formatPersianNumber(item.unitNumber)})</span>
          </div>
          <div>
            <span className="text-[#858E87] block">مبلغ قابل پرداخت:</span>
            <span className="font-extrabold text-sm text-[#1B4332]">{formatRial(item.finalAmount)}</span>
          </div>
        </div>

        {/* Modal Body: Either Gateway Form or Success Screen */}
        <div className="p-6">
          {!paymentResult ? (
            <div className="space-y-4">
              {/* Card Number */}
              <div>
                <label className="block text-xs font-bold mb-1 text-[#5E6660]">
                  شماره کارت ۱۶ رقمی
                </label>
                <div className="relative">
                  <input
                    type="text"
                    value={cardNumber}
                    onChange={(e) => setCardNumber(e.target.value.replace(/\D/g, '').slice(0, 16))}
                    placeholder="xxxx - xxxx - xxxx - xxxx"
                    className="w-full p-3 pl-10 rounded-xl border border-[#E6E1D8] font-mono text-base tracking-widest text-center focus:outline-none focus:border-[#1B4332]"
                  />
                  <CreditCard className="w-5 h-5 absolute left-3 top-1/2 -translate-y-1/2 text-[#858E87]" />
                </div>
                <div className="text-[11px] text-[#858E87] mt-1 flex items-center justify-between">
                  <span>فرمت: {formatCardNumberDisplay(cardNumber)}</span>
                  <span className="text-[#1B4332] font-semibold">بانک متصل: ملی / سامان / ملت</span>
                </div>
              </div>

              {/* CVV2 & Expiry Date */}
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-bold mb-1 text-[#5E6660]">
                    کد شناسایی (CVV2)
                  </label>
                  <input
                    type="password"
                    maxLength={4}
                    value={cvv2}
                    onChange={(e) => setCvv2(e.target.value)}
                    placeholder="۳ یا ۴ رقم"
                    className="w-full p-3 rounded-xl border border-[#E6E1D8] font-mono text-center focus:outline-none focus:border-[#1B4332]"
                  />
                </div>

                <div>
                  <label className="block text-xs font-bold mb-1 text-[#5E6660]">
                    تاریخ انقضای کارت
                  </label>
                  <div className="grid grid-cols-2 gap-2">
                    <input
                      type="text"
                      maxLength={2}
                      value={expMonth}
                      onChange={(e) => setExpMonth(e.target.value)}
                      placeholder="ماه"
                      className="p-3 rounded-xl border border-[#E6E1D8] font-mono text-center focus:outline-none focus:border-[#1B4332]"
                    />
                    <input
                      type="text"
                      maxLength={2}
                      value={expYear}
                      onChange={(e) => setExpYear(e.target.value)}
                      placeholder="سال"
                      className="p-3 rounded-xl border border-[#E6E1D8] font-mono text-center focus:outline-none focus:border-[#1B4332]"
                    />
                  </div>
                </div>
              </div>

              {/* Dynamic PIN (رمز دوم پویا) */}
              <div>
                <label className="block text-xs font-bold mb-1 text-[#5E6660]">
                  رمز دوم (پویا / اینترنتی)
                </label>
                <div className="flex gap-2">
                  <input
                    type="password"
                    maxLength={8}
                    value={dynamicPin}
                    onChange={(e) => setDynamicPin(e.target.value)}
                    placeholder="رمز دریافتی از پیامک"
                    className="flex-1 p-3 rounded-xl border border-[#E6E1D8] font-mono text-center focus:outline-none focus:border-[#1B4332]"
                  />
                  <button
                    type="button"
                    onClick={handleRequestOtp}
                    disabled={isOtpSent && otpTimer > 0}
                    className="px-4 py-3 rounded-xl bg-[#EAF0EC] text-[#1B4332] font-bold text-xs hover:bg-[#DCE7DF] transition-all cursor-pointer whitespace-nowrap disabled:opacity-60"
                  >
                    {isOtpSent && otpTimer > 0 ? (
                      <span className="flex items-center gap-1">
                        <Timer className="w-3.5 h-3.5" />
                        <span>{otpTimer} ثانیه</span>
                      </span>
                    ) : (
                      'دریافت رمز پویا'
                    )}
                  </button>
                </div>
              </div>

              {/* Security Captcha Code */}
              <div>
                <label className="block text-xs font-bold mb-1 text-[#5E6660]">
                  کد امنیتی تصویر
                </label>
                <div className="flex items-center gap-3">
                  <input
                    type="text"
                    value={enteredSecurityCode}
                    onChange={(e) => setEnteredSecurityCode(e.target.value)}
                    placeholder="کد تصویر"
                    className="flex-1 p-3 rounded-xl border border-[#E6E1D8] font-mono text-center focus:outline-none focus:border-[#1B4332]"
                  />
                  <div className="bg-[#FAF8F5] px-4 py-2.5 rounded-xl border border-[#E6E1D8] font-mono font-bold tracking-widest text-lg text-[#1B4332] select-none line-through">
                    {securityCode}
                  </div>
                  <button
                    type="button"
                    onClick={() => {
                      const newCode = Math.floor(10000 + Math.random() * 90000).toString();
                      setSecurityCode(newCode);
                      setEnteredSecurityCode(newCode);
                    }}
                    className="p-3 rounded-xl border border-[#E6E1D8] text-[#5E6660] hover:bg-[#FAF8F5] cursor-pointer"
                    title="تغییر کد امنیتی"
                  >
                    <RefreshCw className="w-4 h-4" />
                  </button>
                </div>
              </div>

              {/* Submit Payment Button */}
              <div className="pt-4 flex items-center justify-between gap-3">
                <button
                  type="button"
                  onClick={onClose}
                  className="px-5 py-3 rounded-xl border border-[#E6E1D8] text-[#5E6660] text-sm font-bold hover:bg-[#FAF8F5] cursor-pointer"
                >
                  انصراف
                </button>
                <button
                  type="button"
                  disabled={isProcessing}
                  onClick={handlePay}
                  className="flex-1 flex items-center justify-center gap-2 px-6 py-3.5 rounded-xl bg-[#1B4332] text-[#FFFFFF] text-sm font-bold shadow-md hover:bg-[#133024] transition-all cursor-pointer disabled:opacity-60"
                >
                  {isProcessing ? (
                    <>
                      <RefreshCw className="w-4 h-4 animate-spin" />
                      <span>در حال ارتباط با شاپرک و تسویه حساب...</span>
                    </>
                  ) : (
                    <>
                      <Lock className="w-4 h-4" />
                      <span>پرداخت و تسویه آنی ({formatRial(item.finalAmount)})</span>
                    </>
                  )}
                </button>
              </div>
            </div>
          ) : (
            /* Success Receipt Screen */
            <div className="text-center space-y-5 py-4">
              <div className="w-16 h-16 rounded-full bg-[#EAF0EC] text-[#1B4332] flex items-center justify-center mx-auto shadow-inner">
                <CheckCircle2 className="w-10 h-10" />
              </div>

              <div>
                <h3 className="text-xl font-extrabold text-[#1B4332]">پرداخت با موفقیت انجام شد</h3>
                <p className="text-xs text-[#5E6660] mt-1">
                  شارژ واحد شماره {formatPersianNumber(item.unitNumber)} در سامانه اپیار تسویه گردید.
                </p>
              </div>

              {/* Digital Receipt Card */}
              <div className="p-4 rounded-2xl bg-[#FAF8F5] border border-[#E6E1D8] text-xs text-right space-y-2.5">
                <div className="flex items-center justify-between">
                  <span className="text-[#858E87]">کد پیگیری تراکنش:</span>
                  <span className="font-mono font-bold text-[#1B4332]">{paymentResult.trackingCode}</span>
                </div>
                <div className="flex items-center justify-between">
                  <span className="text-[#858E87]">شماره مرجع شاپرک (RRN):</span>
                  <span className="font-mono font-bold">{paymentResult.refNumber}</span>
                </div>
                <div className="flex items-center justify-between">
                  <span className="text-[#858E87]">مبلغ پرداخت شده:</span>
                  <span className="font-bold text-[#1B4332]">{formatRial(item.finalAmount)}</span>
                </div>
                <div className="flex items-center justify-between">
                  <span className="text-[#858E87]">زمان ثبت تراکنش:</span>
                  <span className="font-bold">{paymentResult.date}</span>
                </div>
                <div className="flex items-center justify-between pt-2 border-t border-[#E6E1D8]">
                  <span className="text-[#858E87]">دوره مالی:</span>
                  <span className="font-bold">{period.title}</span>
                </div>
              </div>

              <div className="pt-4 flex items-center justify-center gap-3">
                <AccessibleButton
                  settings={settings}
                  variant="primary"
                  onClick={onClose}
                >
                  بازگشت به پیشخوان شارژ اپیار
                </AccessibleButton>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
