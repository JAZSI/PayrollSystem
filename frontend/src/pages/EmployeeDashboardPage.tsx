import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import {
  faIdBadge,
  faReceipt,
  faMoneyBillWave,
  faHandHoldingDollar,
  faGift,
} from '@fortawesome/free-solid-svg-icons'
import { useNavigate } from 'react-router-dom'
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { useMe } from '@/hooks/useMe'
import { useMyPayslips } from '@/hooks/usePayroll'
import { useMyLoans } from '@/hooks/useLoans'
import { useMyThirteenthMonth } from '@/hooks/useThirteenthMonth'
import { peso } from '@/lib/format'

export function EmployeeDashboardPage() {
  const { data: me, isLoading } = useMe()
  const { data: payslips } = useMyPayslips()
  const { data: loans } = useMyLoans()
  const { data: thirteenth } = useMyThirteenthMonth()
  const navigate = useNavigate()

  if (isLoading || !me) {
    return <p className="text-muted-foreground text-sm">Loading…</p>
  }

  const emp = me.employee
  const latest = payslips?.[0]
  const activeLoans = loans?.filter((l) => l.status === 'ACTIVE') ?? []
  const outstanding = activeLoans.reduce((sum, l) => sum + l.balance, 0)
  const latest13th = thirteenth?.[0]

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-xl font-semibold">Welcome, {emp?.fullName ?? me.username}</h1>
        <p className="text-muted-foreground text-sm">Your employee profile and payslips.</p>
      </div>

      <div className="grid gap-4 md:grid-cols-3">
        <Card className="md:col-span-2">
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <FontAwesomeIcon icon={faIdBadge} className="text-muted-foreground" />
              My details
            </CardTitle>
          </CardHeader>
          <CardContent>
            {emp ? (
              <dl className="grid grid-cols-2 gap-x-6 gap-y-2 text-sm">
                <dt className="text-muted-foreground">Employee ID</dt>
                <dd className="font-mono">{emp.id}</dd>
                <dt className="text-muted-foreground">Name</dt>
                <dd>{emp.fullName}</dd>
                <dt className="text-muted-foreground">Type</dt>
                <dd>{emp.typeLabel}</dd>
                <dt className="text-muted-foreground">Rate</dt>
                <dd>
                  {emp.type === 'PART_TIMER'
                    ? `${peso(emp.hourlyRate)} / hr`
                    : `${peso(emp.monthlyRate)} / mo`}
                </dd>
                <dt className="text-muted-foreground">Status</dt>
                <dd>
                  <Badge variant={emp.active ? 'default' : 'secondary'}>
                    {emp.active ? 'Active' : 'Inactive'}
                  </Badge>
                </dd>
              </dl>
            ) : (
              <p className="text-muted-foreground text-sm">
                Your account is not linked to an employee record yet. Contact HR.
              </p>
            )}
          </CardContent>
        </Card>

        <div className="grid gap-4">
          <Card>
            <CardContent className="flex items-center gap-4 p-5">
              <div className="bg-muted text-muted-foreground flex size-10 items-center justify-center rounded-md">
                <FontAwesomeIcon icon={faMoneyBillWave} />
              </div>
              <div>
                <div className="text-xl font-semibold tabular-nums">
                  {latest ? peso(latest.netPay) : '—'}
                </div>
                <div className="text-muted-foreground text-sm">Latest net pay</div>
                {latest && (
                  <div className="text-muted-foreground text-xs">{latest.cutoffPeriod}</div>
                )}
              </div>
            </CardContent>
          </Card>
          <Card>
            <CardContent className="flex items-center gap-4 p-5">
              <div className="bg-muted text-muted-foreground flex size-10 items-center justify-center rounded-md">
                <FontAwesomeIcon icon={faReceipt} />
              </div>
              <div>
                <div className="text-xl font-semibold tabular-nums">
                  {payslips?.length ?? 0}
                </div>
                <div className="text-muted-foreground text-sm">Payslips</div>
              </div>
            </CardContent>
          </Card>
          <Card>
            <CardContent className="flex items-center gap-4 p-5">
              <div className="bg-muted text-muted-foreground flex size-10 items-center justify-center rounded-md">
                <FontAwesomeIcon icon={faHandHoldingDollar} />
              </div>
              <div>
                <div className="text-xl font-semibold tabular-nums">{peso(outstanding)}</div>
                <div className="text-muted-foreground text-sm">Outstanding loans</div>
                {activeLoans.length > 0 && (
                  <div className="text-muted-foreground text-xs">
                    {activeLoans.length} active
                  </div>
                )}
              </div>
            </CardContent>
          </Card>
          {latest13th && (
            <Card>
              <CardContent className="flex items-center gap-4 p-5">
                <div className="bg-muted text-muted-foreground flex size-10 items-center justify-center rounded-md">
                  <FontAwesomeIcon icon={faGift} />
                </div>
                <div>
                  <div className="text-xl font-semibold tabular-nums">{peso(latest13th.amount)}</div>
                  <div className="text-muted-foreground text-sm">13th month pay</div>
                  <div className="text-muted-foreground text-xs">{latest13th.year}</div>
                </div>
              </CardContent>
            </Card>
          )}
        </div>
      </div>

      <Card>
        <CardHeader className="flex flex-row items-center justify-between">
          <div>
            <CardTitle>Recent payslips</CardTitle>
            <CardDescription>Your most recent payroll history.</CardDescription>
          </div>
          <Button variant="outline" size="sm" onClick={() => navigate('/my-payslips')}>
            View all
          </Button>
        </CardHeader>
        <CardContent>
          {payslips && payslips.length > 0 ? (
            <ul className="divide-y text-sm">
              {payslips.slice(0, 5).map((p) => (
                <li key={p.id} className="flex items-center justify-between py-2">
                  <span>
                    <span className="text-muted-foreground">#{p.id} · </span>
                    {p.cutoffPeriod}
                  </span>
                  <span className="font-semibold tabular-nums">{peso(p.netPay)}</span>
                </li>
              ))}
            </ul>
          ) : (
            <p className="text-muted-foreground text-sm">No payslips yet.</p>
          )}
        </CardContent>
      </Card>
    </div>
  )
}
