import { useState } from 'react'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faPlay, faFilePdf } from '@fortawesome/free-solid-svg-icons'
import { toast } from 'sonner'
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { Label } from '@/components/ui/label'
import { Button } from '@/components/ui/button'
import { PayslipView } from '@/components/PayslipView'
import { useEmployees } from '@/hooks/useEmployees'
import { useIsPeriodLocked, useRunPayroll } from '@/hooks/usePayroll'
import { LockedBanner } from '@/components/LockedBanner'
import { downloadPayslipPdf } from '@/api/payroll'
import { toApiError } from '@/api/client'
import { PERIODS } from '@/lib/format'
import type { Payslip } from '@/types/payroll'

export function RunPayrollPage() {
  const { data: employees } = useEmployees()
  const run = useRunPayroll()
  const [employeeId, setEmployeeId] = useState('')
  const [period, setPeriod] = useState<string>(PERIODS[0])
  const [slip, setSlip] = useState<Payslip | null>(null)
  const locked = useIsPeriodLocked(period)

  function onRun() {
    if (!employeeId) {
      toast.error('Select an employee first')
      return
    }
    run.mutate(
      { employeeId, period },
      {
        onSuccess: (result) => {
          setSlip(result)
          toast.success('Payroll computed and saved')
        },
        onError: (err) => toast.error(toApiError(err).message),
      },
    )
  }

  return (
    <div className="space-y-4">
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <FontAwesomeIcon icon={faPlay} className="text-muted-foreground" />
            Run Payroll
          </CardTitle>
          <CardDescription>
            Computes a payslip from the employee's attendance and current settings, then
            saves it. Active loans are deducted automatically.
          </CardDescription>
        </CardHeader>
        <CardContent className="space-y-4">
          {locked && <LockedBanner period={period} />}
          <div className="flex flex-wrap items-end gap-4">
            <div className="grid gap-1.5">
              <Label>Employee</Label>
              <Select value={employeeId} onValueChange={(v) => setEmployeeId(v ?? '')}>
                <SelectTrigger className="w-64">
                  <SelectValue placeholder="Select employee" />
                </SelectTrigger>
                <SelectContent>
                  {employees?.map((e) => (
                    <SelectItem key={e.id} value={e.id}>
                      {e.fullName} ({e.id})
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
            <div className="grid gap-1.5">
              <Label>Cut-off period</Label>
              <Select value={period} onValueChange={(v) => setPeriod(v ?? period)}>
                <SelectTrigger className="w-40">
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
            </div>
            <Button onClick={onRun} disabled={run.isPending || locked}>
              <FontAwesomeIcon icon={faPlay} className="mr-2" />
              {run.isPending ? 'Running…' : 'Run payroll'}
            </Button>
          </div>
        </CardContent>
      </Card>

      {slip && (
        <Card>
          <CardHeader className="flex flex-row items-start justify-between gap-4">
            <div>
              <CardTitle>Payslip</CardTitle>
              <CardDescription>Saved as payslip #{slip.id}.</CardDescription>
            </div>
            <Button
              variant="outline"
              onClick={() =>
                downloadPayslipPdf(slip.id).catch((err) =>
                  toast.error(toApiError(err).message),
                )
              }
            >
              <FontAwesomeIcon icon={faFilePdf} className="mr-2" />
              Download PDF
            </Button>
          </CardHeader>
          <CardContent>
            <PayslipView slip={slip} />
          </CardContent>
        </Card>
      )}
    </div>
  )
}
