import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import {
  createPayItem,
  deletePayItem,
  getEmployeePayItems,
} from '@/api/payItems'

export function useEmployeePayItems(employeeId: string | null) {
  return useQuery({
    queryKey: ['pay-items', employeeId],
    queryFn: () => getEmployeePayItems(employeeId as string),
    enabled: Boolean(employeeId),
  })
}

export function useCreatePayItem() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: createPayItem,
    onSuccess: (item) => qc.invalidateQueries({ queryKey: ['pay-items', item.employeeId] }),
  })
}

export function useDeletePayItem(employeeId: string) {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: deletePayItem,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['pay-items', employeeId] }),
  })
}
