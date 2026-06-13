import { useState } from 'react'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import {
  faLayerGroup,
  faCircleCheck,
  faLock,
  faPlus,
} from '@fortawesome/free-solid-svg-icons'
import { toast } from 'sonner'
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import {
  useCreateRun,
  usePayrollRun,
  usePayrollRuns,
  useRunTransition,
} from '@/hooks/usePayroll'
import { toApiError } from '@/api/client'
import { peso, PERIODS } from '@/lib/format'
import type { PayrollRunStatus } from '@/types/payroll'

const badgeVariant: Record<PayrollRunStatus, 'secondary' | 'default' | 'outline'> = {
  DRAFT: 'secondary',
  APPROVED: 'default',
  LOCKED: 'outline',
}

export function PayrollRunsPage() {
  const [period, setPeriod] = useState<string>(PERIODS[0])
  const [openId, setOpenId] = useState<number | null>(null)
  const runs = usePayrollRuns()
  const detail = usePayrollRun(openId)
  const create = useCreateRun()
  const approve = useRunTransition('approve')
  const lock = useRunTransition('lock')

  function onCreate() {
    create.mutate(period, {
      onSuccess: (run) => {
        toast.success(`Run #${run.id} created for ${run.cutoffPeriod}`)
        setOpenId(run.id)
      },
      onError: (err) => toast.error(toApiError(err).message),
    })
  }

  function transition(kind: 'approve' | 'lock', id: number) {
    const m = kind === 'approve' ? approve : lock
    m.mutate(id, {
      onSuccess: (run) => toast.success(`Run #${run.id} ${run.status.toLowerCase()}`),
      onError: (err) => toast.error(toApiError(err).message),
    })
  }

  const run = detail.data

  return (
    <Card>
      <CardHeader className="flex flex-row items-start justify-between gap-4">
        <div>
          <CardTitle className="flex items-center gap-2">
            <FontAwesomeIcon icon={faLayerGroup} className="text-muted-foreground" />
            Payroll Runs
          </CardTitle>
          <CardDescription>
            Batch-process all active employees for a cut-off, then approve and lock.
          </CardDescription>
        </div>
        <div className="flex items-end gap-2">
          <Select value={period} onValueChange={(v) => setPeriod(v ?? period)}>
            <SelectTrigger className="w-36">
              <SelectValue />
            </SelectTrigger>
            <SelectContent>
              {PERIODS.map((p) => (
                <SelectItem key={p} value={p}>
                  {p}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
          <Button onClick={onCreate} disabled={create.isPending}>
            <FontAwesomeIcon icon={faPlus} className="mr-2" />
            {create.isPending ? 'Running…' : 'New run'}
          </Button>
        </div>
      </CardHeader>
      <CardContent>
        {runs.data && (
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>#</TableHead>
                <TableHead>Cut-off</TableHead>
                <TableHead>Status</TableHead>
                <TableHead className="text-right">Employees</TableHead>
                <TableHead className="text-right">Gross</TableHead>
                <TableHead className="text-right">Net</TableHead>
                <TableHead></TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {runs.data.length === 0 && (
                <TableRow>
                  <TableCell colSpan={7} className="text-muted-foreground text-center">
                    No runs yet — create one above.
                  </TableCell>
                </TableRow>
              )}
              {runs.data.map((r) => (
                <TableRow key={r.id}>
                  <TableCell className="font-mono">{r.id}</TableCell>
                  <TableCell>{r.cutoffPeriod}</TableCell>
                  <TableCell>
                    <Badge variant={badgeVariant[r.status]}>{r.status}</Badge>
                  </TableCell>
                  <TableCell className="text-right">{r.employeeCount}</TableCell>
                  <TableCell className="text-right tabular-nums">{peso(r.totalGross)}</TableCell>
                  <TableCell className="text-right font-semibold tabular-nums">
                    {peso(r.totalNet)}
                  </TableCell>
                  <TableCell className="text-right">
                    <Button variant="ghost" size="sm" onClick={() => setOpenId(r.id)}>
                      View
                    </Button>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        )}
      </CardContent>

      <Dialog open={openId !== null} onOpenChange={(o) => !o && setOpenId(null)}>
        <DialogContent className="sm:max-w-2xl">
          <DialogHeader>
            <DialogTitle className="flex items-center gap-2">
              Run #{run?.id}
              {run && <Badge variant={badgeVariant[run.status]}>{run.status}</Badge>}
            </DialogTitle>
            <DialogDescription>
              {run?.cutoffPeriod} · {run?.employeeCount} employees · net{' '}
              {run ? peso(run.totalNet) : ''}
            </DialogDescription>
          </DialogHeader>

          {run && (
            <>
              <div className="max-h-80 overflow-y-auto">
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>Employee</TableHead>
                      <TableHead className="text-right">Gross</TableHead>
                      <TableHead className="text-right">Deductions</TableHead>
                      <TableHead className="text-right">Net</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {run.payslips?.map((p) => (
                      <TableRow key={p.id}>
                        <TableCell className="font-medium">{p.employeeName}</TableCell>
                        <TableCell className="text-right tabular-nums">
                          {peso(p.grossPay)}
                        </TableCell>
                        <TableCell className="text-right tabular-nums">
                          {peso(p.grossPay - p.netPay)}
                        </TableCell>
                        <TableCell className="text-right font-semibold tabular-nums">
                          {peso(p.netPay)}
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </div>

              <div className="flex justify-end gap-2 pt-2">
                <Button
                  variant="outline"
                  disabled={run.status !== 'DRAFT' || approve.isPending}
                  onClick={() => transition('approve', run.id)}
                >
                  <FontAwesomeIcon icon={faCircleCheck} className="mr-2" />
                  Approve
                </Button>
                <Button
                  disabled={run.status !== 'APPROVED' || lock.isPending}
                  onClick={() => transition('lock', run.id)}
                >
                  <FontAwesomeIcon icon={faLock} className="mr-2" />
                  Lock
                </Button>
              </div>
            </>
          )}
        </DialogContent>
      </Dialog>
    </Card>
  )
}
