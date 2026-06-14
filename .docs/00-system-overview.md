# 00 — System Overview (as built)

A Philippine payroll web app: **Spring Boot REST backend (Java 21, SQLite)** + **React
SPA (Bun + Vite + shadcn/ui)**. Computation rules are grounded in
[formulas.md](formulas.md) / [sources.md](sources.md) / [instructions.md](instructions.md).

## Stack
- Backend: Spring Boot 3.3, Spring Data JPA, Spring Security (JWT), SQLite, OpenPDF, springdoc.
- Frontend: React 19, Vite, TypeScript, TanStack Query, React Router, shadcn/ui, Recharts, FontAwesome.
- Dev: `mvn spring-boot:run` (:8080) · `cd frontend && bun run dev` (:5173, proxies `/api`).
- Prod: one jar serves API + UI on :8080 (see Deployment) — `scripts/build` then `scripts/run`.

## Backend layout (package-by-feature)
Each feature folder holds its own entity/repository/service/controller/`dto`.
```text
com.com253.payrollsystem
├── shared/       Money + domain/ (pure calc: PayrollCalculator, WorkingDayCalculator,
│                 employeetypes/, tax/, model classes), mapping/, error/, security/, config/
├── employee/     EmployeeEntity, repo, service, controller, dto/
├── attendance/   TimeRecord, AttendanceService, KioskService, HolidayCalendar, controllers, dto/
├── payroll/      Payslip + PayrollRun, services, PeriodLockGuard, PayslipPdfService, dto/
├── loan/         Loan + LoanPayment, LoanService, LoanController, dto/
├── payitem/      PayItemEntity (allowances/deductions), PayItemService, controller, dto/
├── report/       ReportService (register/remittance/bank + CSV), ReportController, dto/
├── statutory/    ContributionTable + brackets, provider, DbContributionTables, CRUD, dto/
├── audit/        AuditEntry (append-only), AuditService, AuditController, dto/
├── leave/        LeaveType + LeaveBalance + LeaveRequest, LeaveService, controllers, dto/
├── thirteenthmonth/ ThirteenthMonthRun + Entry, service, controller, PDF, dto/
├── settings/     SettingsEntity, service, controller, dto/
├── holiday/      HolidayEntity, service, controller, dto/
├── user/         UserEntity, AuthService, Auth/User/Me controllers, dto/
└── dashboard/    DashboardService, controller, dto/
```
- `shared/domain` is pure (no Spring/JPA) — reused unchanged by single + batch runs.
- Money rounding lives once in `shared.Money` (centavo HALF_UP).

## Roles
- **ADMIN** — everything, incl. users + holidays.
- **HR** — employees, settings, attendance, payroll, reports.
- **EMPLOYEE** — self-service: own dashboard + payslips. Kiosk is public.

## Implemented features
- Auth (JWT) + role gating; seeded admin.
- Employees (CRUD, edit, optional login account on create).
- Settings (single config row).
- Attendance with auto holiday detection; staff entry **merges** with kiosk punches.
- Public time-clock **kiosk** (clock in/out by id).
- Payroll: single run + **batch run** (DRAFT→APPROVED→LOCKED, lock-enforced); centavo rounding.
- Loans & cash advances: per-cut-off amortization, auto-deducted; posted + decremented on lock.
- Allowances & other deductions: taxable/non-taxable items via a `PayContext`; shown on payslip + PDF.
- Pay rules: night differential (22:00–06:00, +10%); employer SSS/PhilHealth/Pag-IBIG/EC (info only) on the payslip.
- Reports: payroll register, statutory remittance, bank disbursement — preview + CSV export.
- Effective-dated statutory tables: SSS/PhilHealth/Pag-IBIG/BIR editable by date (seeded to match constants).
- Audit log: append-only trail of mutating actions (runs, employees, loans, leave, holidays, auth); admin viewer.
- Leave: types + per-year balances (accrued from defaults); requests with approval; approved paid leave waives absence penalties.
- 13th-month pay: sum of year's basic ÷ 12, run lifecycle + per-entry PDF; employee self-view.
- Payslips: history, detail, **PDF** download.
- Holidays: admin-managed calendar (seeded 2026 defaults).
- Dashboards: admin/HR KPIs, employee self-service.
- Users: admin-managed accounts.

## Core domain rules (see formulas.md)
- Daily rate `monthlyRate / workingDays`; basic pay monthly/2 or hours×rate.
- OT after cut-off start × holiday multiplier (1.25 / 2.60 / 1.69).
- Deductions: SSS table, PhilHealth 5.5%, Pag-IBIG 1–2% cap 100, BIR TRAIN ×24.
- Net = gross − penalties − statutory − loan. All money rounded 2dp HALF_UP.

## Deployment
- The React build is bundled into the jar (`mvn package` copies `frontend/dist` to
  `/static`); a small SPA-forward controller serves client routes. One artifact, one port.
- Scripts: `scripts/build.{bat,sh}` (frontend + jar), `scripts/run.{bat,sh}` (java -jar),
  `scripts/package.{bat,sh}` (jpackage portable app + bundled runtime; installer note inside).
- Schema is managed by Hibernate `ddl-auto=update`. A Flyway baseline
  (`src/main/resources/db/migration/V1__init.sql`) is shipped; enable Flyway by uncommenting
  the deps in `pom.xml` and switching `ddl-auto` to `validate` once online.

## Notes
- Cut-offs are month-agnostic (`1st-15th` / `16th-30th`); period-scoped queries (leave
  coverage, reports) bucket by day-of-month.
- Statutory EC and the Pag-IBIG employer rate stay rule-based; everything else is table-driven.
