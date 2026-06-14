export type EmployeeType =
  | 'REGULAR'
  | 'PROBATIONARY'
  | 'CONTRACTUAL'
  | 'PART_TIMER'

export const EMPLOYEE_TYPES: { value: EmployeeType; label: string }[] = [
  { value: 'REGULAR', label: 'Regular' },
  { value: 'PROBATIONARY', label: 'Probationary' },
  { value: 'CONTRACTUAL', label: 'Contractual' },
  { value: 'PART_TIMER', label: 'Part-Timer' },
]

export interface Employee {
  id: string
  fullName: string
  type: EmployeeType
  typeLabel: string
  monthlyRate: number
  hourlyRate: number
  bankAccount: string | null
  active: boolean
  createdAt: string | null
}

export interface EmployeeRequest {
  id: string
  fullName: string
  type: EmployeeType
  monthlyRate: number
  hourlyRate: number
  bankAccount?: string | null
  password?: string | null
}
