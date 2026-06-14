import { api } from './client'
import type {
  BankReport,
  RegisterReport,
  RemittanceReport,
  ReportType,
} from '@/types/report'

export const getRegister = (period: string) =>
  api.get<RegisterReport>('/reports/register', { params: { period } }).then((r) => r.data)

export const getRemittance = (period: string) =>
  api.get<RemittanceReport>('/reports/remittance', { params: { period } }).then((r) => r.data)

export const getBank = (period: string) =>
  api.get<BankReport>('/reports/bank', { params: { period } }).then((r) => r.data)

/** Downloads a report CSV (with the auth token) and triggers a browser download. */
export async function downloadReportCsv(type: ReportType, period: string) {
  const res = await api.get('/reports/export', {
    params: { type, period },
    responseType: 'blob',
  })
  const url = URL.createObjectURL(res.data as Blob)
  const link = document.createElement('a')
  link.href = url
  link.download = `${type}-${period}.csv`
  document.body.appendChild(link)
  link.click()
  link.remove()
  URL.revokeObjectURL(url)
}
