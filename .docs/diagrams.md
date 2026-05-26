# Payroll Management System: UML Design Document

---

## 1. Class Diagram

```mermaid
classDiagram
    %% --- PRESENTATION LAYER ---
    class SceneManager {
        -SceneManager instance$
        -Map~Screen, Scene~ sceneCache
        -Map~Screen, Object~ controllerCache
        -Stage primaryStage
        -Screen currentScreen
        -SceneManager()
        +getInstance() SceneManager$
        +initialize(Stage stage) void
        +switchScene(Screen screen) void
        +switchScene(Stage stage, Screen screen) void
        +showModal(Screen screen, String title) Stage
        +getController(Screen screen, Class~T~ clazz) T
        +clearCache() void
    }

    class Routes {
        <<enumeration>>
        ADMIN_DASHBOARD
        ADMIN_EMPLOYEES
        ADMIN_ATTENDANCE
        ADMIN_PAYROLL
        ADMIN_REPORTS
        EMPLOYEE_LOGIN
        EMPLOYEE_DASHBOARD
        KIOSK_IDLE
        KIOSK_ACTIVE
        +getFxmlPath() String
        +getCssPath() String
        +getModule() String
    }

    class AdminDashboardController {
        -EmployeeService employeeService
        -AttendanceService attendanceService
        -SimplePayrollService payrollService
        -ObservableList~Employee~ employees
        -ObservableList~AttendanceRow~ attendanceRows
        -ObservableList~PayrollRow~ payrollRows
        +initialize() void
        +showEmployees() void
        +saveEmployee() void
        +loadAttendance() void
        +computePayroll() void
        +handleLogout() void
    }

    class KioskTerminalController {
        -EmployeeService employeeService
        -AttendanceService attendanceService
        -StringBuilder enteredId
        -Employee currentEmployee
        +initialize() void
        +handleSubmit() void
        +handleTimeIn() void
        +handleTimeOut() void
        -processAutoAttendance(Employee emp) void
    }

    class EmployeePortalController {
        +initialize() void
        +handleLogin() void
        +switchToFirstCutoff() void
        +switchToSecondCutoff() void
        +handleDownloadPdf() void
    }

    %% --- APPLICATION LAYER (SERVICES & PORTS) ---
    class AuthService {
        -AccountRepository accountRepository
        +authenticate(String user, String pass) EndUser
    }

    class EmployeeService {
        -AccountRepositoryPort accountRepository
        -EmployeeRepositoryPort employeeRepository
        +registerEmployee(Employee emp, String user, String pass) void
        +saveEmployee(Employee emp) void
        +findEmployee(String id) Employee
        +getAllEmployees() List~Employee~
        +deleteEmployee(String id) void
    }

    class AttendanceService {
        -AttendanceRepositoryPort attendanceRepository
        +clockIn(String empId, LocalDate date, double time) void
        +clockOut(String empId, LocalDate date, double time) void
        +upsertAttendance(String empId, LocalDate date, Double timeIn, Double timeOut) void
        +getAttendanceHistory(String empId, LocalDate from, LocalDate to) List~AttendanceRecord~
    }

    class SubmissionService {
        -EmployeeRepositoryPort employeeRepository
        -SubmissionRepositoryPort submissionRepository
        -DeductionService deductionService
        +submitPayroll(String empId, double leaves, double ot, double loan) boolean
        +getPendingSubmissions() List~Submission~
        +updateSubmissionStatus(int subId, Status status, String cutoff) void
        +buildPayrollEntry(String empId, String cutoff) PayrollEntry
    }

    class PayrollGenerationService {
        -AttendanceRepositoryPort attendanceRepository
        -EmployeeRepositoryPort employeeRepository
        -SubmissionRepositoryPort submissionRepository
        +buildPayrollEntry(String empId, String cutoff) PayrollEntry
        -buildTimeRecords(String empId, LocalDate from, LocalDate to) List~TimeRecord~
    }

    class DeductionService {
        -EmployeeRepositoryPort employeeRepository
        -LeaveTransactionRepositoryPort leaveTransactionRepository
        -LoanTransactionRepositoryPort loanTransactionRepository
        +applyDeductions(Employee emp, int leaves, double loan, String cutoff) void
        +getLeaveHistory(String empId) List~LeaveTransaction~
        +getLoanHistory(String empId) List~LoanTransaction~
    }

    class HolidayService {
        -int year
        +getHolidayType(LocalDate date) HolidayType
    }

    class AccountRepositoryPort {
        <<interface>>
        +save(EndUser user) void
        +findByUsername(String user) Optional~EndUser~
        +deleteByEmployeeId(String empId) void
    }

    class EmployeeRepositoryPort {
        <<interface>>
        +findById(String id) Optional~Employee~
        +findAll() List~Employee~
        +save(Employee emp) void
        +delete(String id) void
        +updateLeaveBalance(String id, LeaveBalance bal) void
        +updateLoanBalance(String id, LoanBalance bal) void
    }

    class AttendanceRepositoryPort {
        <<interface>>
        +clockIn(String id, LocalDate date, double time) void
        +clockOut(String id, LocalDate date, double time) void
        +getAttendance(String id, LocalDate from, LocalDate to) List~AttendanceRecord~
        +upsert(String id, LocalDate date, Double timeIn, Double timeOut) void
    }

    class SubmissionRepositoryPort {
        <<interface>>
        +save(Submission sub) boolean
        +findById(int id) Optional~Submission~
        +findByEmployeeId(String empId) Optional~Submission~
        +findAllPending() List~Submission~
        +updateStatus(int id, Status status) void
        +savePayrollEntry(PayrollEntry entry) void
        +deleteByEmployeeId(String empId) void
    }

    %% --- INFRASTRUCTURE PERSISTENCE ---
    class Database {
        +getConnection() Connection$
        +close(Connection conn)$
    }

    class TransactionManager {
        +begin(Connection conn)$
        +commit(Connection conn)$
        +rollback(Connection conn)$
    }

    class EmployeeRepository {
        +findById(String id) Optional~Employee~
    }

    class EmployeeDao {
        +findById(String id) Optional~Employee~
    }

    class AttendanceRepository {
        +getAttendance(...) List~AttendanceRecord~
    }

    class AttendanceDao {
        +getAttendance(...) List~AttendanceRecord~
    }

    class SubmissionRepository {
        +save(Submission sub) boolean
    }

    %% --- DOMAIN LAYER ---
    class Employee {
        -String employeeId
        -String name
        -EmployeeType type
        -double monthlyRate
        -double hourlyRate
        -boolean hasLeave
        -LeaveBalance leaveBalance
        -LoanBalance loanBalance
        +getEmployeeId() String
        +getName() String
        +getLeaveBalance() LeaveBalance
        +getLoanBalance() LoanBalance
        +setLeaveBalance(LeaveBalance bal) void
        +setLoanBalance(LoanBalance bal) void
    }

    class EndUser {
        -String username
        -String passwordHash
        -Role role
        -String linkedEmployeeId
        +getUsername() String
        +getRole() Role
    }

    class LeaveBalance {
        -int sick
        -int vacation
        -int emergency
        +deduct(int days) DeductionResult
        +apply(DeductionResult res) LeaveBalance
        +getTotal() int
    }

    class LoanBalance {
        -double balance
        +deduct(double amount) double
        +apply(double deducted) LoanBalance
        +getBalance() double
    }

    class AttendanceRecord {
        -String employeeId
        -LocalDate recordDate
        -Double timeIn
        -Double timeOut
        +getTimeIn() Double
        +getTimeOut() Double
    }

    class TimeRecord {
        -int dayOfMonth
        -int timeIn
        -int timeOut
        -boolean isAbsent
        -HolidayType holidayType
    }

    class PayrollEntry {
        <<record>>
        +Employee employee
        +String cutOffPeriod
        +double totalHoursWorked
        +double overtimeHours
        +double basicPay
        +double overtimePay
        +double grossPay
        +double sssDeduction
        +double netPay
    }

    class Submission {
        -int submissionId
        -String employeeId
        -double leaveDays
        -double otHours
        -double loanDeduction
        -Status status
    }

    class PayrollCalculator {
        +computeHoursWorked(TimeRecord rec, PayrollSettings set) double$
        +computeGrossPay(Employee emp, TimeRecord[] recs, PayrollSettings set) double$
        +buildPayrollEntry(...) PayrollEntry$
    }

    class NetPayCalculator {
        +buildPayrollEntry(Employee emp, TimeRecord[] recs, String cutoff, double loan, PayrollSettings set) PayrollEntry$
    }

    %% --- RELATIONSHIPS ---
    SceneManager o--> Routes
    AdminDashboardController --> EmployeeService
    AdminDashboardController --> AttendanceService
    KioskTerminalController --> EmployeeService
    KioskTerminalController --> AttendanceService
    EmployeePortalController --> AuthService

    EmployeeService --> AccountRepositoryPort
    EmployeeService --> EmployeeRepositoryPort
    AttendanceService --> AttendanceRepositoryPort
    SubmissionService --> EmployeeRepositoryPort
    SubmissionService --> SubmissionRepositoryPort
    SubmissionService --> DeductionService
    PayrollGenerationService --> AttendanceRepositoryPort
    PayrollGenerationService --> EmployeeRepositoryPort
    PayrollGenerationService --> SubmissionRepositoryPort
    DeductionService --> EmployeeRepositoryPort
    DeductionService --> LeaveTransactionRepositoryPort
    DeductionService --> LoanTransactionRepositoryPort

    EmployeeRepositoryPort <|.. EmployeeRepository
    EmployeeRepositoryPort <|.. EmployeeDao
    AttendanceRepositoryPort <|.. AttendanceRepository
    AttendanceRepositoryPort <|.. AttendanceDao
    SubmissionRepositoryPort <|.. SubmissionRepository

    Employee *-- LeaveBalance
    Employee *-- LoanBalance
    EndUser --> Employee : linkedEmployeeId
    PayrollEntry o--> Employee
    Submission o--> Employee
    TimeRecord --> HolidayType : references

    PayrollGenerationService ..> HolidayService : uses
    PayrollGenerationService ..> PayrollCalculator : uses
    PayrollCalculator ..> NetPayCalculator : delegates
    NetPayCalculator ..> PayrollEntry : instantiates
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
flowchart TD
    %% Define Layers as Subgraphs
    subgraph Presentation ["Presentation Layer (JavaFX & CLI)"]
        fxml["FXML Views (dashboard.fxml, kiosk.fxml, portal.fxml)"]
        ctrl["Controllers (AdminDashboard, KioskTerminal, EmployeePortal)"]
        nav["SceneManager & Routes (Navigation Context)"]
        cli["CLI Interface (MainMenu, Commands)"]
    end

    subgraph Application ["Application Layer (Services & Ports)"]
        services["Application Services (EmployeeService, AttendanceService, PayrollGenerationService, SubmissionService)"]
        ports["Repository Ports (Interfaces: EmployeeRepositoryPort, AttendanceRepositoryPort, etc.)"]
    end

    subgraph Domain ["Domain Layer (Core Logic & Models)"]
        domain_services["Domain Services (PayrollCalculator, EarningsCalculator, NetPayCalculator)"]
        models["Entities & Value Objects (Employee, PayrollEntry, AttendanceRecord, LeaveBalance, LoanBalance)"]
    end

    subgraph Infrastructure ["Infrastructure Layer (SQLite & Config)"]
        repos["JDBC Repositories (EmployeeDao, EmployeeRepository, AttendanceDao, SubmissionRepository)"]
        db_config["Database Connections (Database, TransactionManager, JdbcTemplate)"]
        sqlite[("SQLite Database (payroll.db)")]
    end

    subgraph Shared ["Shared / Cross-Cutting Layer"]
        utils["Shared Utilities (TimeUtils, Logger)"]
    end

    %% Core Dependency & Inter-Layer Interactions
    fxml -->|Binds events to| ctrl
    ctrl -->|Uses SceneManager for| nav
    ctrl -->|Invokes use cases on| services
    cli -->|Invokes commands on| services
    
    services -->|Delegates to| domain_services
    services -->|Mutates/Instantiates| models
    services -->|Performs I/O via| ports
    
    repos -.->|Implements| ports
    repos -->|Constructs queries using| db_config
    db_config -->|Reads/Writes| sqlite
    
    domain_services -->|Processes calculations on| models
    
    %% Utilities references
    Presentation -.->|Uses| utils
    Application -.->|Uses| utils
    Domain -.->|Uses| utils
    Infrastructure -.->|Uses| utils

    %% Styling
    classDef pres fill:#EBF5FB,stroke:#2980B9,stroke-width:2px,color:#2C3E50;
    classDef app fill:#FEF9E7,stroke:#F39C12,stroke-width:2px,color:#2C3E50;
    classDef dom fill:#E8F8F5,stroke:#1ABC9C,stroke-width:2px,color:#2C3E50;
    classDef infra fill:#FDEDEC,stroke:#E74C3C,stroke-width:2px,color:#2C3E50;
    classDef share fill:#F4F6F7,stroke:#7F8C8D,stroke-width:2px,color:#2C3E50;
    classDef db fill:#D5F5E3,stroke:#27AE60,stroke-width:2px,color:#2C3E50;

    class fxml,ctrl,nav,cli pres;
    class services,ports app;
    class domain_services,models dom;
    class repos,db_config infra;
    class utils share;
    class sqlite db;
```

