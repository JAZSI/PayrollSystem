import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import {
  createHoliday,
  deleteHoliday,
  listHolidays,
  updateHoliday,
} from '@/api/holidays'
import type { HolidayRequest } from '@/types/payroll'

const KEY = ['holidays'] as const

export function useHolidays() {
  return useQuery({ queryKey: KEY, queryFn: listHolidays })
}

export function useCreateHoliday() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: createHoliday,
    onSuccess: () => qc.invalidateQueries({ queryKey: KEY }),
  })
}

export function useUpdateHoliday() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: ({ id, body }: { id: number; body: HolidayRequest }) =>
      updateHoliday(id, body),
    onSuccess: () => qc.invalidateQueries({ queryKey: KEY }),
  })
}

export function useDeleteHoliday() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: deleteHoliday,
    onSuccess: () => qc.invalidateQueries({ queryKey: KEY }),
  })
}
