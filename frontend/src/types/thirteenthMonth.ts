import type { PayrollRunStatus } from './payroll'

export interface ThirteenthMonthEntry {
  id: number
  employeeId: string
  employeeName: string
  totalBasic: number
  amount: number
}

export interface ThirteenthMonthRun {
  id: number
  year: number
  status: PayrollRunStatus
  employeeCount: number
  totalAmount: number
  createdAt: string | null
  entries: ThirteenthMonthEntry[] | null
}

export interface MyThirteenthMonth {
  year: number
  amount: number
}
