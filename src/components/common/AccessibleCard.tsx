import React from 'react';
import { AccessibilitySettings } from '../../types';

interface AccessibleCardProps {
  settings: AccessibilitySettings;
  children: React.ReactNode;
  variant?: 'default' | 'subtle' | 'sage' | 'amber';
  className?: string;
  id?: string;
}

export const AccessibleCard: React.FC<AccessibleCardProps> = ({
  settings,
  children,
  variant = 'default',
  className = '',
  id
}) => {
  const isHighContrast = settings.mode === 'high-contrast';
  const isEasy = settings.mode === 'easy';

  const paddingClasses = isEasy ? 'p-6 sm:p-8 rounded-3xl' : 'p-5 sm:p-6 rounded-2xl';

  let bgAndBorder = '';
  if (isHighContrast) {
    switch (variant) {
      case 'sage':
        bgAndBorder = 'bg-[#121212] border-2 border-[#4ADE80] text-[#FFFFFF]';
        break;
      case 'amber':
        bgAndBorder = 'bg-[#121212] border-2 border-[#FACC15] text-[#FFFFFF]';
        break;
      case 'subtle':
        bgAndBorder = 'bg-[#1A1A1A] border-2 border-[#FFFFFF] text-[#FFFFFF]';
        break;
      default:
        bgAndBorder = 'bg-[#121212] border-2 border-[#FFFFFF] text-[#FFFFFF]';
        break;
    }
  } else {
    switch (variant) {
      case 'sage':
        bgAndBorder = 'bg-[#EAF0EC] border border-[#DCE7DF] text-[#1B4332]';
        break;
      case 'amber':
        bgAndBorder = 'bg-[#FDF3E7] border border-[#E6A85C] text-[#8B570A]';
        break;
      case 'subtle':
        bgAndBorder = 'bg-[#F4EFEA] border border-[#E6E1D8] text-[#1E2320]';
        break;
      default:
        bgAndBorder = 'bg-[#FFFFFF] border border-[#E6E1D8] text-[#1E2320] shadow-[0_2px_8px_rgba(0,0,0,0.02)]';
        break;
    }
  }

  return (
    <div id={id} className={`${paddingClasses} ${bgAndBorder} transition-colors ${className}`}>
      {children}
    </div>
  );
};
