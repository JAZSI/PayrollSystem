import { useState } from 'react'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import {
  faGift,
  faCircleCheck,
  faLock,
  faPlus,
  faFilePdf,
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
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import {
  useCreateThirteenthMonthRun,
  useThirteenthMonthRun,
  useThirteenthMonthRuns,
  useThirteenthMonthTransition,
} from '@/hooks/useThirteenthMonth'
import { downloadThirteenthMonthPdf } from '@/api/thirteenthMonth'
import { toApiError } from '@/api/client'
import { peso } from '@/lib/format'
import type { PayrollRunStatus } from '@/types/payroll'

const badgeVariant: Record<PayrollRunStatus, 'secondary' | 'default' | 'outline'> = {
  DRAFT: 'secondary',
  APPROVED: 'default',
  LOCKED: 'outline',
}

export function ThirteenthMonthPage() {
  const [year, setYear] = useState(String(new Date().getFullYear()))
  const [openId, setOpenId] = useState<number | null>(null)
  const runs = useThirteenthMonthRuns()
  const detail = useThirteenthMonthRun(openId)
  const create = useCreateThirteenthMonthRun()
  const approve = useThirteenthMonthTransition('approve')
  const lock = useThirteenthMonthTransition('lock')

  function onCreate() {
    create.mutate(Number(year), {
      onSuccess: (run) => {
        toast.success(`Computed 13th month for ${run.year}`)
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
            <FontAwesomeIcon icon={faGift} className="text-muted-foreground" />
            13th Month Pay
          </CardTitle>
          <CardDescription>
            Sum of basic pay earned in a year ÷ 12, computed from saved payslips.
          </CardDescription>
        </div>
        <div className="flex items-end gap-2">
          <Input
            type="number"
            className="w-28"
            value={year}
            onChange={(e) => setYear(e.target.value)}
          />
          <Button onClick={onCreate} disabled={create.isPending}>
            <FontAwesomeIcon icon={faPlus} className="mr-2" />
            {create.isPending ? 'Computing…' : 'Compute'}
          </Button>
        </div>
      </CardHeader>
      <CardContent>
        {runs.data && (
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>#</TableHead>
                <TableHead>Year</TableHead>
                <TableHead>Status</TableHead>
                <TableHead className="text-right">Employees</TableHead>
                <TableHead className="text-right">Total</TableHead>
                <TableHead></TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {runs.data.length === 0 && (
                <TableRow>
                  <TableCell colSpan={6} className="text-muted-foreground text-center">
                    No runs yet — compute one above.
                  </TableCell>
                </TableRow>
              )}
              {runs.data.map((r) => (
                <TableRow key={r.id}>
                  <TableCell className="font-mono">{r.id}</TableCell>
                  <TableCell>{r.year}</TableCell>
                  <TableCell>
                    <Badge variant={badgeVariant[r.status]}>{r.status}</Badge>
                  </TableCell>
                  <TableCell className="text-right">{r.employeeCount}</TableCell>
                  <TableCell className="text-right font-semibold tabular-nums">
                    {peso(r.totalAmount)}
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
              {run?.year} 13th Month
              {run && <Badge variant={badgeVariant[run.status]}>{run.status}</Badge>}
            </DialogTitle>
            <DialogDescription>
              {run?.employeeCount} employees · total {run ? peso(run.totalAmount) : ''}
            </DialogDescription>
          </DialogHeader>

          {run && (
            <>
              <div className="max-h-80 overflow-y-auto">
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>Employee</TableHead>
                      <TableHead className="text-right">Total basic</TableHead>
                      <TableHead className="text-right">13th month</TableHead>
                      <TableHead></TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {run.entries?.map((e) => (
                      <TableRow key={e.id}>
                        <TableCell className="font-medium">{e.employeeName}</TableCell>
                        <TableCell className="text-right tabular-nums">
                          {peso(e.totalBasic)}
                        </TableCell>
                        <TableCell className="text-right font-semibold tabular-nums">
                          {peso(e.amount)}
                        </TableCell>
                        <TableCell className="text-right">
                          <Button
                            variant="ghost"
                            size="sm"
                            onClick={() =>
                              downloadThirteenthMonthPdf(e.id).catch((err) =>
                                toast.error(toApiError(err).message),
                              )
                            }
                          >
                            <FontAwesomeIcon icon={faFilePdf} />
                          </Button>
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
