import { api } from './client'
import type { Holiday, HolidayRequest } from '@/types/payroll'

export const listHolidays = () =>
  api.get<Holiday[]>('/holidays').then((r) => r.data)

export const createHoliday = (body: HolidayRequest) =>
  api.post<Holiday>('/holidays', body).then((r) => r.data)

export const updateHoliday = (id: number, body: HolidayRequest) =>
  api.put<Holiday>(`/holidays/${id}`, body).then((r) => r.data)

export const deleteHoliday = (id: number) =>
  api.delete(`/holidays/${id}`).then(() => undefined)
