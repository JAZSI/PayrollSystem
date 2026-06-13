import { api } from './client'
import type {
  FileLeaveRequest,
  LeaveBalance,
  LeaveRequest,
  LeaveStatus,
  LeaveType,
  LeaveTypeRequest,
} from '@/types/leave'

// ------------------------------ leave types ------------------------------
export const listLeaveTypes = () =>
  api.get<LeaveType[]>('/leave-types').then((r) => r.data)

export const createLeaveType = (body: LeaveTypeRequest) =>
  api.post<LeaveType>('/leave-types', body).then((r) => r.data)

export const deleteLeaveType = (id: number) =>
  api.delete(`/leave-types/${id}`).then(() => undefined)

// ------------------------------- requests --------------------------------
export const listLeaveRequests = (status?: LeaveStatus) =>
  api
    .get<LeaveRequest[]>('/leave-requests', { params: status ? { status } : {} })
    .then((r) => r.data)

export const fileLeaveRequest = (body: FileLeaveRequest) =>
  api.post<LeaveRequest>('/leave-requests', body).then((r) => r.data)

export const approveLeaveRequest = (id: number) =>
  api.post<LeaveRequest>(`/leave-requests/${id}/approve`).then((r) => r.data)

export const rejectLeaveRequest = (id: number) =>
  api.post<LeaveRequest>(`/leave-requests/${id}/reject`).then((r) => r.data)

export const getEmployeeLeaveBalances = (employeeId: string) =>
  api.get<LeaveBalance[]>(`/employees/${employeeId}/leave-balances`).then((r) => r.data)

// ------------------------------ self-service -----------------------------
export const getMyLeaveBalances = () =>
  api.get<LeaveBalance[]>('/me/leave-balances').then((r) => r.data)

export const getMyLeaveRequests = () =>
  api.get<LeaveRequest[]>('/me/leave-requests').then((r) => r.data)

export const fileMyLeaveRequest = (body: FileLeaveRequest) =>
  api.post<LeaveRequest>('/me/leave-requests', body).then((r) => r.data)
