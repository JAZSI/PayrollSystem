import { keepPreviousData, useQuery } from '@tanstack/react-query'
import { getAudit } from '@/api/audit'

export function useAudit(entity: string | undefined, page: number) {
  return useQuery({
    queryKey: ['audit', entity ?? 'all', page],
    queryFn: () => getAudit(entity, page),
    placeholderData: keepPreviousData,
  })
}
