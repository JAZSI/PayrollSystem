# Payroll Management System: UML Design Document

---

## 1. Class Diagram

```mermaid
classDiagram
    %% --- CONTROLLERS (Presentation / Controller in MVC) ---
    class AdminDashboardController <<Controller>> {
        +initialize()
        +showEmployees()
        +saveEmployee()
        +loadAttendance()
        +computePayroll()
        +handleLogout()
    }

    class KioskTerminalController <<Controller>> {
        +initialize()
        +handleSubmit()
        +handleTimeIn()
        +handleTimeOut()
    }

    class EmployeePortalController <<Controller>> {
        +initialize()
        +handleLogin()
        +handleDownloadPdf()
    }

    class SceneManager <<ViewHelper>>
    class Routes <<enumeration>>

    %% --- SERVICES (Application Layer) ---
    class AuthService <<Service>>
    class EmployeeService <<Service>>
    class AttendanceService <<Service>>
    class PayrollGenerationService <<Service>>
    class SubmissionService <<Service>>
    class DeductionService <<Service>>
    class HolidayService <<Service>>

    %% --- PORTS / INTERFACES ---
    class AccountRepositoryPort <<Port>>
    class EmployeeRepositoryPort <<Port>>
    class AttendanceRepositoryPort <<Port>>
    class SubmissionRepositoryPort <<Port>>

    %% --- INFRASTRUCTURE (Repository implementations) ---
    class EmployeeRepository <<Repository>>
    class EmployeeDao <<Repository>>
    class AttendanceRepository <<Repository>>
    class AttendanceDao <<Repository>>
    class SubmissionRepository <<Repository>>
    class Database <<Infrastructure>>
    class TransactionManager <<Infrastructure>>

    %% --- DOMAIN (Model in MVC) ---
    class Employee <<Entity>> {
        -employeeId
        -name
        -monthlyRate
        -hourlyRate
    }

    class PayrollEntry <<Entity>>
    class AttendanceRecord <<Entity>>
    class LeaveBalance <<ValueObject>>
    class LoanBalance <<ValueObject>>
    class EndUser <<Entity>>

    %% --- RELATIONSHIPS (clear MVC mapping) ---
    %% Controllers -> Services
    AdminDashboardController --> EmployeeService
    AdminDashboardController --> AttendanceService
    AdminDashboardController --> PayrollGenerationService
    KioskTerminalController --> AttendanceService
    EmployeePortalController --> AuthService

    %% Services -> Ports (use interfaces)
    EmployeeService --> EmployeeRepositoryPort
    AttendanceService --> AttendanceRepositoryPort
    SubmissionService --> SubmissionRepositoryPort
    PayrollGenerationService --> AttendanceRepositoryPort

    %% Ports implemented by repositories
    EmployeeRepositoryPort <|.. EmployeeRepository
    EmployeeRepositoryPort <|.. EmployeeDao
    AttendanceRepositoryPort <|.. AttendanceRepository
    AttendanceRepositoryPort <|.. AttendanceDao
    SubmissionRepositoryPort <|.. SubmissionRepository

    %% Domain ownership
    Employee *-- LeaveBalance
    Employee *-- LoanBalance
    PayrollEntry o-- Employee
    SubmissionRepository ..> Database : uses
    Database --> TransactionManager

    %% View helpers
    SceneManager o-- Routes
```

---

## 2. Use Case Diagram

```mermaid
flowchart LR
    %% Actors
    subgraph Actors
        admin["👤 Admin"]
        hr["👥 HR Staff"]
        emp["👤 Employee"]
        kioskUser["🖥️ Kiosk User"]
    end

    %% Kiosk Terminal Boundary
    subgraph KioskTerminal ["Boundary: Kiosk Terminal"]
        ucTimeInOut(["Time In / Out"])
        ucClockIn(["Clock In"])
        ucClockOut(["Clock Out"])
        ucErrNF(["Show 'Employee Not Found'"])
        ucErrDup(["Show 'Already Timed In'"])
    end

    %% Employee Portal Boundary
    subgraph EmployeePortal ["Boundary: Employee Portal"]
        ucEmpLogin(["Login to Portal"])
        ucViewAtt(["View Attendance History"])
        ucViewPayslip(["View Payslip"])
        ucSubLeave(["Submit Leave Request"])
        ucSubLoanDect(["Request Loan Deduction"])
    end

    %% Admin Dashboard Boundary
    subgraph AdminDashboard ["Boundary: Admin Dashboard"]
        ucAdminLogin(["Login to Dashboard"])
        ucManageEmp(["Manage Employees"])
        ucApproveLeave(["Approve Leave Applications"])
        ucManageLoans(["Manage Outstanding Loans"])
        ucGenPayroll(["Generate & Review Payroll"])
        ucGenReports(["Generate Government Reports"])
        ucExportPR(["Export Payroll (CSV/PDF)"])
    end

    %% Actor to Use Case Associations
    kioskUser --> ucTimeInOut
    
    emp --> ucEmpLogin
    emp --> ucViewAtt
    emp --> ucViewPayslip
    emp --> ucSubLeave
    emp --> ucSubLoanDect

    hr --> ucAdminLogin
    hr --> ucManageEmp
    hr --> ucApproveLeave
    hr --> ucManageLoans
    hr --> ucGenReports

    admin --> ucAdminLogin
    admin --> ucManageEmp
    admin --> ucApproveLeave
    admin --> ucManageLoans
    admin --> ucGenPayroll
    admin --> ucGenReports
    admin --> ucExportPR

    %% Include / Extend relationships
    ucTimeInOut -.->|<<include>>| ucClockIn
    ucTimeInOut -.->|<<include>>| ucClockOut
    ucClockIn -.->|<<extend>>| ucErrNF
    ucClockIn -.->|<<extend>>| ucErrDup
    ucClockOut -.->|<<extend>>| ucErrNF

    ucGenPayroll -.->|<<include>>| ucExportPR
    ucSubLeave -.->|<<include>>| ucEmpLogin
    ucViewPayslip -.->|<<include>>| ucEmpLogin

    %% Styles
    classDef actor fill:#f5f5f5,stroke:#7f8c8d,stroke-width:2px,color:#2c3e50;
    classDef usecase fill:#e8f4f8,stroke:#3498db,stroke-width:1.5px,color:#2c3e50;
    
    class admin,hr,emp,kioskUser actor;
    class ucTimeInOut,ucClockIn,ucClockOut,ucErrNF,ucErrDup,ucEmpLogin,ucViewAtt,ucViewPayslip,ucSubLeave,ucSubLoanDect,ucAdminLogin,ucManageEmp,ucApproveLeave,ucManageLoans,ucGenPayroll,ucGenReports,ucExportPR usecase;
```

