import React from 'react';
import { AccessibilitySettings } from '../../types';

interface AccessibleButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  settings: AccessibilitySettings;
  variant?: 'primary' | 'secondary' | 'accent' | 'alert' | 'ghost';
  icon?: React.ReactNode;
  children: React.ReactNode;
  fullWidth?: boolean;
}

export const AccessibleButton: React.FC<AccessibleButtonProps> = ({
  settings,
  variant = 'primary',
  icon,
  children,
  fullWidth = false,
  className = '',
  disabled = false,
  ...props
}) => {
  const isHighContrast = settings.mode === 'high-contrast';
  const isEasy = settings.mode === 'easy';

  // Base sizing guaranteeing touch target compliance
  const sizeClasses = isEasy
    ? 'min-h-[56px] min-w-[56px] px-6 py-3.5 text-base sm:text-lg font-bold rounded-2xl gap-3'
    : 'min-h-[44px] min-w-[44px] px-4 py-2.5 text-sm font-semibold rounded-xl gap-2';

  // Variants based on theme & high contrast
  let variantClasses = '';
  if (isHighContrast) {
    switch (variant) {
      case 'primary':
        variantClasses = 'bg-[#FFFFFF] text-[#000000] border-2 border-[#FFFFFF] hover:bg-[#E5E5E5]';
        break;
      case 'secondary':
        variantClasses = 'bg-[#121212] text-[#FFFFFF] border-2 border-[#FFFFFF] hover:bg-[#252525]';
        break;
      case 'accent':
        variantClasses = 'bg-[#FACC15] text-[#000000] border-2 border-[#FACC15] font-bold hover:bg-[#EAB308]';
        break;
      case 'alert':
        variantClasses = 'bg-[#EF4444] text-[#FFFFFF] border-2 border-[#EF4444] font-bold hover:bg-[#DC2626]';
        break;
      case 'ghost':
        variantClasses = 'bg-transparent text-[#FFFFFF] border border-dashed border-[#FFFFFF] hover:bg-[#1E1E1E]';
        break;
    }
  } else {
    switch (variant) {
      case 'primary':
        variantClasses = 'bg-[#1B4332] text-[#FFFFFF] border border-[#143326] shadow-sm hover:bg-[#2D5A43] active:bg-[#143326]';
        break;
      case 'secondary':
        variantClasses = 'bg-[#EAF0EC] text-[#1B4332] border border-[#DCE7DF] hover:bg-[#DCE7DF] active:bg-[#C9D8CD]';
        break;
      case 'accent':
        variantClasses = 'bg-[#C87D20] text-[#FFFFFF] border border-[#B36E1A] shadow-sm hover:bg-[#B36E1A] active:bg-[#9B5C10]';
        break;
      case 'alert':
        variantClasses = 'bg-[#A4422E] text-[#FFFFFF] border border-[#8D3724] shadow-sm hover:bg-[#8D3724] active:bg-[#782D1D]';
        break;
      case 'ghost':
        variantClasses = 'bg-transparent text-[#5E6660] hover:bg-[#F4EFEA] hover:text-[#1E2320]';
        break;
    }
  }

  const disabledClasses = disabled ? 'opacity-40 cursor-not-allowed pointer-events-none' : 'cursor-pointer active:scale-[0.99]';

  return (
    <button
      className={`inline-flex items-center justify-center font-medium transition-all select-none text-center ${sizeClasses} ${variantClasses} ${disabledClasses} ${fullWidth ? 'w-full' : ''} ${className}`}
      disabled={disabled}
      {...props}
    >
      {icon && <span className="flex-shrink-0 flex items-center">{icon}</span>}
      <span className="truncate">{children}</span>
    </button>
  );
};
