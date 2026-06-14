import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import {
  createStatutoryTable,
  deleteStatutoryTable,
  listStatutoryTables,
  updateStatutoryTable,
} from '@/api/statutory'
import type { ContributionTableRequest } from '@/types/statutory'

const KEY = ['statutory-tables'] as const

export function useStatutoryTables() {
  return useQuery({ queryKey: KEY, queryFn: listStatutoryTables })
}

export function useCreateStatutoryTable() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: createStatutoryTable,
    onSuccess: () => qc.invalidateQueries({ queryKey: KEY }),
  })
}

export function useUpdateStatutoryTable() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: ({ id, body }: { id: number; body: ContributionTableRequest }) =>
      updateStatutoryTable(id, body),
    onSuccess: () => qc.invalidateQueries({ queryKey: KEY }),
  })
}

export function useDeleteStatutoryTable() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: deleteStatutoryTable,
    onSuccess: () => qc.invalidateQueries({ queryKey: KEY }),
  })
}
