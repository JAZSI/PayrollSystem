import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import type { IconDefinition } from '@fortawesome/fontawesome-svg-core'
import {
  faUsers,
  faReceipt,
  faLayerGroup,
  faGaugeHigh,
} from '@fortawesome/free-solid-svg-icons'
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { useDashboard } from '@/hooks/useDashboard'
import { peso } from '@/lib/format'
import type { PayrollRunStatus } from '@/types/payroll'

const badgeVariant: Record<PayrollRunStatus, 'secondary' | 'default' | 'outline'> = {
  DRAFT: 'secondary',
  APPROVED: 'default',
  LOCKED: 'outline',
}

function Kpi({
  icon,
  label,
  value,
  hint,
}: {
  icon: IconDefinition
  label: string
  value: string | number
  hint?: string
}) {
  return (
    <Card>
      <CardContent className="flex items-center gap-4 p-5">
        <div className="bg-muted text-muted-foreground flex size-10 items-center justify-center rounded-md">
          <FontAwesomeIcon icon={icon} />
        </div>
        <div>
          <div className="text-2xl font-semibold tabular-nums">{value}</div>
          <div className="text-muted-foreground text-sm">{label}</div>
          {hint && <div className="text-muted-foreground text-xs">{hint}</div>}
        </div>
      </CardContent>
    </Card>
  )
}

export function DashboardPage() {
  const { data, isLoading } = useDashboard()

  if (isLoading || !data) {
    return <p className="text-muted-foreground text-sm">Loading dashboard…</p>
  }

  const run = data.latestRun

  return (
    <div className="space-y-6">
      <div>
        <h1 className="flex items-center gap-2 text-xl font-semibold">
          <FontAwesomeIcon icon={faGaugeHigh} className="text-muted-foreground" />
          Dashboard
        </h1>
        <p className="text-muted-foreground text-sm">Payroll system overview.</p>
      </div>

      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
        <Kpi
          icon={faUsers}
          label="Active employees"
          value={data.activeEmployees}
          hint={`${data.totalEmployees} total`}
        />
        <Kpi icon={faReceipt} label="Payslips generated" value={data.totalPayslips} />
        <Kpi
          icon={faLayerGroup}
          label="Payroll runs"
          value={data.totalRuns}
          hint={`${data.draftRuns} draft · ${data.approvedRuns} approved · ${data.lockedRuns} locked`}
        />
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Latest payroll run</CardTitle>
          <CardDescription>Most recent batch run.</CardDescription>
        </CardHeader>
        <CardContent>
          {run ? (
            <div className="flex flex-wrap items-center gap-x-8 gap-y-2 text-sm">
              <div>
                <span className="text-muted-foreground">Run </span>#{run.id}
              </div>
              <div>
                <span className="text-muted-foreground">Cut-off </span>
                {run.cutoffPeriod}
              </div>
              <Badge variant={badgeVariant[run.status]}>{run.status}</Badge>
              <div>
                <span className="text-muted-foreground">Employees </span>
                {run.employeeCount}
              </div>
              <div>
                <span className="text-muted-foreground">Gross </span>
                <span className="tabular-nums">{peso(run.totalGross)}</span>
              </div>
              <div className="font-semibold">
                <span className="text-muted-foreground font-normal">Net </span>
                <span className="tabular-nums">{peso(run.totalNet)}</span>
              </div>
            </div>
          ) : (
            <p className="text-muted-foreground text-sm">No payroll runs yet.</p>
          )}
        </CardContent>
      </Card>
    </div>
  )
}
