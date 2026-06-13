import { NavLink, Outlet } from 'react-router-dom'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import {
  faMoneyCheckDollar,
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
} from '@fortawesome/free-solid-svg-icons'
import type { IconDefinition } from '@fortawesome/fontawesome-svg-core'
import { cn } from '@/lib/utils'
import { Button } from '@/components/ui/button'
import { useAuth } from '@/auth/AuthContext'
import { isAdmin, isStaff } from '@/auth/roles'

type NavItem = { to: string; label: string; icon: IconDefinition }

const staffNav: NavItem[] = [
  { to: '/dashboard', label: 'Dashboard', icon: faGaugeHigh },
  { to: '/employees', label: 'Employees', icon: faUsers },
  { to: '/settings', label: 'Settings', icon: faGear },
  { to: '/attendance', label: 'Attendance', icon: faCalendarCheck },
  { to: '/payroll', label: 'Run Payroll', icon: faPlay },
  { to: '/runs', label: 'Payroll Runs', icon: faLayerGroup },
  { to: '/payslips', label: 'Payslips', icon: faReceipt },
  { to: '/leave', label: 'Leave', icon: faCalendarDays },
  { to: '/thirteenth-month', label: '13th Month', icon: faGift },
]

const adminNav: NavItem[] = [
  { to: '/holidays', label: 'Holidays', icon: faCalendarDay },
  { to: '/users', label: 'Users', icon: faUserShield },
]

const employeeNav: NavItem[] = [
  { to: '/employee', label: 'Dashboard', icon: faGaugeHigh },
  { to: '/my-payslips', label: 'My Payslips', icon: faReceipt },
  { to: '/my-leave', label: 'My Leave', icon: faCalendarDays },
]

export function Layout() {
  const { user, logout } = useAuth()
  const items = isStaff(user?.role)
    ? isAdmin(user?.role)
      ? [...staffNav, ...adminNav]
      : staffNav
    : employeeNav

  return (
    <div className="bg-background text-foreground flex min-h-screen">
      <aside className="bg-sidebar text-sidebar-foreground hidden w-60 shrink-0 flex-col border-r p-4 sm:flex">
        <div className="mb-6 flex items-center gap-2 px-2">
          <FontAwesomeIcon icon={faMoneyCheckDollar} className="text-primary text-xl" />
          <span className="text-lg font-semibold">PayrollPal</span>
        </div>
        <nav className="flex flex-1 flex-col gap-1">
          {items.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              className={({ isActive }) =>
                cn(
                  'flex items-center gap-3 rounded-md px-3 py-2 text-sm font-medium transition-colors',
                  isActive
                    ? 'bg-sidebar-accent text-sidebar-accent-foreground'
                    : 'text-muted-foreground hover:bg-sidebar-accent/50',
                )
              }
            >
              <FontAwesomeIcon icon={item.icon} className="w-4" />
              {item.label}
            </NavLink>
          ))}
        </nav>

        <div className="mt-4 border-t pt-4">
          <div className="text-muted-foreground mb-2 flex items-center gap-2 px-2 text-sm">
            <FontAwesomeIcon icon={faUserShield} className="w-4" />
            <span className="truncate">
              {user?.username}
              <span className="ml-1 text-xs">({user?.role})</span>
            </span>
          </div>
          <Button variant="outline" size="sm" className="w-full" onClick={logout}>
            <FontAwesomeIcon icon={faRightFromBracket} className="mr-2" />
            Sign out
          </Button>
        </div>
      </aside>

      <main className="flex-1 overflow-x-auto">
        <div className="mx-auto max-w-5xl px-6 py-8">
          <Outlet />
        </div>
      </main>
    </div>
  )
}
