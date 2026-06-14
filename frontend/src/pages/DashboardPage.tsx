import { useMemo } from 'react'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import type { IconDefinition } from '@fortawesome/fontawesome-svg-core'
import {
  faUsers,
  faReceipt,
  faLayerGroup,
  faSackDollar,
  faArrowTrendUp,
} from '@fortawesome/free-solid-svg-icons'
import {
  ResponsiveContainer,
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  PieChart,
  Pie,
  Cell,
  AreaChart,
  Area,
} from 'recharts'
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import { Badge } from '@/components/ui/badge'
import { Panel } from '@/components/Panel'
import { useDashboard } from '@/hooks/useDashboard'
import { usePayrollRuns, usePayslips } from '@/hooks/usePayroll'
import { useAudit } from '@/hooks/useAudit'
import { useAuth } from '@/auth/AuthContext'
import { isAdmin } from '@/auth/roles'
import { peso } from '@/lib/format'
import type { PayrollRunStatus } from '@/types/payroll'

const GOLD = 'var(--chart-1)'
const BLUE = 'var(--chart-2)'
const VIOLET = 'var(--chart-4)'

const badgeVariant: Record<PayrollRunStatus, 'secondary' | 'default' | 'outline'> = {
  DRAFT: 'secondary',
  APPROVED: 'default',
  LOCKED: 'outline',
}

const tooltipStyle = {
  background: 'var(--popover)',
  border: '1px solid var(--border)',
  borderRadius: 6,
  fontSize: 12,
  color: 'var(--popover-foreground)',
}

const compact = (n: number) => (Math.abs(n) >= 1000 ? `${(n / 1000).toFixed(0)}k` : String(n))

const ACTION_TONE: Record<string, string> = {
  LOCK: 'text-[var(--chart-1)] bg-[var(--chart-1)]/12',
  APPROVE: 'text-[var(--chart-2)] bg-[var(--chart-2)]/12',
  RUN: 'text-[var(--chart-3)] bg-[var(--chart-3)]/12',
  LOGIN: 'text-[var(--chart-3)] bg-[var(--chart-3)]/12',
  CREATE: 'text-emerald-400 bg-emerald-400/12',
  CANCEL: 'text-destructive bg-destructive/12',
  DEACTIVATE: 'text-destructive bg-destructive/12',
  REJECT: 'text-destructive bg-destructive/12',
  UPDATE: 'text-[var(--chart-4)] bg-[var(--chart-4)]/12',
}

function relativeTime(iso: string | null): string {
  if (!iso) return ''
  const then = new Date(iso).getTime()
  if (Number.isNaN(then)) return ''
  const mins = Math.max(0, Math.round((Date.now() - then) / 60000))
  if (mins < 1) return 'just now'
  if (mins < 60) return `${mins}m ago`
  const hrs = Math.round(mins / 60)
  if (hrs < 24) return `${hrs}h ago`
  return `${Math.round(hrs / 24)}d ago`
}