---

## 4. State Diagrams

### Employee Attendance Lifecycle

```mermaid
stateDiagram-v2
    [*] --> Absent : Start of Working Day
    [*] --> Holiday : Date detected as Holiday (via HolidayService)
    
    Absent --> Clocked_In : Clock In Event (Valid ID Entered)
    Absent --> Absent : End of Day (No Clock In recorded)
    
    Clocked_In --> Clocked_Out : Clock Out Event (Second Kiosk Input)
    Clocked_In --> Clocked_In : Duplicate Clock In Attempt (Warning Displayed)
    Clocked_In --> Auto_Clocked_Out : System cutoff reached (Assumed out at 17:00)
    
    Clocked_Out --> Processed : Cutoff Period Closes -> Payroll Calculated
    Auto_Clocked_Out --> Processed : Cutoff Period Closes -> Payroll Calculated (Undertime Penalty Applied)
    Absent --> Processed : Cutoff Period Closes -> Payroll Calculated (Absence Penalty Applied)
    Holiday --> Processed : Cutoff Period Closes -> Payroll Calculated (Holiday Credit Applied)
    
    Processed --> [*]
```

### Payroll Processing Lifecycle

```mermaid
stateDiagram-v2
    [*] --> Draft : Create Cutoff Period
    Draft --> Pending_Calculation : Time records accumulating
    Pending_Calculation --> Generated : Admin triggers "Compute Payroll"
    
    Generated --> Reviewed : Admin reviews calculated entries (SSS, Philhealth, Tax, Net Pay)
    Reviewed --> Draft : Calculations need updates (re-run triggered)
    Reviewed --> Approved : Admin approves & locks Payroll Entry
    
    Approved --> Paid : Bank release advice / Cash payout completed
    Paid --> Archived : Payslips published & records archived for compliance
    Archived --> [*]
```

