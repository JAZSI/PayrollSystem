import type { EmployeeType } from './employee'

export interface Settings {
  workingDays: number
  workdayStartHour: number
  overtimeStartHour: number
  lunchStartHour: number
  leaveRegular: number
  leaveProbationary: number
  leaveContractual: number
  leavePartTimer: number
}

export type HolidayType = 'NONE' | 'REGULAR_HOLIDAY' | 'SPECIAL_OR_REST_DAY'

export const HOLIDAY_TYPES: { value: HolidayType; label: string }[] = [
  { value: 'NONE', label: 'Regular Day' },
  { value: 'REGULAR_HOLIDAY', label: 'Regular Holiday' },
  { value: 'SPECIAL_OR_REST_DAY', label: 'Special / Rest Day' },
]

export interface TimeRecord {
  id?: number
  dayNumber: number
  timeIn: number
  timeOut: number
  absent: boolean
  holidayType: HolidayType
}

export interface DayCalendar {
  dayNumber: number
  holidayType: HolidayType
  holidayName: string | null
}

export interface Holiday {
  id: number
  date: string // ISO yyyy-MM-dd
  name: string
  type: HolidayType
}

export interface HolidayRequest {
  date: string
  name: string
  type: HolidayType
}

export type KioskAction = 'CLOCK_IN' | 'CLOCK_OUT' | 'ALREADY_COMPLETE'

export interface KioskResult {
  action: KioskAction
  employeeId: string
  employeeName: string
  time: string
  date: string
  message: string
}

export interface SaveAttendanceRequest {
  employeeId: string
  cutoffPeriod: string
  year: number
  month: number
  records: TimeRecord[]
}

export interface RunPayrollRequest {
  employeeId: string
  period: string
}

export interface Dashboard {
  activeEmployees: number
  totalEmployees: number
  totalPayslips: number
  totalRuns: number
  draftRuns: number
  approvedRuns: number
  lockedRuns: number
  latestRun: PayrollRun | null
}

export type PayrollRunStatus = 'DRAFT' | 'APPROVED' | 'LOCKED'

export interface PayrollRun {
  id: number
  cutoffPeriod: string
  status: PayrollRunStatus
  employeeCount: number
  totalGross: number
  totalDeductions: number
  totalNet: number
  createdAt: string | null
  payslips: Payslip[] | null
}

export interface Payslip {
  id: number
  employeeId: string
  employeeName: string
  employeeType: EmployeeType
  employeeTypeLabel: string
  cutoffPeriod: string
  totalHours: number
  overtimeHours: number
  undertimeHours: number
  absentDays: number
  basicPay: number
  overtimePay: number
  nightDiffPay: number
  allowances: number
  grossPay: number
  sss: number
  philhealth: number
  pagibig: number
  tax: number
  loan: number
  otherDeductions: number
  undertimePenalty: number
  absencePenalty: number
  employerSss: number
  employerPhilhealth: number
  employerPagibig: number
  employerEc: number
  netPay: number
  createdAt: string | null
}