export function DashboardPage() {
  const { user } = useAuth()
  const admin = isAdmin(user?.role)
  const { data, isLoading } = useDashboard()
  const { data: runs } = usePayrollRuns()
  const { data: payslips } = usePayslips()
  const { data: audit } = useAudit(undefined, 0)

  const runsData = runs ?? []
  const chrono = [...runsData].slice(-8)
  const chartData = chrono.map((r) => ({
    name: `#${r.id}`,
    Net: r.totalNet,
    Deductions: r.totalDeductions,
  }))
  const statusData = [
    { name: 'Draft', value: data?.draftRuns ?? 0, color: VIOLET },
    { name: 'Approved', value: data?.approvedRuns ?? 0, color: BLUE },
    { name: 'Locked', value: data?.lockedRuns ?? 0, color: GOLD },
  ].filter((s) => s.value > 0)

  const netDisbursed = chrono.reduce((s, r) => s + r.totalNet, 0)
  const avgPerRun = chrono.length ? netDisbursed / chrono.length : 0
  const recentRuns = [...runsData].reverse().slice(0, 6)

  const topEarners = useMemo(() => {
    const byEmp = new Map<string, { name: string; type: string; net: number }>()
    for (const p of payslips ?? []) {
      const cur = byEmp.get(p.employeeId)
      if (!cur || p.netPay > cur.net) {
        byEmp.set(p.employeeId, { name: p.employeeName, type: p.employeeTypeLabel, net: p.netPay })
      }
    }
    return [...byEmp.values()].sort((a, b) => b.net - a.net).slice(0, 6)
  }, [payslips])
  const maxEarner = Math.max(1, ...topEarners.map((e) => e.net))

  const activity = (audit?.content ?? []).slice(0, 6)

  if (isLoading) {
    return <p className="text-muted-foreground text-sm">Loading dashboard…</p>
  }

  return (
    <div className="space-y-3">
      <div>
        <h2 className="text-lg font-semibold tracking-tight">Command Center</h2>
        <p className="text-muted-foreground text-xs">Live payroll overview</p>
      </div>

      <div className="grid grid-cols-1 gap-3 lg:grid-cols-12">
        {/* Hero metric */}
        <div className="panel-gradient ring-border/70 relative overflow-hidden rounded-lg p-5 ring-1 lg:col-span-5">
          <span className="gold-gradient absolute top-0 bottom-0 left-0 w-1" />
          <div className="text-muted-foreground text-[11px] font-semibold tracking-wider uppercase">
            Net disbursed · recent runs
          </div>
          <div className="text-gold-gradient mt-1 text-4xl font-bold tracking-tight tabular-nums">
            {peso(netDisbursed)}
          </div>
          <div className="text-muted-foreground mt-1 flex items-center gap-1.5 text-xs">
            <FontAwesomeIcon icon={faArrowTrendUp} className="text-emerald-400" />
            Across {chrono.length} cut-offs · avg {peso(avgPerRun)}/run
          </div>
          <div className="mt-3 -mx-1 h-[90px]">
            {chartData.length > 0 ? (
              <ResponsiveContainer width="100%" height="100%">
                <AreaChart data={chartData}>
                  <defs>
                    <linearGradient id="heroFill" x1="0" y1="0" x2="0" y2="1">
                      <stop offset="0%" stopColor={GOLD} stopOpacity={0.45} />
                      <stop offset="100%" stopColor={GOLD} stopOpacity={0.02} />
                    </linearGradient>
                  </defs>
                  <Tooltip contentStyle={tooltipStyle} formatter={(value) => peso(Number(value))} />
                  <Area type="monotone" dataKey="Net" stroke={GOLD} strokeWidth={2} fill="url(#heroFill)" isAnimationActive={false} />
                </AreaChart>
              </ResponsiveContainer>
            ) : (
              <div className="text-muted-foreground flex h-full items-center text-xs">No runs yet.</div>
            )}
          </div>
          <div className="border-border/60 mt-3 grid grid-cols-3 gap-2 border-t pt-3">
            <MiniStat label="Runs" value={data?.totalRuns ?? 0} />
            <MiniStat label="Active staff" value={data?.activeEmployees ?? 0} />
            <MiniStat label="Payslips" value={data?.totalPayslips ?? 0} />
          </div>
        </div>

        {/* KPI cluster */}
        <div className="grid grid-cols-2 gap-3 lg:col-span-7">
          <Kpi icon={faUsers} label="Active employees" value={data?.activeEmployees ?? 0} hint={`${data?.totalEmployees ?? 0} on file`} />
          <Kpi icon={faReceipt} label="Payslips generated" value={data?.totalPayslips ?? 0} hint="all time" />
          <Kpi icon={faLayerGroup} label="Payroll runs" value={data?.totalRuns ?? 0} hint={`${data?.lockedRuns ?? 0} locked`} />
          <Kpi icon={faSackDollar} label="Latest net" value={data?.latestRun ? peso(data.latestRun.totalNet) : '—'} hint={data?.latestRun?.cutoffPeriod ?? '—'} />
        </div>

        {/* Bars */}
        <Panel title="Net vs. deductions" description="Recent payroll runs" className="lg:col-span-7">
          {chartData.length > 0 ? (
            <ResponsiveContainer width="100%" height={230}>
              <BarChart data={chartData} barSize={20}>
                <CartesianGrid strokeDasharray="3 3" stroke="var(--border)" vertical={false} />
                <XAxis dataKey="name" stroke="var(--muted-foreground)" fontSize={11} tickLine={false} axisLine={false} />
                <YAxis stroke="var(--muted-foreground)" fontSize={11} tickLine={false} axisLine={false} tickFormatter={compact} width={34} />
                <Tooltip contentStyle={tooltipStyle} cursor={{ fill: 'var(--muted)', opacity: 0.25 }} formatter={(value) => peso(Number(value))} />
                <Bar dataKey="Net" stackId="a" fill={GOLD} isAnimationActive={false} />
                <Bar dataKey="Deductions" stackId="a" fill={BLUE} isAnimationActive={false} radius={[2, 2, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          ) : (
            <Empty />
          )}
        </Panel>

        {/* Donut */}
        <Panel title="Run status" description="Lifecycle mix" className="lg:col-span-5">
          {statusData.length > 0 ? (
            <div className="flex items-center gap-2">
              <ResponsiveContainer width="60%" height={200}>
                <PieChart>
                  <Pie data={statusData} dataKey="value" nameKey="name" innerRadius={52} outerRadius={82} paddingAngle={2} stroke="var(--card)" isAnimationActive={false}>
                    {statusData.map((s) => (
                      <Cell key={s.name} fill={s.color} />
                    ))}
                  </Pie>
                  <Tooltip contentStyle={tooltipStyle} />
                </PieChart>
              </ResponsiveContainer>
              <div className="flex flex-col gap-2 text-sm">
                {statusData.map((s) => (
                  <div key={s.name} className="flex items-center gap-2">
                    <span className="size-2.5 rounded-sm" style={{ background: s.color }} />
                    <span className="text-muted-foreground w-20">{s.name}</span>
                    <span className="font-semibold tabular-nums">{s.value}</span>
                  </div>
                ))}
              </div>
            </div>
          ) : (
            <Empty />
          )}
        </Panel>

        {/* Recent runs table */}
        <Panel title="Recent payroll runs" description="Latest batches" className="lg:col-span-7" bodyClassName="p-0">
          {recentRuns.length > 0 ? (
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead className="pl-4">#</TableHead>
                  <TableHead>Cut-off</TableHead>
                  <TableHead>Status</TableHead>
                  <TableHead className="text-right">Staff</TableHead>
                  <TableHead className="text-right">Gross</TableHead>
                  <TableHead className="pr-4 text-right">Net</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {recentRuns.map((r) => (
                  <TableRow key={r.id}>
                    <TableCell className="pl-4 font-mono text-xs">{r.id}</TableCell>
                    <TableCell>{r.cutoffPeriod}</TableCell>
                    <TableCell>
                      <Badge variant={badgeVariant[r.status]}>{r.status}</Badge>
                    </TableCell>
                    <TableCell className="text-right tabular-nums">{r.employeeCount}</TableCell>
                    <TableCell className="text-right tabular-nums">{peso(r.totalGross)}</TableCell>
                    <TableCell className="pr-4 text-right font-semibold tabular-nums">{peso(r.totalNet)}</TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          ) : (
            <div className="p-4">
              <Empty />
            </div>
          )}
        </Panel>

        {/* Top earners */}
        <Panel title="Top earners" description="By highest net pay" className="lg:col-span-5">
          {topEarners.length > 0 ? (
            <div className="flex flex-col gap-2.5">
              {topEarners.map((e, i) => (
                <div key={e.name + i} className="flex items-center gap-3">
                  <span className="text-muted-foreground w-4 text-right text-xs tabular-nums">{i + 1}</span>
                  <div className="min-w-0 flex-1">
                    <div className="flex items-center justify-between gap-2">
                      <span className="truncate text-sm font-medium">{e.name}</span>
                      <span className="text-sm tabular-nums">{peso(e.net)}</span>
                    </div>
                    <div className="bg-muted mt-1 h-1.5 overflow-hidden rounded-full">
                      <span className="gold-gradient block h-full rounded-full" style={{ width: `${(e.net / maxEarner) * 100}%` }} />
                    </div>
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <Empty />
          )}
        </Panel>

        {/* Activity feed (admin only — audit is ADMIN-scoped) */}
        {admin && (
          <Panel title="Recent activity" description="Audit trail" className="lg:col-span-12">
            {activity.length > 0 ? (
              <div className="grid gap-x-6 gap-y-2.5 md:grid-cols-2">
                {activity.map((a) => (
                  <div key={a.id} className="flex items-center gap-3 text-sm">
                    <span className={`rounded px-1.5 py-0.5 text-[10px] font-semibold tracking-wide ${ACTION_TONE[a.action] ?? 'bg-muted text-muted-foreground'}`}>
                      {a.action}
                    </span>
                    <span className="min-w-0 flex-1 truncate">{a.summary}</span>
                    <span className="text-muted-foreground hidden text-xs sm:inline">{a.username}</span>
                    <span className="text-muted-foreground/70 w-20 text-right text-xs">{relativeTime(a.createdAt)}</span>
                  </div>
                ))}
              </div>
            ) : (
              <p className="text-muted-foreground text-sm">No recent activity.</p>
            )}
          </Panel>
        )}
      </div>
    </div>
  )
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
    <div className="bg-card ring-border/70 flex items-center gap-3 rounded-lg p-4 ring-1">
      <div className="bg-primary/12 text-primary flex size-9 items-center justify-center rounded-md">
        <FontAwesomeIcon icon={icon} />
      </div>
      <div className="min-w-0">
        <div className="text-xl leading-tight font-semibold tabular-nums">{value}</div>
        <div className="text-muted-foreground text-xs">{label}</div>
        {hint && <div className="text-muted-foreground/60 text-[11px]">{hint}</div>}
      </div>
    </div>
  )
}

function MiniStat({ label, value }: { label: string; value: string | number }) {
  return (
    <div>
      <div className="text-base font-semibold tabular-nums">{value}</div>
      <div className="text-muted-foreground text-[10px] tracking-wide uppercase">{label}</div>
    </div>
  )
}

function Empty() {
  return (
    <div className="text-muted-foreground flex h-[200px] items-center justify-center text-sm">
      No data yet.
    </div>
  )
}