### Leave and Submission Lifecycle

```mermaid
stateDiagram-v2
    [*] --> Pending : Employee submits Payroll Parameters
    
    Pending --> Approved : Admin reviews & approves submission
    Pending --> Rejected : Admin reviews & rejects submission
    
    state Approved {
        [*] --> Deducting_Leave : Check eligibility (hasLeave = true)
        Deducting_Leave --> Deducting_Loan : Save LeaveTransaction & update credits
        Deducting_Loan --> Applied : Apply loan deduction & update outstanding balance
    }
    
    Applied --> Processed : Saved to Payroll Generation Service
    Rejected --> [*] : Terminated (No deductions applied)
    Processed --> [*]
```

---

## 5. Sequence Diagram: Payroll Generation

```mermaid
sequenceDiagram
    autonumber
    actor Admin
    participant Ctrl as AdminDashboardController
    participant PGS as PayrollGenerationService
    participant SubRepo as SubmissionRepositoryPort
    participant EmpRepo as EmployeeRepositoryPort
    participant AttRepo as AttendanceRepositoryPort
    participant HolSvc as HolidayService
    participant Calc as PayrollCalculator
    participant NPC as NetPayCalculator

    Admin->>Ctrl: Click "Compute Payroll"
    activate Ctrl
    
    Ctrl->>PGS: buildPayrollEntry(employeeId, cutOffPeriod)
    activate PGS
    
    Note over PGS: Start JDBC Transaction (TransactionManager.begin)
    
    PGS->>SubRepo: findByEmployeeId(employeeId)
    activate SubRepo
    SubRepo-->>PGS: Optional<Submission> (contains approved leave days, OT hours, loanDeduction)
    deactivate SubRepo
    
    PGS->>EmpRepo: findById(employeeId)
    activate EmpRepo
    EmpRepo-->>PGS: Optional<Employee> (rates, balances)
    deactivate EmpRepo
    
    PGS->>AttRepo: getAttendance(employeeId, from, to)
    activate AttRepo
    AttRepo-->>PGS: List<AttendanceRecord>
    deactivate AttRepo
    
    loop For each date in Cutoff Date Range
        PGS->>HolSvc: getHolidayType(currentDate)
        activate HolSvc
        HolSvc-->>PGS: HolidayType (REGULAR, SPECIAL, NONE)
        deactivate HolSvc
        PGS->>PGS: Map AttendanceRecord to TimeRecord (with holiday properties)
    end
    
    PGS->>Calc: buildPayrollEntry(employee, timeRecords[], cutOffPeriod, loanDeduction, settings)
    activate Calc
    
    Calc->>NPC: buildPayrollEntry(...)
    activate NPC
    
    Note over NPC: Compute worked/OT/undertime hours (WorkHoursCalculator)
    Note over NPC: Calculate basic & premium earnings (EarningsCalculator)
    Note over NPC: Calculate government contributions (GovDeductionCalculator)
    Note over NPC: Deduct loan amount & calculate undertime/absence penalties (PenaltyCalculator)
    
    NPC-->>Calc: PayrollEntry (Record representation)
    deactivate NPC
    
    Calc-->>PGS: PayrollEntry
    deactivate Calc
    
    PGS->>SubRepo: savePayrollEntry(entry)
    activate SubRepo
    SubRepo-->>PGS: void
    deactivate SubRepo
    
    Note over PGS: Commit JDBC Transaction (TransactionManager.commit)
    
    PGS-->>Ctrl: PayrollEntry
    deactivate PGS
    
    Ctrl->>Admin: Populate TableView (convert to PayrollRow & display)
    deactivate Ctrl
```

