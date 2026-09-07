import React from 'react';
import { CheckCircle2, Clock, AlertCircle, XCircle, Info, Shield } from 'lucide-react';
import { AccessibilitySettings } from '../../types';

interface AccessibleBadgeProps {
  settings: AccessibilitySettings;
  variant: 'success' | 'warning' | 'alert' | 'neutral' | 'sage' | 'amber';
  label: string;
  icon?: React.ReactNode;
  size?: 'sm' | 'md' | 'lg';
}

export const AccessibleBadge: React.FC<AccessibleBadgeProps> = ({
  settings,
  variant,
  label,
  icon,
  size = 'md'
}) => {
  const isHighContrast = settings.mode === 'high-contrast';
  const isEasy = settings.mode === 'easy';

  // Sizing
  const sizeClasses = isEasy || size === 'lg'
    ? 'px-3.5 py-1.5 text-base font-bold rounded-xl gap-2'
    : size === 'sm'
      ? 'px-2 py-0.5 text-xs font-semibold rounded-lg gap-1.5'
      : 'px-2.5 py-1 text-sm font-semibold rounded-lg gap-2';

  // Default Icon fallback if none provided
  const renderIcon = icon || (() => {
    switch (variant) {
      case 'success':
      case 'sage':
        return <CheckCircle2 className="w-4 h-4" aria-hidden="true" />;
      case 'warning':
      case 'amber':
        return <Clock className="w-4 h-4" aria-hidden="true" />;
      case 'alert':
        return <AlertCircle className="w-4 h-4" aria-hidden="true" />;
      case 'neutral':
      default:
        return <Info className="w-4 h-4" aria-hidden="true" />;
    }
  })();

  let colorClasses = '';
  if (isHighContrast) {
    switch (variant) {
      case 'success':
      case 'sage':
        colorClasses = 'bg-[#000000] text-[#4ADE80] border-2 border-[#4ADE80]';
        break;
      case 'warning':
      case 'amber':
        colorClasses = 'bg-[#000000] text-[#FACC15] border-2 border-[#FACC15]';
        break;
      case 'alert':
        colorClasses = 'bg-[#000000] text-[#F87171] border-2 border-[#F87171]';
        break;
      case 'neutral':
      default:
        colorClasses = 'bg-[#000000] text-[#FFFFFF] border-2 border-[#FFFFFF]';
        break;
    }
  } else {
    switch (variant) {
      case 'success':
        colorClasses = 'bg-[#EAF0EC] text-[#1B4332] border border-[#DCE7DF]';
        break;
      case 'sage':
        colorClasses = 'bg-[#EAF0EC] text-[#244E3B] border border-[#DCE7DF]';
        break;
      case 'warning':
      case 'amber':
        colorClasses = 'bg-[#FDF3E7] text-[#8B570A] border border-[#E6A85C]';
        break;
      case 'alert':
        colorClasses = 'bg-[#FDF0ED] text-[#A4422E] border border-[#E29B8C]';
        break;
      case 'neutral':
      default:
        colorClasses = 'bg-[#F4EFEA] text-[#1E2320] border border-[#E6E1D8]';
        break;
    }
  }

  return (
    <span
      className={`inline-flex items-center justify-center font-medium leading-none whitespace-nowrap ${sizeClasses} ${colorClasses}`}
      role="status"
    >
      <span className="flex-shrink-0">{renderIcon}</span>
      <span>{label}</span>
    </span>
  );
};
