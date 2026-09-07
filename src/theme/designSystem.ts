// APYAR Design System Tokens & Helper Utilities
import { AccessibilitySettings } from '../types';

export const THEME_COLORS = {
  // Primary & Secondary Brand Colors
  forestPrimary: '#1B4332',
  forestHover: '#2D5A43',
  forestSurface: '#244E3B',
  sageLight: '#EAF0EC',
  sageMedium: '#DCE7DF',
  sageDark: '#5E7D6A',

  // Accents & Highlights
  amberAccent: '#C87D20',
  amberLight: '#FDF3E7',
  amberBorder: '#E6A85C',

  // Alerts & Terracotta (Carefully calibrated, not harsh)
  terracottaAlert: '#A4422E',
  terracottaLight: '#FDF0ED',
  terracottaBorder: '#E29B8C',

  // Neutrals & Backgrounds (Warm Ivory & Charcoal)
  warmIvoryBg: '#FAF8F5',
  warmSurface: '#FFFFFF',
  warmSurfaceSubtle: '#F4EFEA',
  charcoalText: '#1E2320',
  mutedText: '#5E6660',
  warmBorder: '#E6E1D8',
  warmBorderDarker: '#D3CDC2',
};

// Dynamic Accessibility Classes generator
export function getThemeClasses(settings: AccessibilitySettings) {
  const isHighContrast = settings.mode === 'high-contrast';
  const isEasy = settings.mode === 'easy';

  return {
    // Root container
    rootBg: isHighContrast ? 'bg-[#000000] text-[#FFFFFF]' : 'bg-[#FAF8F5] text-[#1E2320]',
    
    // Cards & Surfaces
    cardBg: isHighContrast 
      ? 'bg-[#121212] border-2 border-[#FFFFFF] text-[#FFFFFF] shadow-none' 
      : 'bg-[#FFFFFF] border border-[#E6E1D8] text-[#1E2320] shadow-[0_2px_8px_rgba(0,0,0,0.03)]',
    
    cardSubtleBg: isHighContrast
      ? 'bg-[#1E1E1E] border border-[#FFFFFF] text-[#FFFFFF]'
      : 'bg-[#F4EFEA] border border-[#E6E1D8] text-[#1E2320]',
      
    sageCardBg: isHighContrast
      ? 'bg-[#121212] border-2 border-[#A3E635] text-[#FFFFFF]'
      : 'bg-[#EAF0EC] border border-[#DCE7DF] text-[#1B4332]',

    // Primary Button (Deep Forest)
    primaryBtn: isHighContrast
      ? 'bg-[#FFFFFF] text-[#000000] border-2 border-[#FFFFFF] font-bold hover:bg-[#E5E5E5]'
      : 'bg-[#1B4332] text-[#FFFFFF] hover:bg-[#2D5A43] active:bg-[#143326] transition-colors',

    // Secondary Button (Soft Sage / Bordered)
    secondaryBtn: isHighContrast
      ? 'bg-[#000000] text-[#FFFFFF] border-2 border-[#FFFFFF] hover:bg-[#222222]'
      : 'bg-[#EAF0EC] text-[#1B4332] border border-[#DCE7DF] hover:bg-[#DCE7DF] transition-colors',

    // Accent / Warning Button (Warm Amber)
    accentBtn: isHighContrast
      ? 'bg-[#FACC15] text-[#000000] border-2 border-[#FACC15] font-bold'
      : 'bg-[#C87D20] text-[#FFFFFF] hover:bg-[#B36E1A] transition-colors',

    // Alert / Terracotta Button
    alertBtn: isHighContrast
      ? 'bg-[#EF4444] text-[#FFFFFF] border-2 border-[#FFFFFF] font-bold'
      : 'bg-[#A4422E] text-[#FFFFFF] hover:bg-[#8D3724] transition-colors',

    // Text Sizes based on scale
    headingText: isEasy 
      ? 'text-2xl sm:text-3xl font-extrabold tracking-tight'
      : settings.textSize === 'extra' 
        ? 'text-2xl font-bold' 
        : settings.textSize === 'large' 
          ? 'text-xl font-bold' 
          : 'text-lg font-bold',
          
    bodyText: isEasy 
      ? 'text-lg sm:text-xl leading-relaxed' 
      : settings.textSize === 'extra' 
        ? 'text-lg leading-relaxed' 
        : settings.textSize === 'large' 
          ? 'text-base' 
          : 'text-sm',

    subText: isEasy 
      ? 'text-base font-medium text-[#5E6660]' 
      : 'text-xs sm:text-sm text-[#5E6660]',

    // Touch Targets
    touchTarget: isEasy 
      ? 'min-h-[56px] min-w-[56px] px-6 py-3 text-lg font-bold rounded-2xl' 
      : 'min-h-[44px] min-w-[44px] px-4 py-2 text-sm font-medium rounded-xl',
  };
}

// Convert numbers to Persian formatted strings
export function formatPersianNumber(val: number | string): string {
  const numStr = val.toString();
  const persianDigits = ['۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹'];
  return numStr.replace(/\d/g, (d) => persianDigits[parseInt(d, 10)]);
}

// Format Currency in Rial with commas
export function formatRial(amount: number): string {
  const formatted = amount.toLocaleString('en-US');
  return `${formatPersianNumber(formatted)} ریال`;
}

// Human Date Formatter
export function formatPersianDate(timestamp: number): string {
  const date = new Date(timestamp);
  // Gregorian fallback with clean locale string
  return new Intl.DateTimeFormat('fa-IR', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  }).format(date);
}