---

## 6. Activity Diagram: Kiosk Attendance Flow

```mermaid
flowchart TD
    %% Start Node
    startNode([Start: Kiosk Idle]) --> input[User enters Employee ID & clicks Submit]
    
    input --> findEmp{Search Employee in DB}
    
    %% Employee check branch
    findEmp -->|Not Found| errNF[Show 'Employee Not Found' screen]
    errNF --> waitNF[Wait 3 Seconds]
    waitNF --> idle[Return to Idle State]
    
    findEmp -->|Found| showActive[Show 'Active' screen with Employee Profile]
    showActive --> checkRec{Check today's attendance record}
    
    %% Attendance cases
    checkRec -->|No record OR Time In is null| doClockIn[Record Time In in Database]
    doClockIn --> showInSuccess[Show 'Time In Success' screen with Timestamp]
    showInSuccess --> waitIn[Wait 3 Seconds]
    waitIn --> idle
    
    checkRec -->|Time In exists AND Time Out is null| doClockOut[Record Time Out in Database]
    doClockOut --> showOutSuccess[Show 'Time Out Success' screen with Timestamp]
    showOutSuccess --> waitOut[Wait 3 Seconds]
    waitOut --> idle
    
    checkRec -->|Both Time In & Time Out exist| showErrDup[Show 'Already Timed In' duplicate error]
    showErrDup --> waitDup[Wait 3 Seconds]
    waitDup --> idle

    idle --> endNode([End: Kiosk Reset])
    
    %% Styling
    classDef action fill:#d5f5e3,stroke:#27ae60,stroke-width:1.5px,color:#196f3d;
    classDef decision fill:#fcf3cf,stroke:#f1c40f,stroke-width:1.5px,color:#7d6608;
    classDef error fill:#fadbd8,stroke:#e74c3c,stroke-width:1.5px,color:#78281f;
    classDef state fill:#ebf5fb,stroke:#2980b9,stroke-width:1.5px,color:#1b4f72;

    class input,doClockIn,doClockOut action;
    class findEmp,checkRec decision;
    class errNF,showErrDup error;
    class showActive,showInSuccess,showOutSuccess,waitNF,waitIn,waitOut,waitDup,idle state;
```

