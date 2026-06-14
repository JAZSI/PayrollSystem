import { useQuery } from '@tanstack/react-query'
import { getBank, getRegister, getRemittance } from '@/api/reports'

export function useRegister(period: string) {
  return useQuery({
    queryKey: ['report-register', period],
    queryFn: () => getRegister(period),
    enabled: Boolean(period),
  })
}

export function useRemittance(period: string) {
  return useQuery({
    queryKey: ['report-remittance', period],
    queryFn: () => getRemittance(period),
    enabled: Boolean(period),
  })
}

export function useBank(period: string) {
  return useQuery({
    queryKey: ['report-bank', period],
    queryFn: () => getBank(period),
    enabled: Boolean(period),
  })
}
