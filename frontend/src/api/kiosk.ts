import { api } from './client'
import type { KioskResult } from '@/types/payroll'

/** Public time-clock punch — clocks the employee in or out based on the current time. */
export const kioskClock = (employeeId: string) =>
  api.post<KioskResult>('/kiosk/clock', { employeeId }).then((r) => r.data)
