export type LoanType = 'SSS' | 'PAGIBIG' | 'COMPANY' | 'CASH_ADVANCE'

export const LOAN_TYPES: { value: LoanType; label: string }[] = [
  { value: 'SSS', label: 'SSS Loan' },
  { value: 'PAGIBIG', label: 'Pag-IBIG Loan' },
  { value: 'COMPANY', label: 'Company Loan' },
  { value: 'CASH_ADVANCE', label: 'Cash Advance' },
]

export type LoanStatus = 'ACTIVE' | 'PAID' | 'CANCELLED'

export interface Loan {
  id: number
  employeeId: string
  type: LoanType
  typeLabel: string
  principal: number
  perCutoffAmount: number
  balance: number
  status: LoanStatus
  startPeriod: string | null
  createdAt: string | null
}

export interface LoanRequest {
  employeeId: string
  type: LoanType
  principal: number
  perCutoffAmount: number
  startPeriod?: string | null
}
