import React from 'react';
import { Compass, Sparkles, HelpCircle } from 'lucide-react';
import { AccessibilitySettings } from '../../types';

interface LocationBreadcrumbProps {
  settings: AccessibilitySettings;
  currentScreenName: string;
  whatCanIDo: string;
  nextStepSuggestion?: string;
}

export const LocationBreadcrumb: React.FC<LocationBreadcrumbProps> = ({
  settings,
  currentScreenName,
  whatCanIDo,
  nextStepSuggestion
}) => {
  const isHighContrast = settings.mode === 'high-contrast';
  const isEasy = settings.mode === 'easy';

  return (
    <div
      className={`mb-6 rounded-2xl transition-all ${
        isHighContrast
          ? 'bg-[#181818] border-2 border-[#FFFFFF] p-4 text-[#FFFFFF]'
          : 'bg-[#F4EFEA] border border-[#E6E1D8] p-4 text-[#1E2320]'
      }`}
      role="region"
      aria-label="راهنمای موقعیت و وظیفه جاری"
    >
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-3">
        <div className="flex items-center gap-3">
          <div
            className={`w-10 h-10 rounded-xl flex items-center justify-center flex-shrink-0 ${
              isHighContrast
                ? 'bg-[#FFFFFF] text-[#000000]'
                : 'bg-[#1B4332] text-[#FFFFFF]'
            }`}
          >
            <Compass className="w-5 h-5" aria-hidden="true" />
          </div>
          <div>
            <div className="flex items-center gap-2">
              <span className={`font-semibold ${isEasy ? 'text-lg' : 'text-xs text-[#5E6660]'}`}>
                موقعیت شما:
              </span>
              <h2 className={`font-bold ${isEasy ? 'text-2xl text-[#1B4332]' : 'text-base font-bold'}`}>
                {currentScreenName}
              </h2>
            </div>
            <p className={`mt-0.5 text-[#5E6660] ${isEasy ? 'text-base font-medium text-[#1E2320]' : 'text-xs sm:text-sm'}`}>
              {whatCanIDo}
            </p>
          </div>
        </div>

        {nextStepSuggestion && (
          <div
            className={`flex items-center gap-2 px-3 py-1.5 rounded-xl flex-shrink-0 ${
              isHighContrast
                ? 'bg-[#282828] border border-[#FACC15] text-[#FACC15]'
                : 'bg-[#EAF0EC] border border-[#DCE7DF] text-[#1B4332]'
            }`}
          >
            <Sparkles className="w-4 h-4 flex-shrink-0" aria-hidden="true" />
            <span className={`font-medium ${isEasy ? 'text-sm font-bold' : 'text-xs'}`}>
              پیشنهاد: {nextStepSuggestion}
            </span>
          </div>
        )}
      </div>
    </div>
  );
};
