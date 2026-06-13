import { api } from './client'
import type {
  DayCalendar,
  PayrollRun,
  Payslip,
  RunPayrollRequest,
  SaveAttendanceRequest,
  Settings,
  TimeRecord,
} from '@/types/payroll'

// ------------------------------ settings ------------------------------
export const getSettings = () =>
  api.get<Settings>('/settings').then((r) => r.data)

export const updateSettings = (body: Settings) =>
  api.put<Settings>('/settings', body).then((r) => r.data)

// ----------------------------- attendance -----------------------------
export const getWorkingDays = (period: string, year: number, month: number) =>
  api
    .get<number[]>('/attendance/working-days', { params: { period, year, month } })
    .then((r) => r.data)

export const getAttendanceCalendar = (period: string, year: number, month: number) =>
  api
    .get<DayCalendar[]>('/attendance/calendar', { params: { period, year, month } })
    .then((r) => r.data)

export const getAttendance = (employeeId: string, period: string) =>
  api
    .get<TimeRecord[]>('/attendance', { params: { employeeId, period } })
    .then((r) => r.data)

export const saveAttendance = (body: SaveAttendanceRequest) =>
  api.put<TimeRecord[]>('/attendance', body).then((r) => r.data)

// ------------------------------ payroll -------------------------------
export const runPayroll = (body: RunPayrollRequest) =>
  api.post<Payslip>('/payroll/run', body).then((r) => r.data)

export const listPayslips = (employeeId?: string) =>
  api
    .get<Payslip[]>('/payslips', { params: employeeId ? { employeeId } : {} })
    .then((r) => r.data)

/** Self-service: the current user's own payslips. */
export const listMyPayslips = () =>
  api.get<Payslip[]>('/payslips/me').then((r) => r.data)

// -------------------------- batch payroll runs ------------------------
export const createRun = (period: string) =>
  api.post<PayrollRun>('/payroll/runs', { period }).then((r) => r.data)

export const listRuns = () =>
  api.get<PayrollRun[]>('/payroll/runs').then((r) => r.data)

export const getRun = (id: number) =>
  api.get<PayrollRun>(`/payroll/runs/${id}`).then((r) => r.data)

export const approveRun = (id: number) =>
  api.post<PayrollRun>(`/payroll/runs/${id}/approve`).then((r) => r.data)

export const lockRun = (id: number) =>
  api.post<PayrollRun>(`/payroll/runs/${id}/lock`).then((r) => r.data)

/** Fetches the payslip PDF (with the auth token) and triggers a browser download. */
export async function downloadPayslipPdf(id: number) {
  const res = await api.get(`/payslips/${id}/pdf`, { responseType: 'blob' })
  const url = URL.createObjectURL(res.data as Blob)
  const link = document.createElement('a')
  link.href = url
  link.download = `payslip-${id}.pdf`
  document.body.appendChild(link)
  link.click()
  link.remove()
  URL.revokeObjectURL(url)
}
