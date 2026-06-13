import { api } from './client'
import type { MyThirteenthMonth, ThirteenthMonthRun } from '@/types/thirteenthMonth'

const BASE = '/thirteenth-month'

export const createThirteenthMonthRun = (year: number) =>
  api.post<ThirteenthMonthRun>(`${BASE}/runs`, { year }).then((r) => r.data)

export const listThirteenthMonthRuns = () =>
  api.get<ThirteenthMonthRun[]>(`${BASE}/runs`).then((r) => r.data)

export const getThirteenthMonthRun = (id: number) =>
  api.get<ThirteenthMonthRun>(`${BASE}/runs/${id}`).then((r) => r.data)

export const approveThirteenthMonthRun = (id: number) =>
  api.post<ThirteenthMonthRun>(`${BASE}/runs/${id}/approve`).then((r) => r.data)

export const lockThirteenthMonthRun = (id: number) =>
  api.post<ThirteenthMonthRun>(`${BASE}/runs/${id}/lock`).then((r) => r.data)

export const getMyThirteenthMonth = () =>
  api.get<MyThirteenthMonth[]>('/me/thirteenth-month').then((r) => r.data)

/** Downloads a 13th-month entry PDF (with the auth token). */
export async function downloadThirteenthMonthPdf(entryId: number) {
  const res = await api.get(`${BASE}/entries/${entryId}/pdf`, { responseType: 'blob' })
  const url = URL.createObjectURL(res.data as Blob)
  const link = document.createElement('a')
  link.href = url
  link.download = `13th-month-${entryId}.pdf`
  document.body.appendChild(link)
  link.click()
  link.remove()
  URL.revokeObjectURL(url)
}
