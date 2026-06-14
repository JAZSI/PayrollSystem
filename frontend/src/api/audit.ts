import { api } from './client'
import type { AuditPage } from '@/types/audit'

export const getAudit = (entity: string | undefined, page: number, size = 25) =>
  api
    .get<AuditPage>('/audit', { params: { ...(entity ? { entity } : {}), page, size } })
    .then((r) => r.data)
