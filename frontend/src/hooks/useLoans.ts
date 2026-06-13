import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import {
  cancelLoan,
  createLoan,
  getEmployeeLoans,
  getMyLoans,
  updateLoan,
} from '@/api/loans'
import type { LoanRequest } from '@/types/loan'

export function useEmployeeLoans(employeeId: string | null) {
  return useQuery({
    queryKey: ['loans', employeeId],
    queryFn: () => getEmployeeLoans(employeeId as string),
    enabled: Boolean(employeeId),
  })
}

export function useMyLoans() {
  return useQuery({ queryKey: ['my-loans'], queryFn: getMyLoans })
}

export function useCreateLoan() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: createLoan,
    onSuccess: (loan) => qc.invalidateQueries({ queryKey: ['loans', loan.employeeId] }),
  })
}

export function useUpdateLoan() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: ({ id, body }: { id: number; body: LoanRequest }) => updateLoan(id, body),
    onSuccess: (loan) => qc.invalidateQueries({ queryKey: ['loans', loan.employeeId] }),
  })
}

export function useCancelLoan() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: cancelLoan,
    onSuccess: (loan) => qc.invalidateQueries({ queryKey: ['loans', loan.employeeId] }),
  })
}
