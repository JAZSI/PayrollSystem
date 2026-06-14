<h1 align="center">PayrollPal</h1>

<p align="center">
  A full-featured Philippine payroll system — pure-Java computation engine, secured REST API, and a sharp single-page dashboard, shipped as one self-contained jar.
</p>

<p align="center">
  <img alt="Java" src="https://img.shields.io/badge/Java-21-007396?logo=openjdk&logoColor=white">
  <img alt="Spring Boot" src="https://img.shields.io/badge/Spring%20Boot-3.3.5-6DB33F?logo=springboot&logoColor=white">
  <img alt="React" src="https://img.shields.io/badge/React-19-61DAFB?logo=react&logoColor=white">
  <img alt="Vite" src="https://img.shields.io/badge/Vite-8-646CFF?logo=vite&logoColor=white">
  <img alt="Tailwind CSS" src="https://img.shields.io/badge/Tailwind-4-06B6D4?logo=tailwindcss&logoColor=white">
  <img alt="SQLite" src="https://img.shields.io/badge/SQLite-embedded-003B57?logo=sqlite&logoColor=white">
  <img alt="Bun" src="https://img.shields.io/badge/Bun-1.3+-000000?logo=bun&logoColor=white">
</p>

---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Screenshots](#screenshots)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Installation](#installation)
- [Quick Start](#quick-start)
- [Configuration](#configuration)
- [Usage](#usage)
- [API Documentation](#api-documentation)
- [Payroll Run Lifecycle & Locking](#payroll-run-lifecycle--locking)
- [Development](#development)
- [Testing](#testing)
- [Deployment](#deployment)
- [Security](#security)
- [Troubleshooting](#troubleshooting)
- [FAQ](#faq)
- [Roadmap](#roadmap)
- [Contributing](#contributing)
- [License](#license)
- [Acknowledgements](#acknowledgements)

---

## Overview

**PayrollPal** is a payroll application built for Philippine pay rules. It pairs a **pure,
dependency-free Java computation engine** (the original CLI calculator, preserved and
test-pinned) with a **Spring Boot REST API** and a **React single-page UI**.

- **What it does** — manages employees, attendance, loans, leave, allowances, and statutory
  contributions, then computes and locks payslips per cut-off, with reports, PDFs, and an
  audit trail.
- **Why it exists** — to turn a single-file payroll calculator into a real, multi-user,
  auditable system without changing the proven money math.
- **Target users** — small/medium PH employers: HR/payroll staff (ADMIN/HR) and employees
  (self-service).

Computation rules are grounded in [`.docs/formulas.md`](.docs/formulas.md) and
[`.docs/sources.md`](.docs/sources.md); the as-built architecture lives in
[`.docs/00-system-overview.md`](.docs/00-system-overview.md).

---

## Features

**Core payroll**

- ✅ Pure payroll engine (basic, OT, undertime/absence penalties, night differential) — reused unchanged across single + batch runs
- ✅ Batch payroll runs with a **DRAFT → APPROVED → LOCKED** lifecycle and period locking
- ✅ Centavo-precise money (`BigDecimal`, HALF_UP), centralized in one helper
- ✅ PDF payslips and a 13th-month statement (OpenPDF)

**Pay components**

- ✅ Loans & cash advances — per-cut-off amortization, auto-deducted, posted on lock
- ✅ Allowances & other deductions — taxable / non-taxable, via a `PayContext`
- ✅ Night differential (22:00–06:00, +10%) and employer SSS/PhilHealth/Pag-IBIG/EC (info only)
- ✅ Leave — types, per-year accrued balances, approval workflow; approved paid leave waives absence penalties
- ✅ 13th-month pay — sum of a year's basic ÷ 12

**Compliance & data**

- ✅ Effective-dated statutory tables (SSS/PhilHealth/Pag-IBIG/BIR) editable by date, seeded to match built-in constants
- ✅ Auto holiday detection from an admin-managed calendar
- ✅ Append-only audit log of mutating actions
- ✅ Reports: payroll register, statutory remittance, bank disbursement — preview + CSV export

**Platform**

- ✅ JWT auth with role gating (ADMIN / HR / EMPLOYEE)
- ✅ Public time-clock **kiosk** (clock in/out by ID), merged into attendance
- ✅ Advanced dark dashboard with charts (Recharts); employee self-service portal
- ✅ Single-artifact deployment (UI bundled into the jar) + jpackage portable app

---

## Screenshots

### Admin / HR

The advanced dark dashboard — KPI cards, charts, recent runs, top earners, and an activity feed:

![Admin dashboard](.docs/previews/admin/dashboard.png)

<details>
<summary><b>More admin screens</b> — employees, attendance, payroll, reports, statutory, and more</summary>

| Employees | Attendance |
| --- | --- |
| ![Employees](.docs/previews/admin/employees.png) | ![Attendance](.docs/previews/admin/attendance.png) |
| **Run payroll** | **Payroll runs** |
| ![Run payroll](.docs/previews/admin/run_payroll.png) | ![Payroll runs](.docs/previews/admin/payroll_runs.png) |
| **Payslips** | **Reports** |
| ![Payslips](.docs/previews/admin/payslips.png) | ![Reports](.docs/previews/admin/reports.png) |
| **Leave** | **13th-month pay** |
| ![Leave](.docs/previews/admin/leave.png) | ![13th month](.docs/previews/admin/13th_month.png) |
| **Holidays** | **Statutory tables** |
| ![Holidays](.docs/previews/admin/holiday.png) | ![Statutory tables](.docs/previews/admin/satutory_tables.png) |
| **Audit log** | **Users** |
| ![Audit log](.docs/previews/admin/audit_log.png) | ![Users](.docs/previews/admin/users.png) |
| **Settings** | |
| ![Settings](.docs/previews/admin/settings.png) | |

</details>

### Employee self-service

| Dashboard | Payslips | Leave |
| --- | --- | --- |
| ![Employee dashboard](.docs/previews/employee/dashboard.png) | ![Employee payslips](.docs/previews/employee/payslips.png) | ![Employee leave](.docs/previews/employee/leave.png) |

### Kiosk

Public time-clock — clock in/out by employee ID:

![Kiosk](.docs/previews/kiosk/kiosk.png)

---

## Architecture

A pure domain core with no framework dependencies, wrapped by Spring services and a React SPA.

```mermaid
flowchart LR
    UI["React SPA<br/>(TanStack Query + Router)"] -->|"/api + JWT"| API["Spring REST controllers"]
    API --> SVC["Feature services<br/>(payroll, loan, leave, payitem, ...)"]
    SVC --> ENG["Pure engine<br/>PayrollCalculator"]
    SVC --> REPO["Spring Data JPA"]
    REPO --> DB[("SQLite<br/>payroll.db")]
    SVC --> SEC["Spring Security<br/>JWT filter + roles"]
```

The services gather inputs and hand them to the **pure** `PayrollCalculator`:

```mermaid
flowchart TD
    ATT["Attendance (TimeRecords)"] --> ENG
    CTX["PayContext<br/>loans · covered leave · allowances · other deductions"] --> ENG
    TBL["ContributionTables<br/>(effective-dated SSS/PhilHealth/Pag-IBIG/BIR)"] --> ENG
    ENG["PayrollCalculator.buildPayrollEntry"] --> SLIP["Payslip<br/>gross · deductions · net"]
```

- The engine is **pure** (no Spring/JPA) and pinned by characterization tests, so behavior is stable across refactors.
- Statutory rates are resolved by date from the DB (`ContributionTableProvider`), falling back to built-in constants.

---

## Tech Stack

| Category | Technology |
| --- | --- |
| Language / Runtime | Java 21 |
| Backend framework | Spring Boot 3.3.5 (Web, Data JPA, Security, Validation) |
| Auth | JWT (jjwt 0.12), BCrypt |
| Database | SQLite (`sqlite-jdbc`) + Hibernate community dialect |
| PDF | OpenPDF |
| API docs | springdoc-openapi (Swagger UI) |
| Build | Maven |
| Frontend | React 19, TypeScript, Vite 8 |
| UI | Tailwind CSS 4, shadcn/ui (Base UI), FontAwesome, Recharts |
| Data fetching | TanStack Query, Axios, React Router 7 |
| Frontend tooling | Bun |
| Packaging | Single Spring Boot jar · jpackage (portable app) |

---

## Project Structure

```text
PayrollSystem/
├── src/main/java/com/com253/payrollsystem/
│   ├── shared/
│   ├── employee/  attendance/  payroll/ 
│   ├── loan/  leave/  payitem/  thirteenthmonth/
│   ├── statutory/ report/ audit/ holiday/ settings/ user/ dashboard/
│   └── PayrollApplication.java
├── src/main/resources/
│   ├── application.properties
│   └── db/migration/V1__init.sql
├── src/test/java/...
├── frontend/
│   └── src/{api,hooks,pages,components,types,auth,lib}
├── scripts/
└── .docs/
```

Each backend feature folder is self-contained (entity → repository → service → controller →
`dto/`). The pure computation core sits in `shared/domain` and depends on nothing else.

---

## Installation

### Prerequisites

| Tool | Version |
| --- | --- |
| JDK | 21+ |
| Maven | 3.9+ |
| Bun | 1.3+ (frontend only) |

### Clone

```bash
git clone https://github.com/JAZSI/PayrollSystem.git
cd PayrollSystem
```

---

## Quick Start

**Single command (production-style): build the UI + jar, then run.**

```bash
# macOS / Linux
./scripts/build.sh && ./scripts/run.sh
```

```bat
:: Windows
scripts\build.bat && scripts\run.bat
```

Then open **http://localhost:8080** and sign in with the seeded admin:

```text
username: admin
password: admin123    # change after first login
```

> The first run creates `payroll.db` and seeds the admin user, 2026 holidays, default leave
> types, and the 2026 statutory tables.

---

## Configuration

Configured via `src/main/resources/application.properties`; the sensitive values read
environment variables with safe dev defaults.

| Key / Env var | Description | Default |
| --- | --- | --- |
| `APP_JWT_SECRET` | JWT signing secret (≥ 32 bytes) | dev-only placeholder |
| `app.jwt.expiration-ms` | Token lifetime | `86400000` (24h) |
| `app.admin.username` | Seeded admin username | `admin` |
| `app.admin.password` | Seeded admin password | `admin123` |
| `app.cors.allowed-origin` | Allowed browser origin (dev) | `http://localhost:5173` |
| `spring.datasource.url` | SQLite database URL | `jdbc:sqlite:payroll.db` |
| `server.port` | HTTP port | `8080` |

```bash
# Example: production secret + custom port
export APP_JWT_SECRET="a-strong-random-secret-at-least-32-bytes"
java -jar target/PayrollSystem-1.0-SNAPSHOT.jar --server.port=9090
```

---

## Usage

Sign in to get a token, then call the API with `Authorization: Bearer <token>`.

```bash
# 1) Login
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' | jq -r .token)

# 2) Dashboard summary
curl -s http://localhost:8080/api/dashboard -H "Authorization: Bearer $TOKEN"

# 3) Run a batch payroll for a cut-off, then approve & lock it
RUN=$(curl -s -X POST http://localhost:8080/api/payroll/runs \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"period":"1st-15th"}' | jq -r .id)
curl -s -X POST http://localhost:8080/api/payroll/runs/$RUN/approve -H "Authorization: Bearer $TOKEN"
curl -s -X POST http://localhost:8080/api/payroll/runs/$RUN/lock    -H "Authorization: Bearer $TOKEN"

# 4) Export the payroll register as CSV
curl -s "http://localhost:8080/api/reports/export?type=register&period=1st-15th" \
  -H "Authorization: Bearer $TOKEN" -o register.csv
```

### Roles

| Role | Can do |
| --- | --- |
| `ADMIN` | Everything, incl. users, holidays, statutory tables, audit log |
| `HR` | Employees, attendance, payroll, loans, leave, reports |
| `EMPLOYEE` | Self-service: own dashboard, payslips, leave; public kiosk |

---

## API Documentation

Interactive docs (when the app is running):

- **Swagger UI** — http://localhost:8080/swagger-ui.html
- **OpenAPI JSON** — http://localhost:8080/v3/api-docs

| Area | Representative endpoints | Access |
| --- | --- | --- |
| Auth | `POST /api/auth/login`, `POST /api/auth/register` | public / ADMIN |
| Employees | `GET/POST/PUT/DELETE /api/employees` | ADMIN/HR |
| Attendance | `GET/PUT /api/attendance`, `GET /api/attendance/calendar` | ADMIN/HR |
| Kiosk | `POST /api/kiosk/clock` | public |
| Payroll | `POST /api/payroll/run`, `POST /api/payroll/runs`, `.../{id}/approve`, `.../{id}/lock` | ADMIN/HR |
| Payslips | `GET /api/payslips`, `GET /api/payslips/{id}/pdf`, `GET /api/payslips/me` | ADMIN/HR / self |
| Loans | `GET /api/employees/{id}/loans`, `POST /api/loans`, `.../{id}/cancel` | ADMIN/HR |
| Pay items | `GET /api/employees/{id}/pay-items`, `POST /api/pay-items` | ADMIN/HR |
| Leave | `GET /api/leave-requests`, `.../{id}/approve`, `GET /api/me/leave-balances` | ADMIN/HR / self |
| 13th month | `POST /api/thirteenth-month/runs`, `GET .../entries/{id}/pdf` | ADMIN/HR |
| Reports | `GET /api/reports/{register,remittance,bank}`, `/api/reports/export` | ADMIN/HR |
| Statutory | `GET/POST/PUT/DELETE /api/statutory-tables` | ADMIN |
| Audit | `GET /api/audit` | ADMIN |
| Holidays / Settings | `/api/holidays`, `/api/settings` | ADMIN / staff |

---

## Payroll Run Lifecycle & Locking

A batch run is a terminal-locking state machine; locking finalizes the period.

```mermaid
stateDiagram-v2
    [*] --> DRAFT: createRun (computes payslips)
    DRAFT --> APPROVED: approve
    APPROVED --> LOCKED: lock (posts loans, audits, freezes period)
    LOCKED --> [*]
```

- **DRAFT** — recomputable scratch run; payslips generated for all active employees.
- **APPROVED** — reviewed; still reversible (no financial side effects).
- **LOCKED** — terminal: loan balances are decremented (loan payments posted), a `LOCK` audit
  entry is written, and the cut-off is **frozen** — `PeriodLockGuard` then rejects new runs or
  attendance edits for that period. Because LOCKED is terminal, loan posting can't double-run.

---

## Development

Run the two dev servers side by side:

```bash
# Terminal 1 — backend (:8080)
mvn spring-boot:run

# Terminal 2 — frontend (:5173, proxies /api → :8080)
cd frontend
bun install
bun run dev
```

Frontend build / lint:

```bash
cd frontend
bun run build     # type-check (tsc) + production build
bun run lint
```

---

## Testing

```bash
mvn test
```

The suite covers domain math (characterization tests that pin exact payslip outputs),
service logic (loans, leave, pay items, reports, statutory tables, run lifecycle), and a
full-context wiring test against an in-memory SQLite database.

---

## Deployment

### Single artifact (recommended)

`mvn package` bundles the built React app into the jar under `/static`; a small SPA-forward
controller serves client-side routes. One jar runs the API **and** the UI on one port.

```bash
./scripts/build.sh     # frontend + jar
./scripts/run.sh       # java -jar  → http://localhost:8080
```

| Script | Purpose |
| --- | --- |
| `scripts/build.{sh,bat}` | Build the frontend, then the jar (UI bundled) |
| `scripts/run.{sh,bat}` | Run the jar (API + UI on :8080) |
| `scripts/package.{sh,bat}` | jpackage → portable app with a bundled Java runtime |

### Portable app / installer (jpackage)

```bash
./scripts/package.sh   # → dist/PayrollPal (self-contained, no JDK required to run)
```

A native installer (`.msi` / `.deb` / `.dmg`) can be produced by switching `--type`; the
script prints the exact command (Windows installers require the WiX Toolset).

### Database migrations (optional)

The schema is managed by Hibernate `ddl-auto=update`. A Flyway baseline is shipped at
`src/main/resources/db/migration/V1__init.sql`. To adopt Flyway, uncomment the Flyway
dependencies in `pom.xml`, set `spring.flyway.baseline-on-migrate=true`, and switch
`spring.jpa.hibernate.ddl-auto` to `validate`.

---

## Security

- **Stateless JWT** auth; tokens signed with `APP_JWT_SECRET` (override in production).
- **Role-based** authorization via method security (`ADMIN` / `HR` / `EMPLOYEE`); only
  `/api/auth/login`, `/api/kiosk/**`, and Swagger are public.
- Passwords stored with **BCrypt**; the audit log is **append-only** (no update/delete API).
- Change the seeded `admin` password immediately after first login.

> Found a vulnerability? Please open a private security advisory or an issue on the repository.

---

## Troubleshooting

<details>
<summary>Port 8080 is already in use</summary>

Another instance (or app) holds the port. Run on another port:
`java -jar target/PayrollSystem-1.0-SNAPSHOT.jar --server.port=8090`
</details>

<details>
<summary>UnsupportedClassVersionError on startup</summary>

Stale classes compiled by a newer JDK in the IDE. Rebuild clean: `mvn clean package`.
</details>

<details>
<summary>The UI loads but API calls fail in development</summary>

Make sure the backend is running on `:8080` — the Vite dev server proxies `/api` to it.
In production the single jar serves both, so there is no proxy.
</details>

<details>
<summary>"No active employees to run payroll for"</summary>

Add at least one active employee before creating a payroll run.
</details>

<details>
<summary>Can't edit attendance or create a run for a period</summary>

That cut-off has a LOCKED run, which freezes the period. Locking is intentional and terminal.
</details>

---

## FAQ

<details>
<summary>What database does it use?</summary>

Embedded SQLite (`payroll.db`, created on first run). No external database to set up.
</details>

<details>
<summary>How are statutory rates kept current?</summary>

SSS/PhilHealth/Pag-IBIG/BIR live in effective-dated tables editable by an admin. They are
seeded to match the built-in 2026 constants, and payroll picks the version effective for the date.
</details>

<details>
<summary>Do I need Node.js?</summary>

No — the frontend uses Bun. You need JDK 21, Maven, and Bun (for building the UI).
</details>

<details>
<summary>How is the payslip total computed?</summary>

`gross = basic + overtime + night differential + allowances`; deductions are SSS/PhilHealth/
Pag-IBIG/withholding tax + loans + other deductions + penalties; `net = gross − deductions`.
All amounts are rounded to centavo (HALF_UP). See [`.docs/formulas.md`](.docs/formulas.md).
</details>

---

Out of scope (by design): email notifications, employee photos, org/department structure,
self-service password reset.

---

## Contributing

1. **Fork** the repository
2. Create a branch — `git checkout -b feature/your-feature`
3. **Commit** your changes — `git commit -m "Add your feature"`
4. **Push** — `git push origin feature/your-feature`
5. Open a **Pull Request**

Please keep the pure domain (`shared/domain`) free of framework dependencies, and update the
characterization tests in the same commit when an intentional change shifts a payslip number.

---

## License

Released under the [MIT License](LICENSE).

---

## Acknowledgements

Built with [Spring Boot](https://spring.io/projects/spring-boot),
[React](https://react.dev/), [Vite](https://vitejs.dev/),
[Tailwind CSS](https://tailwindcss.com/), [shadcn/ui](https://ui.shadcn.com/),
[TanStack Query](https://tanstack.com/query), [Recharts](https://recharts.org/),
[OpenPDF](https://github.com/LibrePDF/OpenPDF), and [SQLite](https://www.sqlite.org/).

---

<p align="center"><sub>Built with care for accurate, auditable Philippine payroll.</sub></p>
