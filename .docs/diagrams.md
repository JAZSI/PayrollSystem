# Payroll Management System: UML Design Document

---

## 1. Class Diagram

```mermaid
classDiagram

    %% Controllers
    class AdminDashboardController {
        - employeeService: EmployeeService
        - attendanceService: AttendanceService
        - payrollService: SimplePayrollService
        + initialize(): void
        + saveEmployee(): void
        + loadAttendance(): void
        + computePayroll(): void
    }

    class KioskTerminalController {
        - employeeService: EmployeeService
        - attendanceService: AttendanceService
        + initialize(): void
        + handleSubmit(): void
        + handleTimeIn(): void
        + handleTimeOut(): void
    }

    %% Services
    class EmployeeService {
        - accountRepository: AccountRepositoryPort
        - employeeRepository: EmployeeRepositoryPort
        + registerEmployee(emp: Employee, username: String, password: String): void
        + saveEmployee(emp: Employee): void
        + updateEmployee(emp: Employee): void
        + findEmployee(id: String): Employee
        + getAllEmployees(): List~Employee~
        + deleteEmployee(id: String): void
    }

    class AttendanceService {
        - attendanceRepository: AttendanceRepositoryPort
        + updateTimeIn(employeeId: String, date: LocalDate, timeIn: double): void
        + updateTimeOut(employeeId: String, date: LocalDate, timeOut: double): void
        + upsertAttendance(employeeId: String, date: LocalDate, timeIn: Double, timeOut: Double): void
        + clockIn(employeeId: String, date: LocalDate, timeIn: double): void
        + clockOut(employeeId: String, date: LocalDate, timeOut: double): void
        + getAttendanceHistory(employeeId: String, from: LocalDate, to: LocalDate): List~AttendanceRecord~
    }

    class SimplePayrollService {
        + calculate(employee: Employee, attendance: List~AttendanceRecord~, from: LocalDate, to: LocalDate): SimplePayrollResult
    }

    %% Ports (interfaces)
    class AccountRepositoryPort {
        + save(user: EndUser): void
        + findByUsername(username: String): Optional~EndUser~
        + deleteByEmployeeId(employeeId: String): void
    }

    class EmployeeRepositoryPort {
        + save(emp: Employee): void
        + findById(id: String): Optional~Employee~
        + findAll(): List~Employee~
        + delete(id: String): void
        + updateLeaveBalance(employeeId: String, leaveBalance: LeaveBalance): void
        + updateLoanBalance(employeeId: String, loanBalance: LoanBalance): void
    }

    class AttendanceRepositoryPort {
        + clockIn(employeeId: String, date: LocalDate, timeIn: double): void
        + clockOut(employeeId: String, date: LocalDate, timeOut: double): void
        + getAttendance(employeeId: String, from: LocalDate, to: LocalDate): List~AttendanceRecord~
        + updateTimeIn(employeeId: String, date: LocalDate, timeIn: double): void
        + updateTimeOut(employeeId: String, date: LocalDate, timeOut: double): void
        + deleteByEmployeeAndDate(employeeId: String, date: LocalDate): void
        + upsert(employeeId: String, date: LocalDate, timeIn: Double, timeOut: Double): void
    }

    %% Repository implementations (selected)
    class AccountRepository {
        + save(user: EndUser): void
        + findByUsername(username: String): Optional~EndUser~
        + deleteByEmployeeId(employeeId: String): void
    }

    class EmployeeRepository {
        + save(emp: Employee): void
        + findById(id: String): Optional~Employee~
        + findAll(): List~Employee~
        + delete(id: String): void
        + updateLeaveBalance(employeeId: String, leaveBalance: LeaveBalance): void
        + updateLoanBalance(employeeId: String, loanBalance: LoanBalance): void
    }

    class EmployeeDao {
        + save(emp: Employee): void
        + findById(id: String): Optional~Employee~
        + findAll(): List~Employee~
        + delete(id: String): void
        + updateLeaveBalance(employeeId: String, leaveBalance: LeaveBalance): void
        + updateLoanBalance(employeeId: String, loanBalance: LoanBalance): void
    }

    class AttendanceRepository {
        + clockIn(employeeId: String, date: LocalDate, timeIn: double): void
        + clockOut(employeeId: String, date: LocalDate, timeOut: double): void
        + getAttendance(employeeId: String, from: LocalDate, to: LocalDate): List~AttendanceRecord~
        + updateTimeIn(employeeId: String, date: LocalDate, timeIn: double): void
        + updateTimeOut(employeeId: String, date: LocalDate, timeOut: double): void
        + deleteByEmployeeAndDate(employeeId: String, date: LocalDate): void
        + upsert(employeeId: String, date: LocalDate, timeIn: Double, timeOut: Double): void
    }

    class AttendanceDao {
        + clockIn(employeeId: String, date: LocalDate, timeIn: double): void
        + clockOut(employeeId: String, date: LocalDate, timeOut: double): void
        + getAttendance(employeeId: String, from: LocalDate, to: LocalDate): List~AttendanceRecord~
        + updateTimeIn(employeeId: String, date: LocalDate, timeIn: double): void
        + updateTimeOut(employeeId: String, date: LocalDate, timeOut: double): void
        + deleteByEmployeeAndDate(employeeId: String, date: LocalDate): void
        + upsert(employeeId: String, date: LocalDate, timeIn: Double, timeOut: Double): void
    }

    %% Domain models (selected)
    class Employee {
        - employeeId: String
        - name: String
        - type: EmployeeType
        - monthlyRate: double
        - hourlyRate: double
        - hasLeave: boolean
        - leaveBalance: LeaveBalance
        - loanBalance: LoanBalance
        + getEmployeeId(): String
        + getName(): String
        + getEmployeeType(): EmployeeType
        + getMonthlyRate(): double
        + getHourlyRate(): double
        + isHasLeave(): boolean
        + getLeaveBalance(): LeaveBalance
        + getLoanBalance(): LoanBalance
    }

    class EndUser {
        - username: String
        - passwordHash: String
        - role: Role
        - linkedEmployeeId: String
        + getUsername(): String
        + getPasswordHash(): String
        + getRole(): Role
        + getLinkedEmployeeId(): String
    }

    class AttendanceRecord {
        - employeeId: String
        - recordDate: LocalDate
        - timeIn: Double
        - timeOut: Double
        + getEmployeeId(): String
        + getRecordDate(): LocalDate
        + getTimeIn(): Double
        + getTimeOut(): Double
        + isAbsent(): boolean
    }

    class LeaveBalance {
        - sick: int
        - vacation: int
        - emergency: int
        + getSick(): int
        + getVacation(): int
        + getEmergency(): int
        + getTotal(): int
        + deduct(days: int): DeductionResult
        + apply(res: DeductionResult): LeaveBalance
    }

    class LoanBalance {
        - balance: double
        + getBalance(): double
        + deduct(amount: double): double
        + apply(amount: double): LoanBalance
    }

    %% Relationships
    AdminDashboardController --> EmployeeService
    AdminDashboardController --> AttendanceService
    AdminDashboardController --> SimplePayrollService
    KioskTerminalController --> EmployeeService
    KioskTerminalController --> AttendanceService

    EmployeeService --> AccountRepositoryPort
    EmployeeService --> EmployeeRepositoryPort
    EmployeeService ..> Employee
    EmployeeService ..> EndUser

    AttendanceService --> AttendanceRepositoryPort
    AttendanceService ..> AttendanceRecord

    SimplePayrollService ..> Employee
    SimplePayrollService ..> AttendanceRecord

    AccountRepositoryPort <|.. AccountRepository
    EmployeeRepositoryPort <|.. EmployeeRepository
    EmployeeRepositoryPort <|.. EmployeeDao
    AttendanceRepositoryPort <|.. AttendanceRepository
    AttendanceRepositoryPort <|.. AttendanceDao

    AccountRepository ..> EndUser
    Employee *-- LeaveBalance
    Employee *-- LoanBalance
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