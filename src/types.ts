// Types and Interfaces for APYAR Application

export type AccessibilityMode = 'standard' | 'high-contrast' | 'easy';
export type TextSize = 'normal' | 'large' | 'extra';

export interface AccessibilitySettings {
  mode: AccessibilityMode;
  textSize: TextSize;
  reduceMotion: boolean;
  screenReaderDescriptions: boolean;
}

export type Role = 'BUILDING_ADMIN' | 'BOARD_MEMBER' | 'OWNER' | 'TENANT' | 'RESIDENT' | 'APYAR_EXECUTIVE';

export type Permission = 
  | 'VIEW_BUILDING'
  | 'MANAGE_BUILDING'
  | 'VIEW_UNIT'
  | 'MANAGE_UNITS'
  | 'VIEW_RESIDENTS'
  | 'MANAGE_RESIDENTS'
  | 'MANAGE_MEMBERS'
  | 'DELEGATE_PERMISSIONS'
  | 'VIEW_FINANCIAL_DATA'
  | 'MANAGE_FINANCIAL_DATA'
  | 'CALCULATE_CHARGE'
  | 'FINALIZE_CHARGE'
  | 'VIEW_SERVICES'
  | 'MANAGE_SERVICES'
  | 'VIEW_PARKING_STORAGE'
  | 'MANAGE_PARKING_STORAGE'
  | 'VIEW_REPORTS';

export interface Building {
  id: string;
  name: string;
  address: string;
  city: string;
  postalCode: string;
  unitCount: number;
  createdAt: number;
  isActive: boolean;
}

export interface Unit {
  id: string;
  buildingId: string;
  unitNumber: string;
  floor: number;
  areaSquareMeters: number;
  residentCount: number;
  parkingCount: number;
  storageCount: number;
  occupancyStatus: 'OCCUPIED' | 'VACANT' | 'UNKNOWN';
  ownerName?: string;
  tenantName?: string;
  isActive: boolean;
}

export interface Person {
  id: string;
  firstName: string;
  lastName: string;
  mobileNumber: string;
  email?: string;
}

export interface BuildingMember {
  id: string;
  buildingId: string;
  userId: string;
  userName: string;
  userPhone: string;
  role: Role;
  startDate: number;
  isActive: boolean;
}

export interface DelegatedPermission {
  id: string;
  buildingId: string;
  grantedByUserId: string;
  grantedToUserId: string;
  granteeName: string;
  permission: Permission;
  startDate: number;
  endDate: number;
  status: 'ACTIVE' | 'REVOKED' | 'EXPIRED';
  note?: string;
}

// Stage 7.1 Charge Models
export type MethodType = 'EQUAL' | 'AREA_BASED' | 'RESIDENT_BASED' | 'MIXED' | 'CUSTOM';
export type ComponentType = 'AREA' | 'RESIDENT_COUNT' | 'UNIT_COUNT' | 'FIXED_AMOUNT' | 'OCCUPANCY' | 'CUSTOM_RULE';

export interface FormulaComponent {
  id: string;
  calculationMethodId: string;
  componentType: ComponentType;
  weight: number;
  enabled: boolean;
  configuration: Record<string, string>;
}

export interface ChargeCalculationMethod {
  id: string;
  buildingId: string;
  name: string;
  description?: string;
  methodType: MethodType;
  isDefault: boolean;
  isActive: boolean;
  createdAt: number;
  createdBy: string;
  components: FormulaComponent[];
}

export interface BuildingExpense {
  id: string;
  buildingId: string;
  chargePeriodId?: string;
  title: string;
  description?: string;
  category: string;
  amount: number;
  expenseDate: number;
  createdBy: string;
  status: 'RECORDED' | 'APPROVED' | 'ALLOCATED' | 'REJECTED';
}

export interface ChargePeriod {
  id: string;
  buildingId: string;
  title: string;
  periodStart: number;
  periodEnd: number;
  dueDate: number;
  status: 'DRAFT' | 'CALCULATED' | 'PUBLISHED' | 'PARTIALLY_PAID' | 'SETTLED' | 'CANCELLED' | 'FINALIZED';
  calculationMethodId: string;
  totalBuildingCost: number;
  createdAt: number;
  createdBy: string;
  finalizedAt?: number;
}

export interface ChargeItem {
  id: string;
  chargePeriodId: string;
  buildingId: string;
  unitId: string;
  unitNumber: string;
  calculationMethodId: string;
  baseAmount: number;
  adjustments: number;
  finalAmount: number;
  paidAmount: number;
  status: 'CALCULATED' | 'ISSUED' | 'PAID' | 'PARTIALLY_PAID' | 'OVERDUE';
}

// Services & Maintenance Models (Stage 6)
export interface ServiceProvider {
  id: string;
  name: string;
  specialty: string;
  phone: string;
  rating: number;
  status: 'ACTIVE' | 'SUSPENDED';
}

export interface ServiceRecord {
  id: string;
  buildingId: string;
  providerId: string;
  providerName: string;
  title: string;
  category: string;
  cost: number;
  serviceDate: number;
  status: 'PENDING' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED';
  description?: string;
}

export interface MaintenanceRecord {
  id: string;
  buildingId: string;
  equipmentName: string;
  lastCheckDate: number;
  nextCheckDate: number;
  status: 'HEALTHY' | 'NEEDS_INSPECTION' | 'CRITICAL';
  notes?: string;
}

// Parking & Storage Models (Stage 5)
export interface ParkingSpace {
  id: string;
  buildingId: string;
  spaceNumber: string;
  floor: number;
  assignedUnitId?: string;
  assignedUnitNumber?: string;
  isGuestSpace: boolean;
  status: 'OCCUPIED' | 'VACANT' | 'RESERVED';
}

export interface StorageUnit {
  id: string;
  buildingId: string;
  storageNumber: string;
  floor: number;
  areaSquareMeters: number;
  assignedUnitId?: string;
  assignedUnitNumber?: string;
  status: 'OCCUPIED' | 'VACANT';
}

// Voice Command Simulation Interface
export interface VoiceCommandIntent {
  rawVoicePhrase: string;
  normalizedAction: string;
  targetTab: 'dashboard' | 'charges' | 'services' | 'parking' | 'units' | 'members';
  description: string;
}
