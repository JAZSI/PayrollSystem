export interface RegisterRow {
  employeeId: string
  employeeName: string
  grossPay: number
  sss: number
  philhealth: number
  pagibig: number
  tax: number
  loan: number
  otherDeductions: number
  penalties: number
  totalDeductions: number
  netPay: number
}

export interface RegisterReport {
  period: string
  rows: RegisterRow[]
  totalGross: number
  totalDeductions: number
  totalNet: number
}

export interface RemittanceReport {
  period: string
  sssEmployee: number
  sssEmployer: number
  sssEc: number
  philhealthEmployee: number
  philhealthEmployer: number
  pagibigEmployee: number
  pagibigEmployer: number
  tax: number
  grandTotal: number
}

export interface BankRow {
  employeeId: string
  employeeName: string
  bankAccount: string
  netPay: number
}

export interface BankReport {
  period: string
  rows: BankRow[]
  totalNet: number
}

export type ReportType = 'register' | 'remittance' | 'bank'
