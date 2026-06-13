import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import {
  approveRun,
  createRun,
  getAttendance,
  getAttendanceCalendar,
  getRun,
  getSettings,
  getWorkingDays,
  listMyPayslips,
  listPayslips,
  listRuns,
  lockRun,
  runPayroll,
  saveAttendance,
  updateSettings,
} from '@/api/payroll'

export function useAttendanceCalendar(period: string, year: number, month: number) {
  return useQuery({
    queryKey: ['attendance-calendar', period, year, month],
    queryFn: () => getAttendanceCalendar(period, year, month),
    enabled: Boolean(period),
  })
}

export function useMyPayslips() {
  return useQuery({ queryKey: ['my-payslips'], queryFn: listMyPayslips })
}

export function usePayrollRuns() {
  return useQuery({ queryKey: ['runs'], queryFn: listRuns })
}

/** True when the given cut-off period has a LOCKED run (changes should be blocked). */
export function useIsPeriodLocked(period: string) {
  const runs = usePayrollRuns()
  return (
    runs.data?.some((r) => r.cutoffPeriod === period && r.status === 'LOCKED') ?? false
  )
}

export function usePayrollRun(id: number | null) {
  return useQuery({
    queryKey: ['run', id],
    queryFn: () => getRun(id as number),
    enabled: id !== null,
  })
}

export function useCreateRun() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: createRun,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['runs'] }),
  })
}

export function useRunTransition(kind: 'approve' | 'lock') {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => (kind === 'approve' ? approveRun(id) : lockRun(id)),
    onSuccess: (run) => {
      qc.invalidateQueries({ queryKey: ['runs'] })
      qc.invalidateQueries({ queryKey: ['run', run.id] })
    },
  })
}

export function useWorkingDays(period: string, year: number, month: number) {
  return useQuery({
    queryKey: ['working-days', period, year, month],
    queryFn: () => getWorkingDays(period, year, month),
    enabled: Boolean(period),
  })
}

export function useSettings() {
  return useQuery({ queryKey: ['settings'], queryFn: getSettings })
}

export function useUpdateSettings() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: updateSettings,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['settings'] }),
  })
}

export function useAttendance(employeeId: string, period: string) {
  return useQuery({
    queryKey: ['attendance', employeeId, period],
    queryFn: () => getAttendance(employeeId, period),
    enabled: Boolean(employeeId && period),
  })
}

export function useSaveAttendance() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: saveAttendance,
    onSuccess: (_data, vars) =>
      qc.invalidateQueries({
        queryKey: ['attendance', vars.employeeId, vars.cutoffPeriod],
      }),
  })
}

export function useRunPayroll() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: runPayroll,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['payslips'] }),
  })
}

export function usePayslips(employeeId?: string) {
  return useQuery({
    queryKey: ['payslips', employeeId ?? 'all'],
    queryFn: () => listPayslips(employeeId),
  })
}
