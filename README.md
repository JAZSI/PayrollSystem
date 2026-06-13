# Payroll System

A Philippine payroll application: a **Spring Boot REST backend** (Java 21 + SQLite) that
reuses the original payroll computation engine, and a **React frontend** (Bun + Vite +
shadcn/ui + FontAwesome).

Planning and design live in [`.docs/`](.docs) — start with
[`.docs/03-target-architecture.md`](.docs/03-target-architecture.md).

## Prerequisites
- JDK 21 (`C:\Program Files\Java\jdk-21`)
- Maven 3.9+
- Bun 1.3+

## Run the backend (port 8080)
```sh
# from the project root, using JDK 21
mvn spring-boot:run
```
- REST API base: `http://localhost:8080/api`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Data is stored in `payroll.db` (SQLite, created on first run).

Run the domain tests (payroll math characterization):
```sh
mvn test
```

## Run the frontend (port 5173)
```sh
cd frontend
bun install      # first time only
bun run dev
```
Open `http://localhost:5173`. The Vite dev server proxies `/api` → `http://localhost:8080`,
so run the backend alongside it.

## Project layout
```
/                 Spring Boot backend (Maven)
  src/main/java/com/com253/payrollsystem/
    model/ service/        domain: payroll calculator + statutory rules (reused, pure)
    persistence/           JPA entities + repositories (SQLite)
    application/           services
    web/                   REST controllers, DTOs, error handling
  src/test/java/...        domain characterization tests
/frontend         React + Vite + TypeScript SPA (Bun)
/.docs            architecture, domain logic, feature plan, API contract, roadmap
```

## Status
Employee management is implemented end-to-end (CRUD over REST → React table + add form).
Settings, attendance, payroll run, payslips, auth, and reports are planned — see
[`.docs/09-implementation-roadmap-and-risks.md`](.docs/09-implementation-roadmap-and-risks.md).