---

## 3. MVC / Architecture Diagram

```mermaid
flowchart LR
    %% Explicit MVC diagram: View <-> Controller -> Services -> Model -> Repository
    subgraph VIEW ["View (FXML / UI)"]
        V1["FXML Views\n(AdminDashboard.fxml, EmployeePortal.fxml, KioskTerminal.fxml)"]
        V2["SceneManager & Routes\n(navigation)"]
    end

    subgraph CONTROLLER ["Controller (MVC) "]
        C1["AdminDashboardController\nKioskTerminalController\nEmployeePortalController"]
    end

    subgraph SERVICE ["Application Services"]
        S1["EmployeeService\nAttendanceService\nPayrollGenerationService\nSubmissionService"]
    end

    subgraph MODEL ["Model (Domain) "]
        M1["Entities: Employee, PayrollEntry, AttendanceRecord, EndUser"]
        M2["Value Objects: LeaveBalance, LoanBalance"]
    end

    subgraph REPO ["Repository (Ports & Impl)"]
        P_IF["Repository Ports (interfaces)"]
        R_IMP["JDBC Repos / DAOs\nEmployeeDao, AttendanceDao, SubmissionRepository"]
    end

    subgraph INFRA ["Infrastructure (DB) "]
        DB[("SQLite / JDBC / Database")]
    end

    %% Key interactions
    V1 -- "user actions / UI events" --> C1
    V2 -- "navigation" --> C1
    C1 -- "calls / requests" --> S1
    C1 -- "binds/updates" --> V1
    S1 -- "reads/writes" --> M1
    S1 -- "validates/calculates" --> M2
    S1 -- "uses" --> P_IF
    R_IMP -.-> P_IF
    R_IMP --> DB

    %% Notes for clarity
    %% Note: click links removed to avoid empty href parse issues
```

---

## 4. State Diagrams

### Employee Attendance Lifecycle

```mermaid
stateDiagram-v2
    [*] --> Absent : day_start
    [*] --> Holiday : holiday_detected / mark as holiday

    Absent --> Clocked_In : clock_in [id_valid] / record timeIn
    Absent --> Error_NotFound : clock_in [id_not_found] / show "Employee Not Found"

    Clocked_In --> Clocked_Out : clock_out [valid] / record timeOut
    Clocked_In --> Clocked_In : clock_in [duplicate] / show "Already Timed In" (no-op)
    Clocked_In --> Auto_Clocked_Out : cutoff_reached / set timeOut = cutoff

    Clocked_Out --> Processed : cutoff_close / enqueue payroll calculation
    Auto_Clocked_Out --> Processed : cutoff_close / enqueue payroll calculation (apply assumed-out policy)
    Absent --> Processed : cutoff_close / mark as absent for payroll
    Holiday --> Processed : cutoff_close / apply holiday rules

    Error_NotFound --> Absent : retry_or_cancel
    Processed --> [*]
```

### Payroll Processing Lifecycle

```mermaid
stateDiagram-v2
    [*] --> Draft : create_cutoff
    Draft --> PendingCalculation : collect_time_records
    PendingCalculation --> Generated : compute_payroll / calculate gross, deductions

    Generated --> Reviewed : admin_review_required
    Reviewed --> Draft : changes_requested / edit inputs
    Reviewed --> Approved : approve / lock payroll

    Approved --> Paid : payout_completed / send bank file or mark paid
    Paid --> Archived : publish_payslips / archive_records
    Archived --> [*]
```

### Leave and Submission Lifecycle

```mermaid
stateDiagram-v2
    [*] --> Pending : submit

    Pending --> UnderReview : notify_admin
    UnderReview --> Approved : approve / create transactions
    UnderReview --> Rejected : reject / notify employee

    state Approved {
        [*] --> ApplyingDeductions
        ApplyingDeductions --> DeductionsRecorded : save leave & loan transactions
        DeductionsRecorded --> Applied : update balances
    }

    Applied --> Processed : enqueue_for_payroll
    Rejected --> [*] : end
    Processed --> [*]
```
