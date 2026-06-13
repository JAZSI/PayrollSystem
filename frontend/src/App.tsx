import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import { AuthProvider, useAuth } from '@/auth/AuthContext'
import { ProtectedRoute } from '@/auth/ProtectedRoute'
import { StaffRoute } from '@/auth/StaffRoute'
import { AdminRoute } from '@/auth/AdminRoute'
import { isStaff } from '@/auth/roles'
import { Layout } from '@/components/Layout'
import { LoginPage } from '@/pages/LoginPage'
import { KioskPage } from '@/pages/KioskPage'
import { DashboardPage } from '@/pages/DashboardPage'
import { EmployeeDashboardPage } from '@/pages/EmployeeDashboardPage'
import { EmployeesPage } from '@/pages/EmployeesPage'
import { SettingsPage } from '@/pages/SettingsPage'
import { AttendancePage } from '@/pages/AttendancePage'
import { RunPayrollPage } from '@/pages/RunPayrollPage'
import { PayrollRunsPage } from '@/pages/PayrollRunsPage'
import { PayslipsPage } from '@/pages/PayslipsPage'
import { LeavePage } from '@/pages/LeavePage'
import { ThirteenthMonthPage } from '@/pages/ThirteenthMonthPage'
import { MyPayslipsPage } from '@/pages/MyPayslipsPage'
import { MyLeavePage } from '@/pages/MyLeavePage'
import { UsersPage } from '@/pages/UsersPage'
import { HolidaysPage } from '@/pages/HolidaysPage'

function HomeRedirect() {
  const { user } = useAuth()
  return <Navigate to={isStaff(user?.role) ? '/dashboard' : '/employee'} replace />
}

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/kiosk" element={<KioskPage />} />
          <Route element={<ProtectedRoute />}>
            <Route element={<Layout />}>
              <Route index element={<HomeRedirect />} />
              <Route element={<StaffRoute />}>
                <Route path="/dashboard" element={<DashboardPage />} />
                <Route path="/employees" element={<EmployeesPage />} />
                <Route path="/settings" element={<SettingsPage />} />
                <Route path="/attendance" element={<AttendancePage />} />
                <Route path="/payroll" element={<RunPayrollPage />} />
                <Route path="/runs" element={<PayrollRunsPage />} />
                <Route path="/payslips" element={<PayslipsPage />} />
                <Route path="/leave" element={<LeavePage />} />
                <Route path="/thirteenth-month" element={<ThirteenthMonthPage />} />
              </Route>
              <Route element={<AdminRoute />}>
                <Route path="/users" element={<UsersPage />} />
                <Route path="/holidays" element={<HolidaysPage />} />
              </Route>
              <Route path="/employee" element={<EmployeeDashboardPage />} />
              <Route path="/my-payslips" element={<MyPayslipsPage />} />
              <Route path="/my-leave" element={<MyLeavePage />} />
            </Route>
          </Route>
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  )
}

export default App
