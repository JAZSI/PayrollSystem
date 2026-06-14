export interface AuditEntry {
  id: number
  username: string
  role: string | null
  action: string
  entity: string
  entityId: string | null
  summary: string | null
  createdAt: string | null
}

export interface AuditPage {
  content: AuditEntry[]
  page: number
  size: number
  totalElements: number
  totalPages: number
}

export const AUDIT_ENTITIES = [
  'PayrollRun',
  'Employee',
  'Loan',
  'LeaveRequest',
  'Holiday',
  'User',
] as const
