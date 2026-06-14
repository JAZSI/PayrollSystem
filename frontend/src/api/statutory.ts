import { api } from './client'
import type { ContributionTable, ContributionTableRequest } from '@/types/statutory'

export const listStatutoryTables = () =>
  api.get<ContributionTable[]>('/statutory-tables').then((r) => r.data)

export const createStatutoryTable = (body: ContributionTableRequest) =>
  api.post<ContributionTable>('/statutory-tables', body).then((r) => r.data)

export const updateStatutoryTable = (id: number, body: ContributionTableRequest) =>
  api.put<ContributionTable>(`/statutory-tables/${id}`, body).then((r) => r.data)

export const deleteStatutoryTable = (id: number) =>
  api.delete(`/statutory-tables/${id}`).then(() => undefined)
