import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { createUser, listUsers } from '@/api/users'

export function useUsers() {
  return useQuery({ queryKey: ['users'], queryFn: listUsers })
}

export function useCreateUser() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: createUser,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['users'] }),
  })
}
