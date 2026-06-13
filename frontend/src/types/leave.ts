export type LeaveStatus = 'PENDING' | 'APPROVED' | 'REJECTED'

export interface LeaveType {
  id: number
  name: string
  paid: boolean
  defaultAnnualCredits: number
}

export interface LeaveTypeRequest {
  name: string
  paid: boolean
  defaultAnnualCredits: number
}

export interface LeaveBalance {
  leaveTypeId: number
  leaveTypeName: string
  paid: boolean
  year: number
  credits: number
  used: number
  remaining: number
}

export interface LeaveRequest {
  id: number
  employeeId: string
  leaveTypeId: number
  leaveTypeName: string
  startDate: string
  endDate: string
  days: number
  status: LeaveStatus
  reason: string | null
  decidedBy: string | null
  createdAt: string | null
}

export interface FileLeaveRequest {
  employeeId?: string
  leaveTypeId: number
  startDate: string
  endDate: string
  reason?: string
}
