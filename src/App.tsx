import React, { useState } from 'react';
import { 
  Building as BuildingIcon, 
  Home, 
  Layers, 
  Wrench, 
  Calculator, 
  Car, 
  Users, 
  Sliders, 
  Eye, 
  Maximize2, 
  Sparkles, 
  Plus, 
  X, 
  Check, 
  DollarSign, 
  Phone, 
  ShieldCheck,
  ChevronDown
} from 'lucide-react';
import { 
  AccessibilitySettings, 
  AccessibilityMode, 
  TextSize, 
  Building, 
  Unit, 
  BuildingMember, 
  DelegatedPermission, 
  Role, 
  ChargePeriod, 
  ChargeCalculationMethod, 
  BuildingExpense, 
  ChargeItem, 
  ServiceProvider, 
  ServiceRecord, 
  MaintenanceRecord, 
  ParkingSpace, 
  StorageUnit,
  MethodType
} from './types';
import { OverviewDashboard } from './components/dashboard/OverviewDashboard';
import { ChargeCalculationSection } from './components/charges/ChargeCalculationSection';
import { ServicesSection } from './components/services/ServicesSection';
import { ParkingStorageSection } from './components/parking/ParkingStorageSection';
import { UnitsSection } from './components/units/UnitsSection';
import { MembersSection } from './components/members/MembersSection';
import { LocationBreadcrumb } from './components/common/LocationBreadcrumb';
import { VoiceAssistantBar } from './components/common/VoiceAssistantBar';
import { AccessibilityControlPanel } from './components/common/AccessibilityControlPanel';
import { AccessibleButton } from './components/common/AccessibleButton';
import { InvoiceModal } from './components/modals/InvoiceModal';
import { PaymentGatewayModal } from './components/modals/PaymentGatewayModal';
import { NewUnitModal } from './components/modals/NewUnitModal';
import { NewProviderModal } from './components/modals/NewProviderModal';
import { NewDelegationModal } from './components/modals/NewDelegationModal';
import { NewParkingStorageModal } from './components/modals/NewParkingStorageModal';
import { formatPersianNumber, formatRial } from './theme/designSystem';

