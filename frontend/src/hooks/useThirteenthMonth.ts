import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import {
  approveThirteenthMonthRun,
  createThirteenthMonthRun,
  getMyThirteenthMonth,
  getThirteenthMonthRun,
  listThirteenthMonthRuns,
  lockThirteenthMonthRun,
} from '@/api/thirteenthMonth'

export function useThirteenthMonthRuns() {
  return useQuery({ queryKey: ['13th-runs'], queryFn: listThirteenthMonthRuns })
}

export function useThirteenthMonthRun(id: number | null) {
  return useQuery({
    queryKey: ['13th-run', id],
    queryFn: () => getThirteenthMonthRun(id as number),
    enabled: id !== null,
  })
}

export function useMyThirteenthMonth() {
  return useQuery({ queryKey: ['my-13th'], queryFn: getMyThirteenthMonth })
}

export function useCreateThirteenthMonthRun() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: createThirteenthMonthRun,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['13th-runs'] }),
  })
}

export function useThirteenthMonthTransition(kind: 'approve' | 'lock') {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (id: number) =>
      kind === 'approve' ? approveThirteenthMonthRun(id) : lockThirteenthMonthRun(id),
    onSuccess: (run) => {
      qc.invalidateQueries({ queryKey: ['13th-runs'] })
      qc.invalidateQueries({ queryKey: ['13th-run', run.id] })
    },
  })
}
