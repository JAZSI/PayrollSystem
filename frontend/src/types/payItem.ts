export type PayItemKind = 'ALLOWANCE' | 'DEDUCTION'

export const PAY_ITEM_KINDS: { value: PayItemKind; label: string }[] = [
  { value: 'ALLOWANCE', label: 'Allowance' },
  { value: 'DEDUCTION', label: 'Deduction' },
]

export interface PayItem {
  id: number
  employeeId: string
  kind: PayItemKind
  name: string
  amount: number
  taxable: boolean
  recurring: boolean
  active: boolean
  createdAt: string | null
}

export interface PayItemRequest {
  employeeId: string
  kind: PayItemKind
  name: string
  amount: number
  taxable: boolean
  recurring: boolean
}