export function App() {
  // Accessibility Configuration State
  const [accessibilitySettings, setAccessibilitySettings] = useState<AccessibilitySettings>({
    mode: 'standard',
    textSize: 'normal',
    reduceMotion: false,
    screenReaderDescriptions: true
  });

  const [isAccessibilityModalOpen, setIsAccessibilityModalOpen] = useState(false);

  // Navigation State
  const [activeTab, setActiveTab] = useState<'dashboard' | 'charges' | 'services' | 'parking' | 'units' | 'members'>('dashboard');

  // Simulated User / Building Context
  const [buildings] = useState<Building[]>([
    {
      id: 'b-arghavan',
      name: 'مجتمع مسکونی ارغوان',
      address: 'تهران، خیابان شریعتی، بالاتر از پل رومی، پلاک ۱۲',
      city: 'تهران',
      postalCode: '1939543210',
      unitCount: 4,
      createdAt: 1720000000000,
      isActive: true
    },
    {
      id: 'b-niloufar',
      name: 'برج نیلوفر',
      address: 'تهران، سعادت‌آباد، میدان کاج، خیابان هفتم',
      city: 'تهران',
      postalCode: '1998765432',
      unitCount: 2,
      createdAt: 1721000000000,
      isActive: true
    }
  ]);

  const [selectedBuildingId, setSelectedBuildingId] = useState<string>('b-arghavan');
  const activeBuilding = buildings.find(b => b.id === selectedBuildingId) || buildings[0];

  const [userRole, setUserRole] = useState<Role>('BUILDING_ADMIN');

  // Units State
  const [units, setUnits] = useState<Unit[]>([
    {
      id: 'u-101',
      buildingId: 'b-arghavan',
      unitNumber: '۱۰۱',
      floor: 1,
      areaSquareMeters: 100,
      residentCount: 2,
      parkingCount: 1,
      storageCount: 1,
      occupancyStatus: 'OCCUPIED',
      ownerName: 'رضا کمالی',
      tenantName: 'مهرداد صادقی',
      isActive: true
    },
    {
      id: 'u-102',
      buildingId: 'b-arghavan',
      unitNumber: '۱۰۲',
      floor: 1,
      areaSquareMeters: 120,
      residentCount: 3,
      parkingCount: 2,
      storageCount: 1,
      occupancyStatus: 'OCCUPIED',
      ownerName: 'مریم حسینی',
      tenantName: undefined,
      isActive: true
    },
    {
      id: 'u-201',
      buildingId: 'b-arghavan',
      unitNumber: '۲۰۱',
      floor: 2,
      areaSquareMeters: 80,
      residentCount: 0,
      parkingCount: 1,
      storageCount: 0,
      occupancyStatus: 'VACANT',
      ownerName: 'پیمان یوسفی',
      tenantName: undefined,
      isActive: true
    },
    {
      id: 'u-202',
      buildingId: 'b-arghavan',
      unitNumber: '۲۰۲',
      floor: 2,
      areaSquareMeters: 100,
      residentCount: 4,
      parkingCount: 1,
      storageCount: 1,
      occupancyStatus: 'OCCUPIED',
      ownerName: 'فاطمه موسوی',
      tenantName: undefined,
      isActive: true
    }
  ]);

  // Charge Calculation Methods (Stage 7.1)
  const [methods, setMethods] = useState<ChargeCalculationMethod[]>([
    {
      id: 'm-equal',
      buildingId: 'b-arghavan',
      name: 'تسهیم مساوی بین واحدها',
      description: 'هزینه کل بدون در نظر گرفتن متراژ به طور مساوی بین تمام واحدها تقسیم می‌شود',
      methodType: 'EQUAL',
      isDefault: true,
      isActive: true,
      createdAt: 1720000000000,
      createdBy: 'admin-1',
      components: []
    },
    {
      id: 'm-area',
      buildingId: 'b-arghavan',
      name: 'تسهیم بر مبنای متراژ واحد',
      description: 'هزینه کل به نسبت مساحت هر واحد (مترمربع) تسهیم می‌گردد',
      methodType: 'AREA_BASED',
      isDefault: false,
      isActive: true,
      createdAt: 1720000000000,
      createdBy: 'admin-1',
      components: [
        {
          id: 'c-area',
          calculationMethodId: 'm-area',
          componentType: 'AREA',
          weight: 1.0,
          enabled: true,
          configuration: {}
        }
      ]
    },
    {
      id: 'm-resident',
      buildingId: 'b-arghavan',
      name: 'تسهیم بر مبنای تعداد نفرات',
      description: 'هزینه‌ها بر حسب تعداد ساکنین مقیم هر واحد محاسبه می‌شوند',
      methodType: 'RESIDENT_BASED',
      isDefault: false,
      isActive: true,
      createdAt: 1720000000000,
      createdBy: 'admin-1',
      components: [
        {
          id: 'c-res',
          calculationMethodId: 'm-resident',
          componentType: 'RESIDENT_COUNT',
          weight: 1.0,
          enabled: true,
          configuration: {}
        }
      ]
    },
    {
      id: 'm-mixed',
      buildingId: 'b-arghavan',
      name: 'فرمول ترکیبی (۶۰٪ متراژ + ۴۰٪ نفرات)',
      description: 'تسهیم بر اساس تلفیق متراژ و تعداد ساکنین',
      methodType: 'MIXED',
      isDefault: false,
      isActive: true,
      createdAt: 1720000000000,
      createdBy: 'admin-1',
      components: [
        {
          id: 'c-m1',
          calculationMethodId: 'm-mixed',
          componentType: 'AREA',
          weight: 0.6,
          enabled: true,
          configuration: {}
        },
        {
          id: 'c-m2',
          calculationMethodId: 'm-mixed',
          componentType: 'RESIDENT_COUNT',
          weight: 0.4,
          enabled: true,
          configuration: {}
        }
      ]
    }
  ]);

  // Building Expenses State (Stage 7.1)
  const [expenses, setExpenses] = useState<BuildingExpense[]>([
    {
      id: 'exp-1',
      buildingId: 'b-arghavan',
      chargePeriodId: 'cp-mordad',
      title: 'سرویس و روغن‌کاری ماهانه آسانسور',
      category: 'سرویس آسانسور',
      amount: 4500000,
      expenseDate: Date.now() - 86400000 * 5,
      createdBy: 'علی محمدی',
      status: 'APPROVED'
    },
    {
      id: 'exp-2',
      buildingId: 'b-arghavan',
      chargePeriodId: 'cp-mordad',
      title: 'باغبانی و نگهداری فضای سبز حیاط',
      category: 'باغبانی',
      amount: 2500000,
      expenseDate: Date.now() - 86400000 * 3,
      createdBy: 'علی محمدی',
      status: 'APPROVED'
    },
    {
      id: 'exp-3',
      buildingId: 'b-arghavan',
      chargePeriodId: 'cp-mordad',
      title: 'نظافت و شستشوی راه‌پله و پارکینگ',
      category: 'نظافت',
      amount: 3000000,
      expenseDate: Date.now() - 86400000 * 1,
      createdBy: 'علی محمدی',
      status: 'APPROVED'
    }
  ]);

  // Charge Periods State
  const [chargePeriods, setChargePeriods] = useState<ChargePeriod[]>([
    {
      id: 'cp-mordad',
      buildingId: 'b-arghavan',
      title: 'شارژ مرداد ماه ۱۴۰۵',
      periodStart: Date.now() - 86400000 * 30,
      periodEnd: Date.now(),
      dueDate: Date.now() + 86400000 * 7,
      status: 'CALCULATED',
      calculationMethodId: 'm-equal',
      totalBuildingCost: 10000000,
      createdAt: Date.now() - 86400000 * 30,
      createdBy: 'علی محمدی'
    },
    {
      id: 'cp-tir',
      buildingId: 'b-arghavan',
      title: 'شارژ تیر ماه ۱۴۰۵',
      periodStart: Date.now() - 86400000 * 60,
      periodEnd: Date.now() - 86400000 * 30,
      dueDate: Date.now() - 86400000 * 20,
      status: 'FINALIZED',
      calculationMethodId: 'm-equal',
      totalBuildingCost: 8800000,
      createdAt: Date.now() - 86400000 * 60,
      createdBy: 'علی محمدی',
      finalizedAt: Date.now() - 86400000 * 25
    }
  ]);

  // Charge Items State
  const [chargeItems, setChargeItems] = useState<ChargeItem[]>([
    {
      id: 'ci-1',
      chargePeriodId: 'cp-mordad',
      buildingId: 'b-arghavan',
      unitId: 'u-101',
      unitNumber: '۱۰۱',
      calculationMethodId: 'm-equal',
      baseAmount: 2500000,
      adjustments: 0,
      finalAmount: 2500000,
      paidAmount: 2500000,
      status: 'PAID'
    },
    {
      id: 'ci-2',
      chargePeriodId: 'cp-mordad',
      buildingId: 'b-arghavan',
      unitId: 'u-102',
      unitNumber: '۱۰۲',
      calculationMethodId: 'm-equal',
      baseAmount: 2500000,
      adjustments: 0,
      finalAmount: 2500000,
      paidAmount: 0,
      status: 'OVERDUE'
    },
    {
      id: 'ci-3',
      chargePeriodId: 'cp-mordad',
      buildingId: 'b-arghavan',
      unitId: 'u-201',
      unitNumber: '۲۰۱',
      calculationMethodId: 'm-equal',
      baseAmount: 2500000,
      adjustments: 0,
      finalAmount: 2500000,
      paidAmount: 2500000,
      status: 'PAID'
    },
    {
      id: 'ci-4',
      chargePeriodId: 'cp-mordad',
      buildingId: 'b-arghavan',
      unitId: 'u-202',
      unitNumber: '۲۰۲',
      calculationMethodId: 'm-equal',
      baseAmount: 2500000,
      adjustments: 0,
      finalAmount: 2500000,
      paidAmount: 2500000,
      status: 'PAID'
    }
  ]);

  // Services & Service Providers State (Stage 6)
  const [providers, setProviders] = useState<ServiceProvider[]>([
    { id: 'sp-1', name: 'مهندس احمد پورحیدری', specialty: 'سرویس و نگهداری آسانسور', phone: '09121112233', rating: 4.9, status: 'ACTIVE' },
    { id: 'sp-2', name: 'استاد علی کاظمی', specialty: 'تاسیسات و موتورخانه', phone: '09123334455', rating: 4.7, status: 'ACTIVE' },
    { id: 'sp-3', name: 'شرکت پاک‌سازان البرز', specialty: 'خدمات نظافت و شستشو', phone: '02188776655', rating: 4.8, status: 'ACTIVE' },
  ]);

  const [serviceRecords, setServiceRecords] = useState<ServiceRecord[]>([
    {
      id: 'sr-1',
      buildingId: 'b-arghavan',
      providerId: 'sp-1',
      providerName: 'مهندس احمد پورحیدری',
      title: 'سرویس ماهانه آسانسور و تنظیم ترمزها',
      category: 'سرویس دوره‌ای',
      cost: 4500000,
      serviceDate: Date.now() - 86400000 * 5,
      status: 'COMPLETED',
      description: 'کابل‌ها و روغن‌کاری قرقره‌ها بررسی و تایید شد.'
    },
    {
      id: 'sr-2',
      buildingId: 'b-arghavan',
      providerId: 'sp-2',
      providerName: 'استاد علی کاظمی',
      title: 'هواگیری مشعل و بررسی پمپ سیرکولاتور',
      category: 'تاسیسات',
      cost: 1800000,
      serviceDate: Date.now() + 86400000 * 2,
      status: 'PENDING',
      description: 'آماده‌سازی سیستم گرمایش پیش از فصل پاییز.'
    }
  ]);

  const [maintenanceRecords, setMaintenanceRecords] = useState<MaintenanceRecord[]>([
    { id: 'mr-1', buildingId: 'b-arghavan', equipmentName: 'آسانسور مسافربری هیدرولیک', lastCheckDate: Date.now() - 86400000 * 5, nextCheckDate: Date.now() + 86400000 * 25, status: 'HEALTHY', notes: 'تاییدیه استاندارد معتبر تا پایان سال' },
    { id: 'mr-2', buildingId: 'b-arghavan', equipmentName: 'پمپ آب تحت فشار بوستر', lastCheckDate: Date.now() - 86400000 * 12, nextCheckDate: Date.now() + 86400000 * 18, status: 'HEALTHY' },
    { id: 'mr-3', buildingId: 'b-arghavan', equipmentName: 'دوربین‌های مداربسته و ذخیره‌ساز DVR', lastCheckDate: Date.now() - 86400000 * 40, nextCheckDate: Date.now() - 86400000 * 5, status: 'NEEDS_INSPECTION', notes: 'کانال شماره ۳ نیاز به بازبینی زاویه دید دارد' },
  ]);

  // Parking & Storage State (Stage 5)
  const [parkingSpaces, setParkingSpaces] = useState<ParkingSpace[]>([
    { id: 'ps-1', buildingId: 'b-arghavan', spaceNumber: '۱', floor: -1, assignedUnitId: 'u-101', assignedUnitNumber: '۱۰۱', isGuestSpace: false, status: 'OCCUPIED' },
    { id: 'ps-2', buildingId: 'b-arghavan', spaceNumber: '۲', floor: -1, assignedUnitId: 'u-102', assignedUnitNumber: '۱۰۲', isGuestSpace: false, status: 'OCCUPIED' },
    { id: 'ps-3', buildingId: 'b-arghavan', spaceNumber: '۳', floor: -1, assignedUnitId: 'u-102', assignedUnitNumber: '۱۰۲', isGuestSpace: false, status: 'OCCUPIED' },
    { id: 'ps-4', buildingId: 'b-arghavan', spaceNumber: '۴', floor: -1, assignedUnitId: 'u-201', assignedUnitNumber: '۲۰۱', isGuestSpace: false, status: 'OCCUPIED' },
    { id: 'ps-5', buildingId: 'b-arghavan', spaceNumber: '۵', floor: -1, assignedUnitId: 'u-202', assignedUnitNumber: '۲۰۲', isGuestSpace: false, status: 'OCCUPIED' },
    { id: 'ps-6', buildingId: 'b-arghavan', spaceNumber: '۶ (مهمان)', floor: -1, isGuestSpace: true, status: 'VACANT' },
  ]);

  const [storageUnits, setStorageUnits] = useState<StorageUnit[]>([
    { id: 'su-1', buildingId: 'b-arghavan', storageNumber: '۱', floor: -1, areaSquareMeters: 4.5, assignedUnitId: 'u-101', assignedUnitNumber: '۱۰۱', status: 'OCCUPIED' },
    { id: 'su-2', buildingId: 'b-arghavan', storageNumber: '۲', floor: -1, areaSquareMeters: 5.2, assignedUnitId: 'u-102', assignedUnitNumber: '۱۰۲', status: 'OCCUPIED' },
    { id: 'su-3', buildingId: 'b-arghavan', storageNumber: '۳', floor: -1, areaSquareMeters: 3.8, assignedUnitId: 'u-202', assignedUnitNumber: '۲۰۲', status: 'OCCUPIED' },
    { id: 'su-4', buildingId: 'b-arghavan', storageNumber: '۴ (مشاع)', floor: -1, areaSquareMeters: 8.0, status: 'VACANT' },
  ]);

  // Members & RBAC State (Stage 2)
  const [members, setMembers] = useState<BuildingMember[]>([
    { id: 'm-1', buildingId: 'b-arghavan', userId: 'usr-1', userName: 'علی محمدی', userPhone: '09121111111', role: 'BUILDING_ADMIN', startDate: 1720000000000, isActive: true },
    { id: 'm-2', buildingId: 'b-arghavan', userId: 'usr-2', userName: 'سارا احمدی', userPhone: '09122222222', role: 'BOARD_MEMBER', startDate: 1720000000000, isActive: true },
    { id: 'm-3', buildingId: 'b-arghavan', userId: 'usr-3', userName: 'رضا کمالی', userPhone: '09123333333', role: 'OWNER', startDate: 1720000000000, isActive: true },
    { id: 'm-4', buildingId: 'b-arghavan', userId: 'usr-4', userName: 'مهرداد صادقی', userPhone: '09124444444', role: 'TENANT', startDate: 1720500000000, isActive: true },
  ]);

  const [delegations, setDelegations] = useState<DelegatedPermission[]>([
    {
      id: 'del-1',
      buildingId: 'b-arghavan',
      grantedByUserId: 'usr-1',
      grantedToUserId: 'usr-2',
      granteeName: 'سارا احمدی',
      permission: 'MANAGE_FINANCIAL_DATA',
      startDate: Date.now() - 86400000 * 10,
      endDate: Date.now() + 86400000 * 20,
      status: 'ACTIVE',
      note: 'تفویض جهت رسیدگی به ثبت هزینه‌ها در ایام مرخصی مدیر'
    }
  ]);

  // Modals state
  const [isNewExpenseModalOpen, setIsNewExpenseModalOpen] = useState(false);
  const [isNewPeriodModalOpen, setIsNewPeriodModalOpen] = useState(false);
  const [isNewServiceModalOpen, setIsNewServiceModalOpen] = useState(false);
  const [isNewUnitModalOpen, setIsNewUnitModalOpen] = useState(false);
  const [isNewProviderModalOpen, setIsNewProviderModalOpen] = useState(false);
  const [isNewDelegationModalOpen, setIsNewDelegationModalOpen] = useState(false);
  const [isNewParkingStorageModalOpen, setIsNewParkingStorageModalOpen] = useState(false);

  // Invoice and Payment state
  const [selectedInvoiceItem, setSelectedInvoiceItem] = useState<{ item: ChargeItem; period: ChargePeriod } | null>(null);
  const [selectedPaymentItem, setSelectedPaymentItem] = useState<{ item: ChargeItem; period: ChargePeriod } | null>(null);

  // Entity addition handlers
  const handleAddUnit = (newUnit: Unit) => {
    setUnits(prev => [newUnit, ...prev]);
  };

  const handleAddProvider = (provider: ServiceProvider) => {
    setProviders(prev => [provider, ...prev]);
  };

  const handleAddDelegation = (delegation: DelegatedPermission) => {
    setDelegations(prev => [delegation, ...prev]);
  };

  const handleAddParkingSpace = (space: ParkingSpace) => {
    setParkingSpaces(prev => [space, ...prev]);
  };

  const handleAddStorageUnit = (storage: StorageUnit) => {
    setStorageUnits(prev => [storage, ...prev]);
  };

  // Payment Settlement Handler
  const handlePaymentSuccess = (itemId: string, paidAmount: number, trackingCode: string) => {
    setChargeItems(prev => prev.map(item => {
      if (item.id === itemId) {
        return {
          ...item,
          paidAmount: item.finalAmount,
          status: 'PAID'
        };
      }
      return item;
    }));
  };

  // New Expense form state
  const [newExpenseTitle, setNewExpenseTitle] = useState('');
  const [newExpenseAmount, setNewExpenseAmount] = useState('');
  const [newExpenseCategory, setNewExpenseCategory] = useState('تعمیرات عمومی');

  // New Period form state
  const [newPeriodTitle, setNewPeriodTitle] = useState('');
  const [newPeriodCost, setNewPeriodCost] = useState('');

  // Handle deterministic charge calculation execution
  const handleCalculateCharges = (periodId: string, methodId: string) => {
    const period = chargePeriods.find(p => p.id === periodId);
    if (!period) return;

    const method = methods.find(m => m.id === methodId) || methods[0];
    const periodExpenses = expenses.filter(e => e.chargePeriodId === periodId);
    const sumExpenses = periodExpenses.reduce((s, e) => s + e.amount, 0);
    const totalCost = sumExpenses > 0 ? sumExpenses : (period.totalBuildingCost || 10000000);

    let newItems: ChargeItem[] = [];

    if (method.methodType === 'AREA_BASED') {
      const totalArea = units.reduce((s, u) => s + u.areaSquareMeters, 0);
      newItems = units.map((u, i) => {
        const share = Math.round((u.areaSquareMeters / totalArea) * totalCost);
        return {
          id: `ci-calc-${periodId}-${u.id}-${Date.now()}`,
          chargePeriodId: periodId,
          buildingId: activeBuilding.id,
          unitId: u.id,
          unitNumber: u.unitNumber,
          calculationMethodId: method.id,
          baseAmount: share,
          adjustments: 0,
          finalAmount: share,
          paidAmount: 0,
          status: 'CALCULATED'
        };
      });
    } else if (method.methodType === 'RESIDENT_BASED') {
      const totalResidents = units.reduce((s, u) => s + u.residentCount, 0) || 1;
      newItems = units.map((u, i) => {
        const share = Math.round((u.residentCount / totalResidents) * totalCost);
        return {
          id: `ci-calc-${periodId}-${u.id}-${Date.now()}`,
          chargePeriodId: periodId,
          buildingId: activeBuilding.id,
          unitId: u.id,
          unitNumber: u.unitNumber,
          calculationMethodId: method.id,
          baseAmount: share,
          adjustments: 0,
          finalAmount: share,
          paidAmount: 0,
          status: 'CALCULATED'
        };
      });
    } else {
      // EQUAL sharing
      const perUnit = Math.round(totalCost / units.length);
      newItems = units.map((u) => ({
        id: `ci-calc-${periodId}-${u.id}-${Date.now()}`,
        chargePeriodId: periodId,
        buildingId: activeBuilding.id,
        unitId: u.id,
        unitNumber: u.unitNumber,
        calculationMethodId: method.id,
        baseAmount: perUnit,
        adjustments: 0,
        finalAmount: perUnit,
        paidAmount: 0,
        status: 'CALCULATED'
      }));
    }

    setChargeItems(prev => prev.filter(item => item.chargePeriodId !== periodId).concat(newItems));
    setChargePeriods(prev => prev.map(p => p.id === periodId ? {
      ...p,
      status: 'CALCULATED',
      calculationMethodId: method.id,
      totalBuildingCost: totalCost
    } : p));
  };

  const handleFinalizePeriod = (periodId: string) => {
    setChargePeriods(prev => prev.map(p => p.id === periodId ? {
      ...p,
      status: 'FINALIZED',
      finalizedAt: Date.now()
    } : p));
  };

  const handleSaveExpense = () => {
    if (!newExpenseTitle || !newExpenseAmount) return;
    const expense: BuildingExpense = {
      id: `exp-${Date.now()}`,
      buildingId: activeBuilding.id,
      chargePeriodId: chargePeriods[0]?.id,
      title: newExpenseTitle,
      category: newExpenseCategory,
      amount: parseInt(newExpenseAmount, 10),
      expenseDate: Date.now(),
      createdBy: 'علی محمدی (مدیر)',
      status: 'APPROVED'
    };
    setExpenses(prev => [expense, ...prev]);
    setNewExpenseTitle('');
    setNewExpenseAmount('');
    setIsNewExpenseModalOpen(false);
  };

  const handleSavePeriod = () => {
    if (!newPeriodTitle) return;
    const cost = parseInt(newPeriodCost, 10) || 0;
    const period: ChargePeriod = {
      id: `cp-${Date.now()}`,
      buildingId: activeBuilding.id,
      title: newPeriodTitle,
      periodStart: Date.now(),
      periodEnd: Date.now() + 86400000 * 30,
      dueDate: Date.now() + 86400000 * 37,
      status: 'DRAFT',
      calculationMethodId: 'm-equal',
      totalBuildingCost: cost,
      createdAt: Date.now(),
      createdBy: 'علی محمدی'
    };
    setChargePeriods(prev => [period, ...prev]);
    setNewPeriodTitle('');
    setNewPeriodCost('');
    setIsNewPeriodModalOpen(false);
  };

  // Voice Action Dispatcher
  const handleExecuteVoiceIntent = (actionKey: string) => {
    switch (actionKey) {
      case 'VIEW_BUILDING':
        setActiveTab('dashboard');
        break;
      case 'NEW_REQUEST':
        setIsNewServiceModalOpen(true);
        break;
      case 'ELEVATOR_STATUS':
        setActiveTab('services');
        break;
      case 'OVERDUE_TASKS':
        setActiveTab('charges');
        break;
      case 'NEW_INVOICE':
        setIsNewExpenseModalOpen(true);
        break;
    }
  };

  const isHighContrast = accessibilitySettings.mode === 'high-contrast';
  const isEasy = accessibilitySettings.mode === 'easy';

  // Navigation Items
  const navItems: { id: typeof activeTab; title: string; icon: React.ReactNode }[] = [
    { id: 'dashboard', title: 'پیشخوان', icon: <Home className="w-4 h-4" /> },
    { id: 'charges', title: 'محاسبه و شارژ', icon: <Calculator className="w-4 h-4" /> },
    { id: 'services', title: 'خدمات و سوابق', icon: <Wrench className="w-4 h-4" /> },
    { id: 'parking', title: 'پارکینگ و مشاعات', icon: <Car className="w-4 h-4" /> },
    { id: 'units', title: 'واحدهای ساختمان', icon: <Layers className="w-4 h-4" /> },
    { id: 'members', title: 'اعضا و دسترسی‌ها', icon: <Users className="w-4 h-4" /> },
  ];

  const getScreenDetails = () => {
    switch (activeTab) {
      case 'dashboard':
        return {
          name: 'پیشخوان اصلی و وضعیت ساختمان',
          whatCanIDo: 'مشاهده درخواست‌های باز، کارهای در دست اقدام و صورت‌جلسه فعالیت‌ها',
          suggestion: 'بررسی ۱ مورد شارژ معوقه در بخش محاسبه و شارژ'
        };
      case 'charges':
        return {
          name: 'بخش مدیریت مالی و محاسبه شارژ',
          whatCanIDo: 'ایجاد دوره، ثبت هزینه‌ها و محاسبه تسهیم مبالغ بر اساس متراژ یا نفرات',
          suggestion: 'کلیک روی «محاسبه و تسهیم مجدد شارژ» برای به‌روزرسانی اقلام'
        };
      case 'services':
        return {
          name: 'بخش خدمات، تعمیرات و سرویس‌کاران',
          whatCanIDo: 'ثبت خرابی، پیگیری زمان‌بندی سرویس آسانسور و دسترسی به شماره نصاب و تعمیرکار',
          suggestion: 'ثبت درخواست جدید در صورت نیاز به تعمیرات در راه‌پله یا مشاعات'
        };
      case 'parking':
        return {
          name: 'بخش پارکینگ‌ها و انباری‌های مشاع',
          whatCanIDo: 'مشاهده تخصیص جای پارک و انباری به واحدها و تفکیک فضای مهمان',
          suggestion: 'تخصیص پارکینگ‌های بدون استفاده'
        };
      case 'units':
        return {
          name: 'فهرست واحدهای مسکونی',
          whatCanIDo: 'بررسی مشخصات متراژ، ساکنین و مالکین واحدهای ساختمان',
          suggestion: 'به‌روزرسانی تعداد نفرات در صورت جابجایی سکونت'
        };
      case 'members':
        return {
          name: 'مدیریت اعضا، نقش‌ها و مجوزها',
          whatCanIDo: 'کنترل سطح دسترسی هیئت مدیره، تفویض موقت مجوزها و بررسی سوابق امنیتی',
          suggestion: 'تفویض دسترسی مالی به اعضای هیئت مدیره در صورت نیاز'
        };
    }
  };

  const screenDetails = getScreenDetails();

  return (
    <div className={`min-h-screen transition-colors ${
      isHighContrast ? 'bg-[#000000] text-[#FFFFFF]' : 'bg-[#FAF8F5] text-[#1E2320]'
    }`}>
      {/* Top Brand Header */}
      <header className={`sticky top-0 z-40 backdrop-blur-md transition-colors ${
        isHighContrast
          ? 'bg-[#121212] border-b-2 border-[#FFFFFF]'
          : 'bg-[#FAF8F5]/90 border-b border-[#E6E1D8]'
      }`}>
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex items-center justify-between h-20">
            {/* Logo & Brand Identity */}
            <div className="flex items-center gap-3.5">
              <div className={`w-12 h-12 rounded-2xl flex items-center justify-center shadow-sm flex-shrink-0 ${
                isHighContrast
                  ? 'bg-[#FFFFFF] text-[#000000]'
                  : 'bg-[#1B4332] text-[#FFFFFF]'
              }`}>
                <BuildingIcon className="w-6 h-6" />
              </div>
              <div>
                <div className="flex items-center gap-2">
                  <span className="font-extrabold text-xl tracking-tight text-[#1B4332]">اَپیار | Apyar</span>
                  <span className="text-[11px] font-bold px-2 py-0.5 rounded-md bg-[#EAF0EC] text-[#1B4332]">
                    مدیریت هوشمند و آرام
                  </span>
                </div>
                <span className="text-xs text-[#5E6660] block font-medium">
                  آسایش، شفافیت و زندگی در آرامش
                </span>
              </div>
            </div>

            {/* Building Switcher & Role Simulator */}
            <div className="hidden md:flex items-center gap-3">
              {/* Building Selector */}
              <div className="flex items-center gap-2 bg-[#FFFFFF] border border-[#E6E1D8] px-3 py-1.5 rounded-xl text-xs">
                <span className="text-[#858E87]">ساختمان:</span>
                <select
                  value={selectedBuildingId}
                  onChange={(e) => setSelectedBuildingId(e.target.value)}
                  className="font-bold text-[#1B4332] bg-transparent focus:outline-none cursor-pointer"
                >
                  {buildings.map(b => (
                    <option key={b.id} value={b.id}>{b.name}</option>
                  ))}
                </select>
              </div>

              {/* Role Simulator */}
              <div className="flex items-center gap-2 bg-[#FFFFFF] border border-[#E6E1D8] px-3 py-1.5 rounded-xl text-xs">
                <span className="text-[#858E87]">نقش من:</span>
                <select
                  value={userRole}
                  onChange={(e) => setUserRole(e.target.value as Role)}
                  className="font-bold text-[#1B4332] bg-transparent focus:outline-none cursor-pointer"
                >
                  <option value="BUILDING_ADMIN">مدیر ارشد ساختمان</option>
                  <option value="BOARD_MEMBER">عضو هیئت مدیره</option>
                  <option value="OWNER">مالک واحد</option>
                  <option value="TENANT">مستأجر</option>
                </select>
              </div>
            </div>

            {/* Accessibility Quick Toggles */}
            <div className="flex items-center gap-2">
              <button
                onClick={() => setAccessibilitySettings(prev => ({
                  ...prev,
                  mode: prev.mode === 'easy' ? 'standard' : 'easy'
                }))}
                className={`flex items-center gap-1.5 px-3 py-2 rounded-xl text-xs font-bold transition-all cursor-pointer ${
                  isEasy
                    ? 'bg-[#1B4332] text-[#FFFFFF]'
                    : isHighContrast
                      ? 'bg-[#1E1E1E] text-[#FFFFFF] border border-[#FFFFFF]'
                      : 'bg-[#EAF0EC] text-[#1B4332] hover:bg-[#DCE7DF]'
                }`}
                title="تغییر به نمای ساده و بزرگ"
              >
                <Maximize2 className="w-4 h-4" />
                <span className="hidden sm:inline">نمای ساده و خوانا</span>
              </button>

              <button
                onClick={() => setIsAccessibilityModalOpen(true)}
                className={`flex items-center gap-1.5 px-3 py-2 rounded-xl text-xs font-bold transition-all cursor-pointer ${
                  isHighContrast
                    ? 'bg-[#FFFFFF] text-[#000000] border-2 border-[#FFFFFF]'
                    : 'bg-[#FFFFFF] text-[#1E2320] border border-[#E6E1D8] hover:bg-[#FAF8F5]'
                }`}
                aria-label="تنظیمات دسترسی‌پذیری و راحتی بصری"
              >
                <Sliders className="w-4 h-4 text-[#1B4332]" />
                <span className="hidden sm:inline">تنظیمات دیداری</span>
              </button>
            </div>
          </div>

          {/* Sub Navigation Bar */}
          <nav className="flex items-center gap-1 sm:gap-2 overflow-x-auto pb-3 pt-1 border-t border-[#E6E1D8]/60 no-scrollbar">
            {navItems.map((item) => {
              const isActive = activeTab === item.id;
              return (
                <button
                  key={item.id}
                  onClick={() => setActiveTab(item.id)}
                  className={`flex items-center gap-2 px-4 py-2.5 rounded-xl font-bold whitespace-nowrap transition-all cursor-pointer ${
                    isEasy ? 'text-base sm:text-lg min-h-[50px]' : 'text-xs sm:text-sm min-h-[44px]'
                  } ${
                    isActive
                      ? isHighContrast
                        ? 'bg-[#FFFFFF] text-[#000000] border-2 border-[#FFFFFF]'
                        : 'bg-[#1B4332] text-[#FFFFFF] shadow-sm'
                      : isHighContrast
                        ? 'bg-[#000000] text-[#FFFFFF] border border-[#444444] hover:border-[#FFFFFF]'
                        : 'text-[#5E6660] hover:bg-[#EAF0EC] hover:text-[#1B4332]'
                  }`}
                >
                  <span className="flex-shrink-0">{item.icon}</span>
                  <span>{item.title}</span>
                </button>
              );
            })}
          </nav>
        </div>
      </header>

      {/* Main Content Area */}
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Voice Ready Interaction Bar */}
        <VoiceAssistantBar
          settings={accessibilitySettings}
          onExecuteIntent={handleExecuteVoiceIntent}
        />

        {/* Location & Step Guide: «الان کجا هستم؟ چه کاری می‌توانم انجام دهم؟» */}
        <LocationBreadcrumb
          settings={accessibilitySettings}
          currentScreenName={screenDetails.name}
          whatCanIDo={screenDetails.whatCanIDo}
          nextStepSuggestion={screenDetails.suggestion}
        />

        {/* Dynamic Screen View */}
        {activeTab === 'dashboard' && (
          <OverviewDashboard
            settings={accessibilitySettings}
            activeBuilding={activeBuilding}
            userRole={userRole}
            onNavigateTab={setActiveTab}
            onOpenNewRequestModal={() => setIsNewServiceModalOpen(true)}
            onOpenNewExpenseModal={() => setIsNewExpenseModalOpen(true)}
          />
        )}

        {activeTab === 'charges' && (
          <ChargeCalculationSection
            settings={accessibilitySettings}
            activeBuilding={activeBuilding}
            periods={chargePeriods}
            methods={methods}
            expenses={expenses}
            chargeItems={chargeItems}
            onCalculateCharges={handleCalculateCharges}
            onFinalizePeriod={handleFinalizePeriod}
            onOpenNewPeriodModal={() => setIsNewPeriodModalOpen(true)}
            onOpenNewExpenseModal={() => setIsNewExpenseModalOpen(true)}
            onOpenNewMethodModal={() => {}}
            onViewInvoice={(item, period) => setSelectedInvoiceItem({ item, period })}
            onOpenPayment={(item, period) => setSelectedPaymentItem({ item, period })}
          />
        )}

        {activeTab === 'services' && (
          <ServicesSection
            settings={accessibilitySettings}
            activeBuilding={activeBuilding}
            providers={providers}
            records={serviceRecords}
            maintenance={maintenanceRecords}
            onOpenNewRecordModal={() => setIsNewServiceModalOpen(true)}
            onOpenNewProviderModal={() => setIsNewProviderModalOpen(true)}
          />
        )}

        {activeTab === 'parking' && (
          <ParkingStorageSection
            settings={accessibilitySettings}
            activeBuilding={activeBuilding}
            parkingSpaces={parkingSpaces}
            storageUnits={storageUnits}
            onOpenNewParkingModal={() => setIsNewParkingStorageModalOpen(true)}
            onOpenNewStorageModal={() => setIsNewParkingStorageModalOpen(true)}
          />
        )}

        {activeTab === 'units' && (
          <UnitsSection
            settings={accessibilitySettings}
            activeBuilding={activeBuilding}
            units={units}
            onOpenNewUnitModal={() => setIsNewUnitModalOpen(true)}
          />
        )}

        {activeTab === 'members' && (
          <MembersSection
            settings={accessibilitySettings}
            activeBuilding={activeBuilding}
            members={members}
            delegations={delegations}
            onOpenNewMemberModal={() => {}}
            onOpenNewDelegationModal={() => setIsNewDelegationModalOpen(true)}
          />
        )}
      </main>

      {/* Accessibility Control Panel Modal */}
      <AccessibilityControlPanel
        settings={accessibilitySettings}
        onUpdateSettings={setAccessibilitySettings}
        isOpen={isAccessibilityModalOpen}
        onClose={() => setIsAccessibilityModalOpen(false)}
      />

      {/* Modal: New Unit Form */}
      <NewUnitModal
        settings={accessibilitySettings}
        isOpen={isNewUnitModalOpen}
        onClose={() => setIsNewUnitModalOpen(false)}
        building={activeBuilding}
        onAddUnit={handleAddUnit}
      />

      {/* Modal: New Service Provider */}
      <NewProviderModal
        settings={accessibilitySettings}
        isOpen={isNewProviderModalOpen}
        onClose={() => setIsNewProviderModalOpen(false)}
        onAddProvider={handleAddProvider}
      />

      {/* Modal: New Delegation */}
      <NewDelegationModal
        settings={accessibilitySettings}
        isOpen={isNewDelegationModalOpen}
        onClose={() => setIsNewDelegationModalOpen(false)}
        building={activeBuilding}
        members={members}
        onAddDelegation={handleAddDelegation}
      />

      {/* Modal: New Parking & Storage */}
      <NewParkingStorageModal
        settings={accessibilitySettings}
        isOpen={isNewParkingStorageModalOpen}
        onClose={() => setIsNewParkingStorageModalOpen(false)}
        building={activeBuilding}
        units={units}
        onAddParking={handleAddParkingSpace}
        onAddStorage={handleAddStorageUnit}
      />

      {/* Modal: Official Charge Invoice */}
      {selectedInvoiceItem && (
        <InvoiceModal
          settings={accessibilitySettings}
          isOpen={!!selectedInvoiceItem}
          onClose={() => setSelectedInvoiceItem(null)}
          building={activeBuilding}
          period={selectedInvoiceItem.period}
          item={selectedInvoiceItem.item}
          unit={units.find(u => u.id === selectedInvoiceItem.item.unitId)}
          onOpenPayment={(item) => {
            const period = selectedInvoiceItem.period;
            setSelectedInvoiceItem(null);
            setSelectedPaymentItem({ item, period });
          }}
        />
      )}

      {/* Modal: Payment Gateway Simulation (شاپرک) */}
      {selectedPaymentItem && (
        <PaymentGatewayModal
          settings={accessibilitySettings}
          isOpen={!!selectedPaymentItem}
          onClose={() => setSelectedPaymentItem(null)}
          building={activeBuilding}
          period={selectedPaymentItem.period}
          item={selectedPaymentItem.item}
          onPaymentSuccess={handlePaymentSuccess}
        />
      )}

      {/* Modal: New Expense */}
      {isNewExpenseModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/40 backdrop-blur-xs">
          <div className="w-full max-w-lg rounded-3xl p-6 sm:p-8 bg-[#FAF8F5] border border-[#E6E1D8] text-[#1E2320] shadow-2xl">
            <div className="flex items-center justify-between pb-4 border-b border-[#E6E1D8]">
              <h2 className="text-xl font-bold text-[#1B4332]">ثبت هزینه جدید برای ساختمان</h2>
              <button onClick={() => setIsNewExpenseModalOpen(false)} className="p-2 text-[#5E6660]">
                <X className="w-5 h-5" />
              </button>
            </div>

            <div className="space-y-4 mt-6">
              <div>
                <label className="block text-xs font-bold mb-1">عنوان هزینه</label>
                <input
                  type="text"
                  placeholder="مثلاً: تعمیر قفل درب ورودی یا سرویس موتورخانه"
                  value={newExpenseTitle}
                  onChange={(e) => setNewExpenseTitle(e.target.value)}
                  className="w-full p-3 rounded-xl border border-[#E6E1D8] bg-[#FFFFFF] text-sm"
                />
              </div>

              <div>
                <label className="block text-xs font-bold mb-1">مبلغ هزینه (ریال)</label>
                <input
                  type="number"
                  placeholder="مبلغ به ریال"
                  value={newExpenseAmount}
                  onChange={(e) => setNewExpenseAmount(e.target.value)}
                  className="w-full p-3 rounded-xl border border-[#E6E1D8] bg-[#FFFFFF] text-sm"
                />
              </div>

              <div>
                <label className="block text-xs font-bold mb-1">دسته‌بندی</label>
                <select
                  value={newExpenseCategory}
                  onChange={(e) => setNewExpenseCategory(e.target.value)}
                  className="w-full p-3 rounded-xl border border-[#E6E1D8] bg-[#FFFFFF] text-sm"
                >
                  <option value="تعمیرات عمومی">تعمیرات عمومی</option>
                  <option value="سرویس آسانسور">سرویس آسانسور</option>
                  <option value="تاسیسات و موتورخانه">تاسیسات و موتورخانه</option>
                  <option value="نظافت و شستشو">نظافت و شستشو</option>
                  <option value="باغبانی و فضای سبز">باغبانی و فضای سبز</option>
                  <option value="قبوض مشاع">قبوض آب/برق/گاز مشاع</option>
                </select>
              </div>
            </div>

            <div className="mt-8 pt-4 border-t border-[#E6E1D8] flex items-center justify-end gap-3">
              <AccessibleButton
                settings={accessibilitySettings}
                variant="ghost"
                onClick={() => setIsNewExpenseModalOpen(false)}
              >
                انصراف
              </AccessibleButton>
              <AccessibleButton
                settings={accessibilitySettings}
                variant="primary"
                onClick={handleSaveExpense}
              >
                ثبت و الصاق به دوره شارژ
              </AccessibleButton>
            </div>
          </div>
        </div>
      )}

      {/* Modal: New Charge Period */}
      {isNewPeriodModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/40 backdrop-blur-xs">
          <div className="w-full max-w-lg rounded-3xl p-6 sm:p-8 bg-[#FAF8F5] border border-[#E6E1D8] text-[#1E2320] shadow-2xl">
            <div className="flex items-center justify-between pb-4 border-b border-[#E6E1D8]">
              <h2 className="text-xl font-bold text-[#1B4332]">تعریف دوره شارژ جدید</h2>
              <button onClick={() => setIsNewPeriodModalOpen(false)} className="p-2 text-[#5E6660]">
                <X className="w-5 h-5" />
              </button>
            </div>

            <div className="space-y-4 mt-6">
              <div>
                <label className="block text-xs font-bold mb-1">عنوان دوره شارژ</label>
                <input
                  type="text"
                  placeholder="مثلاً: شارژ شهریور ماه ۱۴۰۵"
                  value={newPeriodTitle}
                  onChange={(e) => setNewPeriodTitle(e.target.value)}
                  className="w-full p-3 rounded-xl border border-[#E6E1D8] bg-[#FFFFFF] text-sm"
                />
              </div>

              <div>
                <label className="block text-xs font-bold mb-1">مبلغ برآورد اولیه کل دوره (ریال - اختیاری)</label>
                <input
                  type="number"
                  placeholder="در صورت الصاق هزینه‌ها، خودکار محاسبه می‌شود"
                  value={newPeriodCost}
                  onChange={(e) => setNewPeriodCost(e.target.value)}
                  className="w-full p-3 rounded-xl border border-[#E6E1D8] bg-[#FFFFFF] text-sm"
                />
              </div>
            </div>

            <div className="mt-8 pt-4 border-t border-[#E6E1D8] flex items-center justify-end gap-3">
              <AccessibleButton
                settings={accessibilitySettings}
                variant="ghost"
                onClick={() => setIsNewPeriodModalOpen(false)}
              >
                انصراف
              </AccessibleButton>
              <AccessibleButton
                settings={accessibilitySettings}
                variant="primary"
                onClick={handleSavePeriod}
              >
                ایجاد دوره پیش‌نویس
              </AccessibleButton>
            </div>
          </div>
        </div>
      )}

      {/* Modal: New Service Request */}
      {isNewServiceModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/40 backdrop-blur-xs">
          <div className="w-full max-w-lg rounded-3xl p-6 sm:p-8 bg-[#FAF8F5] border border-[#E6E1D8] text-[#1E2320] shadow-2xl">
            <div className="flex items-center justify-between pb-4 border-b border-[#E6E1D8]">
              <h2 className="text-xl font-bold text-[#1B4332]">ثبت درخواست سرویس یا خرابی</h2>
              <button onClick={() => setIsNewServiceModalOpen(false)} className="p-2 text-[#5E6660]">
                <X className="w-5 h-5" />
              </button>
            </div>

            <div className="space-y-4 mt-6">
              <div>
                <label className="block text-xs font-bold mb-1">موضوع خرابی یا نیاز به سرویس</label>
                <input
                  type="text"
                  placeholder="مثلاً: صدای غیرعادی آسانسور در توقف طبقه دوم"
                  className="w-full p-3 rounded-xl border border-[#E6E1D8] bg-[#FFFFFF] text-sm"
                />
              </div>

              <div>
                <label className="block text-xs font-bold mb-1">بخش مربوطه</label>
                <select className="w-full p-3 rounded-xl border border-[#E6E1D8] bg-[#FFFFFF] text-sm">
                  <option>آسانسور</option>
                  <option>موتورخانه و پمپ آب</option>
                  <option>روشنایی و برق عمومی</option>
                  <option>درب ریموت پارکینگ</option>
                  <option>نظافت مشاعات</option>
                </select>
              </div>

              <div>
                <label className="block text-xs font-bold mb-1">توضیحات تکمیلی</label>
                <textarea
                  rows={3}
                  placeholder="شرح جزئیات برای مدیر و سرویس‌کار..."
                  className="w-full p-3 rounded-xl border border-[#E6E1D8] bg-[#FFFFFF] text-sm"
                />
              </div>
            </div>

            <div className="mt-8 pt-4 border-t border-[#E6E1D8] flex items-center justify-end gap-3">
              <AccessibleButton
                settings={accessibilitySettings}
                variant="ghost"
                onClick={() => setIsNewServiceModalOpen(false)}
              >
                انصراف
              </AccessibleButton>
              <AccessibleButton
                settings={accessibilitySettings}
                variant="primary"
                onClick={() => setIsNewServiceModalOpen(false)}
              >
                ارسال درخواست به مدیریت
              </AccessibleButton>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default App;