---

## 7. Package Diagram

```mermaid
flowchart TD
    %% Define Packages
    subgraph com.com253.payrollsystem
        subgraph presentation [presentation]
            direction LR
            gui[gui]
            cli[cli]
        end
        
        subgraph app [app]
            direction LR
            service[service]
            port[port]
        end
        
        subgraph domain [domain]
            direction LR
            model[model]
            dom_svc[service]
            policy[policy]
        end
        
        subgraph infrastructure [infrastructure]
            direction LR
            persistence[persistence]
            config[config]
            logging[logging]
        end
        
        subgraph util [shared/util]
            time_utils[TimeUtils]
        end
    end
    
    %% Dependency Relationships
    presentation -->|uses| service
    presentation -->|uses| model
    presentation -->|uses| util
    
    service -->|uses| port
    service -->|uses| model
    service -->|uses| dom_svc
    service -->|uses| config
    service -->|uses| util
    
    port -->|defines entities| model
    
    dom_svc -->|processes| model
    policy -->|uses| model
    
    persistence -->|implements| port
    persistence -->|maps| model
    persistence -->|uses| config
    
    config -->|manages connections| logging
    
    %% Shared Utility references
    infrastructure -->|uses| util
    domain -->|uses| util

    %% Style packages
    classDef pkg fill:#fcf3cf,stroke:#f39c12,stroke-width:1.5px,color:#7e5109;
    classDef root fill:#ebf5fb,stroke:#2980b9,stroke-width:2px,color:#1b4f72;
    classDef subpkg fill:#fafafa,stroke:#ccc,stroke-width:1px,color:#333;
    
    class com.com253.payrollsystem root;
    class presentation,app,domain,infrastructure pkg;
    class gui,cli,service,port,model,dom_svc,policy,persistence,config,logging,util subpkg;
```
