import { NavLink, Outlet, useLocation } from 'react-router-dom'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import {
  faUsers,
  faGear,
  faCalendarCheck,
  faPlay,
  faLayerGroup,
  faReceipt,
  faRightFromBracket,
  faUserShield,
  faGaugeHigh,
  faCalendarDay,
  faCalendarDays,
  faGift,
  faChartColumn,
  faScaleBalanced,
  faClockRotateLeft,
} from '@fortawesome/free-solid-svg-icons'
import type { IconDefinition } from '@fortawesome/fontawesome-svg-core'
import { cn } from '@/lib/utils'
import nuLogo from '@/assets/NU.png'
import { Button } from '@/components/ui/button'
import { useAuth } from '@/auth/AuthContext'
import { isAdmin, isStaff } from '@/auth/roles'
import type { Role } from '@/types/auth'

type NavItem = { to: string; label: string; icon: IconDefinition }
type NavGroup = { label: string; items: NavItem[] }

const staffGroups: NavGroup[] = [
  { label: 'Overview', items: [{ to: '/dashboard', label: 'Dashboard', icon: faGaugeHigh }] },
  {
    label: 'People',
    items: [
      { to: '/employees', label: 'Employees', icon: faUsers },
      { to: '/attendance', label: 'Attendance', icon: faCalendarCheck },
      { to: '/leave', label: 'Leave', icon: faCalendarDays },
    ],
  },
  {
    label: 'Payroll',
    items: [
      { to: '/payroll', label: 'Run Payroll', icon: faPlay },
      { to: '/runs', label: 'Payroll Runs', icon: faLayerGroup },
      { to: '/payslips', label: 'Payslips', icon: faReceipt },
      { to: '/thirteenth-month', label: '13th Month', icon: faGift },
    ],
  },
  {
    label: 'Insights',
    items: [
      { to: '/reports', label: 'Reports', icon: faChartColumn },
      { to: '/settings', label: 'Settings', icon: faGear },
    ],
  },
]

const adminGroup: NavGroup = {
  label: 'Administration',
  items: [
    { to: '/holidays', label: 'Holidays', icon: faCalendarDay },
    { to: '/statutory-tables', label: 'Statutory Tables', icon: faScaleBalanced },
    { to: '/audit', label: 'Audit Log', icon: faClockRotateLeft },
    { to: '/users', label: 'Users', icon: faUserShield },
  ],
}

const employeeGroups: NavGroup[] = [
  {
    label: 'Self-service',
    items: [
      { to: '/employee', label: 'Dashboard', icon: faGaugeHigh },
      { to: '/my-payslips', label: 'My Payslips', icon: faReceipt },
      { to: '/my-leave', label: 'My Leave', icon: faCalendarDays },
    ],
  },
]

function groupsFor(role: Role | undefined): NavGroup[] {
  if (!isStaff(role)) return employeeGroups
  return isAdmin(role) ? [...staffGroups, adminGroup] : staffGroups
}

function titleFor(groups: NavGroup[], path: string): string {
  for (const g of groups) {
    const hit = g.items.find((i) => i.to === path)
    if (hit) return hit.label
  }
  return 'PayrollPal'
}

export function Layout() {
  const { user, logout } = useAuth()
  const { pathname } = useLocation()
  const groups = groupsFor(user?.role)
  const title = titleFor(groups, pathname)

  return (
    <div className="bg-background text-foreground flex h-screen overflow-hidden">
      <aside className="sidebar-gradient border-sidebar-border hidden h-screen shrink-0 flex-col border-r md:flex md:w-16 lg:w-60">
        <div className="flex h-14 items-center gap-2.5 px-3 lg:px-4">
          <img src={nuLogo} alt="National University" className="size-8 shrink-0 object-contain" />
          <div className="hidden leading-tight lg:block">
            <div className="text-gold-gradient text-sm font-semibold">PayrollPal</div>
            <div className="text-muted-foreground text-[10px] tracking-wide uppercase">
              Payroll Suite
            </div>
          </div>
        </div>

        <nav className="flex flex-1 flex-col gap-4 overflow-y-auto px-2 py-3 lg:px-3">
          {groups.map((group) => (
            <div key={group.label} className="flex flex-col gap-0.5">
              <div className="text-muted-foreground/60 hidden px-2 pb-1 text-[10px] font-semibold tracking-wider uppercase lg:block">
                {group.label}
              </div>
              {group.items.map((item) => (
                <NavLink
                  key={item.to}
                  to={item.to}
                  title={item.label}
                  className={({ isActive }) =>
                    cn(
                      'relative flex items-center gap-3 rounded-md px-2.5 py-2 text-sm lg:py-1.5',
                      'justify-center lg:justify-start',
                      isActive
                        ? 'bg-sidebar-accent text-sidebar-accent-foreground font-medium'
                        : 'text-sidebar-foreground/70 hover:bg-white/5 hover:text-sidebar-foreground',
                    )
                  }
                >
                  {({ isActive }) => (
                    <>
                      {isActive && (
                        <span className="bg-primary absolute top-1.5 bottom-1.5 left-0 w-0.5 rounded-full" />
                      )}
                      <FontAwesomeIcon icon={item.icon} className="w-4 shrink-0" />
                      <span className="hidden lg:inline">{item.label}</span>
                    </>
                  )}
                </NavLink>
              ))}
            </div>
          ))}
        </nav>

        <div className="border-sidebar-border border-t p-2 lg:p-3">
          <div className="mb-2 hidden items-center gap-2 px-1 lg:flex">
            <div className="bg-muted text-muted-foreground flex size-7 items-center justify-center rounded-full text-xs">
              <FontAwesomeIcon icon={faUserShield} />
            </div>
            <div className="min-w-0 leading-tight">
              <div className="truncate text-xs font-medium">{user?.username}</div>
              <div className="text-muted-foreground text-[10px] tracking-wide uppercase">
                {user?.role}
              </div>
            </div>
          </div>
          <Button variant="outline" size="sm" className="w-full" onClick={logout} title="Sign out">
            <FontAwesomeIcon icon={faRightFromBracket} className="lg:mr-2" />
            <span className="hidden lg:inline">Sign out</span>
          </Button>
        </div>
      </aside>

      <div className="flex min-w-0 flex-1 flex-col">
        <header className="border-border/60 bg-background/70 flex h-14 shrink-0 items-center justify-between border-b px-5 backdrop-blur">
          <div className="flex items-center gap-2.5">
            <img
              src={nuLogo}
              alt="National University"
              className="size-7 shrink-0 object-contain md:hidden"
            />
            <span className="gold-gradient hidden h-4 w-1 rounded-full md:block" />
            <h1 className="text-sm font-semibold">{title}</h1>
          </div>
          <div className="text-muted-foreground hidden items-center gap-2 text-xs sm:flex">
            <span className="size-1.5 rounded-full bg-emerald-400" />
            <span className="text-foreground font-medium">{user?.username}</span>
            <span className="text-muted-foreground/60">· {user?.role}</span>
          </div>
        </header>

        <main className="grid-texture flex-1 overflow-y-auto">
          <div className="px-5 py-5 2xl:px-8">
            <Outlet />
          </div>
        </main>
      </div>
    </div>
  )
}
