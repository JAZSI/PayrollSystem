import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import {
  createEmployee,
  deactivateEmployee,
  listEmployees,
  updateEmployee,
} from '@/api/employees'
import type { EmployeeRequest } from '@/types/employee'

const KEY = ['employees'] as const

export function useEmployees() {
  return useQuery({ queryKey: KEY, queryFn: listEmployees })
}

export function useCreateEmployee() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: createEmployee,
    onSuccess: () => qc.invalidateQueries({ queryKey: KEY }),
  })
}

export function useUpdateEmployee() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: ({ id, body }: { id: string; body: EmployeeRequest }) =>
      updateEmployee(id, body),
    onSuccess: () => qc.invalidateQueries({ queryKey: KEY }),
  })
}

export function useDeactivateEmployee() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: deactivateEmployee,
    onSuccess: () => qc.invalidateQueries({ queryKey: KEY }),
  })
}
