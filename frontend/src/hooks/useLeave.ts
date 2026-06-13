import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import {
  approveLeaveRequest,
  createLeaveType,
  deleteLeaveType,
  fileLeaveRequest,
  fileMyLeaveRequest,
  getMyLeaveBalances,
  getMyLeaveRequests,
  listLeaveRequests,
  listLeaveTypes,
  rejectLeaveRequest,
} from '@/api/leave'
import type { LeaveStatus } from '@/types/leave'

export function useLeaveTypes() {
  return useQuery({ queryKey: ['leave-types'], queryFn: listLeaveTypes })
}

export function useLeaveRequests(status?: LeaveStatus) {
  return useQuery({
    queryKey: ['leave-requests', status ?? 'all'],
    queryFn: () => listLeaveRequests(status),
  })
}

export function useMyLeaveBalances() {
  return useQuery({ queryKey: ['my-leave-balances'], queryFn: getMyLeaveBalances })
}

export function useMyLeaveRequests() {
  return useQuery({ queryKey: ['my-leave-requests'], queryFn: getMyLeaveRequests })
}

export function useCreateLeaveType() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: createLeaveType,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['leave-types'] }),
  })
}

export function useDeleteLeaveType() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: deleteLeaveType,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['leave-types'] }),
  })
}

/** Approve/reject from the staff queue; invalidates every request list. */
export function useDecideLeaveRequest(kind: 'approve' | 'reject') {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => (kind === 'approve' ? approveLeaveRequest(id) : rejectLeaveRequest(id)),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['leave-requests'] }),
  })
}

export function useFileLeave(self: boolean) {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: self ? fileMyLeaveRequest : fileLeaveRequest,
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['leave-requests'] })
      qc.invalidateQueries({ queryKey: ['my-leave-requests'] })
      qc.invalidateQueries({ queryKey: ['my-leave-balances'] })
    },
  })
}
