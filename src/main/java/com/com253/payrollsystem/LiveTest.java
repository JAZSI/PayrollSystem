package com.com253.payrollsystem;

import com.com253.payrollsystem.Model.AttendanceRecord;
import com.com253.payrollsystem.Model.Employee;
import com.com253.payrollsystem.Model.EndUser;
import com.com253.payrollsystem.Model.LeaveBalance;
import com.com253.payrollsystem.Model.LeaveTransaction;
import com.com253.payrollsystem.Model.LoanBalance;
import com.com253.payrollsystem.Model.LoanTransaction;
import com.com253.payrollsystem.Model.PayrollEntry;
import com.com253.payrollsystem.Model.PayrollReportEntry;
import com.com253.payrollsystem.Model.Submission;
import com.com253.payrollsystem.Model.EmployeeTypes.Contractual;
import com.com253.payrollsystem.Model.EmployeeTypes.PartTimer;
import com.com253.payrollsystem.Model.EmployeeTypes.Probationary;
import com.com253.payrollsystem.Model.EmployeeTypes.Regular;
import com.com253.payrollsystem.Service.PayrollReportService;
import com.com253.payrollsystem.Service.PayrollService;
import com.com253.payrollsystem.Util.Database;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Interactive CLI shell for live-testing the payroll backend.
 */
public class LiveTest {

    private static final Scanner scanner = new Scanner(System.in);
    private static final PayrollService service = new PayrollService();
    private static final PayrollReportService reportService = new PayrollReportService();

    /**
     * Application entry point.
     */
    public static void main(String[] args) throws Exception {
        try {
            Database.initialize();
            System.out.println("Database initialized.\n");
            runLoginLoop();
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
        System.out.println("Goodbye!");
    }

    /**
     * Runs the login loop until user chooses to exit.
     */
    private static void runLoginLoop() {
        while (true) {
            System.out.println("========================================");
            System.out.println("         PAYROLL SYSTEM LOGIN           ");
            System.out.println("========================================");
            System.out.print("Username [0 to exit]: ");
            String username = scanner.nextLine().trim();

            if (username.equals("0")) {
                break;
            }

            System.out.print("Password: ");
            String password = scanner.nextLine().trim();

            try {
                EndUser user = service.authenticate(username, password);
                if (user == null) {
                    System.out.println("Invalid credentials\n");
                    continue;
                }

                System.out.println("Login successful! (" + user.getRole() + ")\n");

                if (user.getRole() == EndUser.Role.ADMIN) {
                    runAdminSession();
                } else {
                    runEmployeeSession(user.getLinkedEmployeeId());
                }
            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
            }
        }
    }

    // ============================================================
    // ADMIN SESSION
    // ============================================================

    private static void runAdminSession() {
        while (true) {
            System.out.println("\n-------- ADMIN MENU --------");
            System.out.println("[1] Add Employee");
            System.out.println("[2] View All Employees");
            System.out.println("[3] View Pending Submissions");
            System.out.println("[4] Approve Submission");
            System.out.println("[5] Reject Submission");
            System.out.println("[6] Delete Employee");
            System.out.println("[7] View Payroll Report");
            System.out.println("[8] Manage Employee Attendance");
            System.out.println("[9] Logout");
            System.out.print("Choice: ");

            String choice = scanner.nextLine().trim();
            System.out.println();

            try {
                switch (choice) {
                    case "1": addEmployee(); break;
                    case "2": viewAllEmployees(); break;
                    case "3": viewPendingSubmissions(); break;
                    case "4": approveSubmission(); break;
                    case "5": rejectSubmission(); break;
                    case "6": deleteEmployee(); break;
                    case "7": viewPayrollReport(); break;
                    case "8": manageAttendance(); break;
                    case "9": System.out.println("Logged out.\n"); return;
                    default: System.out.println("Invalid option"); break;
                }
            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
            }
        }
    }

    /**
     * Prompts for employee details and registers a new employee.
     */
    private static void addEmployee() throws SQLException {
        System.out.println("--- Add Employee ---");

        System.out.print("Employee ID: ");
        String id = scanner.nextLine().trim();

        System.out.print("Name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Type (R=Regular, P=Probationary, C=Contractual, T=PartTimer): ");
        String type = scanner.nextLine().trim().toUpperCase();

        System.out.print("Rate: ");
        double rate =Double.parseDouble(scanner.nextLine());

        System.out.print("Sick leave days: ");
        int sick = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Vacation leave days: ");
        int vacation = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Emergency leave days: ");
        int emergency = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Loan balance: ");
        double loan = Double.parseDouble(scanner.nextLine().trim());

        System.out.print("Username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        LeaveBalance leave = new LeaveBalance(sick, vacation, emergency);
        LoanBalance loanBalance = new LoanBalance(loan);

        Employee emp;
        if ("R".equals(type)) {
            emp = new Regular(id, name, rate, leave, loanBalance);
        } else if ("P".equals(type)) {
            emp = new Probationary(id, name, rate, leave, loanBalance);
        } else if ("C".equals(type)) {
            emp = new Contractual(id, name, rate, leave, loanBalance);
        } else if ("T".equals(type)) {
            emp = new PartTimer(id, name, rate, leave, loanBalance);
        } else {
            System.out.println("Invalid employee type: " + type);
            return;
        }

        service.registerEmployee(emp, username, password);
        System.out.println("Employee registered successfully!");
    }

    /**
     * Displays all employees.
     */
    private static void viewAllEmployees() throws SQLException {
        System.out.println("--- All Employees ---");

        List<Employee> employees = service.getAllEmployees();
        if (employees.isEmpty()) {
            System.out.println("No employees found.");
            return;
        }

        System.out.println("----------------------------------------");
        for (Employee emp : employees) {
            System.out.printf("ID: %s | %s (%s)%n", emp.getEmployeeId(), emp.getName(), emp.getTypeName());
            System.out.printf("  Rate: %.2f | Leaves: S=%d V=%d E=%d | Loan: %.2f%n",
                    emp.getMonthlyRate(),
                    emp.getLeaveBalance().getSick(),
                    emp.getLeaveBalance().getVacation(),
                    emp.getLeaveBalance().getEmergency(),
                    emp.getLoanBalance().getBalance());
            System.out.println("----------------------------------------");
        }
    }

    /**
     * Displays pending submissions.
     */
    private static void viewPendingSubmissions() throws SQLException {
        System.out.println("--- Pending Submissions ---");

        List<Submission> pending = service.getPendingSubmissions();
        if (pending.isEmpty()) {
            System.out.println("No pending submissions.");
            return;
        }

        for (Submission sub : pending) {
            System.out.printf("ID: %d | Employee: %s%n", sub.getId(), sub.getEmployeeId());
            System.out.printf("  Leave: %.1f | OT: %.1f hrs | Loan: %.2f | Status: %s%n",
                    sub.getLeaveDays(), sub.getOtHours(), sub.getLoanDeduction(), sub.getStatus());
            System.out.println("----------------------------------------");
        }
    }

    /**
     * Approves a submission.
     */
    private static void approveSubmission() throws SQLException {
        System.out.print("Submission ID: ");
        int id = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Cut-off period (1st-15th / 16th-30th): ");
        String period = scanner.nextLine().trim();

        service.updateSubmissionStatus(id, Submission.Status.APPROVED, period);
        System.out.println("Submission approved!");
    }

    /**
     * Rejects a submission.
     */
    private static void rejectSubmission() throws SQLException {
        System.out.print("Submission ID: ");
        int id = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Cut-off period (1st-15th / 16th-30th): ");
        String period = scanner.nextLine().trim();

        service.updateSubmissionStatus(id, Submission.Status.REJECTED, period);
        System.out.println("Submission rejected!");
    }

    /**
     * Deletes an employee and their account.
     */
    private static void deleteEmployee() throws SQLException {
        System.out.print("Employee ID to delete: ");
        String id = scanner.nextLine().trim();

        service.deleteEmployee(id);
        System.out.println("Employee deleted.");
    }

    /**
     * Displays payroll report for a period.
     */
    private static void viewPayrollReport() throws SQLException {
        System.out.print("Cut-off period (1st-15th / 16th-30th): ");
        String period = scanner.nextLine().trim();

        List<PayrollReportEntry> entries = reportService.getReportByPeriod(period);
        if (entries.isEmpty()) {
            System.out.println("No records for this period.");
            return;
        }

        System.out.println("--- Payroll Report: " + period + " ---");
        System.out.println("========================================");

        for (PayrollReportEntry e : entries) {
            double deductions = e.getGrossPay() - e.getNetPay();
            System.out.printf("%s | Gross: %.2f | Deductions: %.2f | Net: %.2f%n",
                    e.getEmployeeName(), e.getGrossPay(), deductions, e.getNetPay());
        }

        double[] totals = reportService.computePeriodTotals(entries);
        System.out.println("========================================");
        System.out.printf("PERIOD TOTALS | Gross: %.2f | Deductions: %.2f | Net: %.2f%n",
                totals[0], totals[1], totals[2]);
    }

    /**
     * Manages employee attendance records.
     */
    private static void manageAttendance() throws SQLException {
        System.out.print("Employee ID: ");
        String empId = scanner.nextLine().trim();

        LocalDate from, to;
        try {
            System.out.print("From date (YYYY-MM-DD): ");
            from = LocalDate.parse(scanner.nextLine().trim());
            System.out.print("To date (YYYY-MM-DD): ");
            to = LocalDate.parse(scanner.nextLine().trim());
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Use YYYY-MM-DD");
            return;
        }

        // Display records
        List<AttendanceRecord> records = service.getAttendanceHistory(empId, from, to);
        System.out.println("\n--- Attendance Records (" + from + " to " + to + ") ---");
        if (records.isEmpty()) {
            System.out.println("No records found.");
        } else {
            for (AttendanceRecord rec : records) {
                String in = rec.getTimeIn() != null ? rec.getTimeIn().toString() : "--";
                String out = rec.getTimeOut() != null ? rec.getTimeOut().toString() : "--";
                System.out.printf("  %s | In: %s | Out: %s%n", rec.getRecordDate(), in, out);
            }
        }

        // Sub-menu loop
        while (true) {
            System.out.println("\n--- Attendance Options ---");
            System.out.println("[1] Edit time-in");
            System.out.println("[2] Edit time-out");
            System.out.println("[3] Delete record");
            System.out.println("[4] Add/overwrite record");
            System.out.println("[5] Back to admin menu");
            System.out.print("Choice: ");

            String choice = scanner.nextLine().trim();
            System.out.println();

            try {
                switch (choice) {
                    case "1": editTimeIn(empId); break;
                    case "2": editTimeOut(empId); break;
                    case "3": deleteRecord(empId); break;
                    case "4": addOrOverwrite(empId); break;
                    case "5": return;
                    default: System.out.println("Invalid option"); break;
                }
            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Use YYYY-MM-DD");
            }
        }
    }

    private static void editTimeIn(String empId) throws SQLException {
        System.out.print("Date (YYYY-MM-DD): ");
        LocalDate date = LocalDate.parse(scanner.nextLine().trim());
        System.out.print("New time-in (decimal hours, e.g. 8.5): ");
        double timeIn = Double.parseDouble(scanner.nextLine().trim());
        service.updateTimeIn(empId, date, timeIn);
        System.out.println("Time-in updated.");
    }

    private static void editTimeOut(String empId) throws SQLException {
        System.out.print("Date (YYYY-MM-DD): ");
        LocalDate date = LocalDate.parse(scanner.nextLine().trim());
        System.out.print("New time-out (decimal hours, e.g. 17.5): ");
        double timeOut = Double.parseDouble(scanner.nextLine().trim());
        service.updateTimeOut(empId, date, timeOut);
        System.out.println("Time-out updated.");
    }

    private static void deleteRecord(String empId) throws SQLException {
        System.out.print("Date (YYYY-MM-DD): ");
        LocalDate date = LocalDate.parse(scanner.nextLine().trim());
        service.deleteAttendance(empId, date);
        System.out.println("Record deleted.");
    }

    private static void addOrOverwrite(String empId) throws SQLException {
        System.out.print("Date (YYYY-MM-DD): ");
        LocalDate date = LocalDate.parse(scanner.nextLine().trim());
        System.out.print("Time-in (decimal hours, e.g. 8.5): ");
        double timeIn = Double.parseDouble(scanner.nextLine().trim());
        System.out.print("Time-out (decimal hours, e.g. 17.5): ");
        double timeOut = Double.parseDouble(scanner.nextLine().trim());
        service.upsertAttendance(empId, date, timeIn, timeOut);
        System.out.println("Record saved.");
    }

    // ============================================================
    // EMPLOYEE SESSION
    // ============================================================

    private static void runEmployeeSession(String employeeId) {
        while (true) {
            System.out.println("\n-------- EMPLOYEE MENU --------");
            System.out.println("[1] View My Info");
            System.out.println("[2] Clock In");
            System.out.println("[3] Clock Out");
            System.out.println("[4] Submit Payroll");
            System.out.println("[5] View Submission Status");
            System.out.println("[6] View Payslip");
            System.out.println("[7] View Leave History");
            System.out.println("[8] View Loan History");
            System.out.println("[9] Logout");
            System.out.print("Choice: ");

            String choice = scanner.nextLine().trim();
            System.out.println();

            try {
                switch (choice) {
                    case "1": viewMyInfo(employeeId); break;
                    case "2": clockIn(employeeId); break;
                    case "3": clockOut(employeeId); break;
                    case "4": submitPayroll(employeeId); break;
                    case "5": viewSubmissionStatus(employeeId); break;
                    case "6": viewPayslip(employeeId); break;
                    case "7": viewLeaveHistory(employeeId); break;
                    case "8": viewLoanHistory(employeeId); break;
                    case "9": System.out.println("Logged out.\n"); return;
                    default: System.out.println("Invalid option"); break;
                }
            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
            }
        }
    }

    /**
     * Displays the employee's own information.
     */
    private static void viewMyInfo(String employeeId) throws SQLException {
        System.out.println("--- My Info ---");

        Employee emp = service.findEmployee(employeeId);
        if (emp == null) {
            System.out.println("Employee not found.");
            return;
        }

        System.out.printf("ID: %s%n", emp.getEmployeeId());
        System.out.printf("Name: %s%n", emp.getName());
        System.out.printf("Type: %s%n", emp.getTypeName());
        System.out.printf("Rate: %.2f%n", emp.getMonthlyRate());
        System.out.printf("Sick Leave: %d | Vacation: %d | Emergency: %d%n",
                emp.getLeaveBalance().getSick(),
                emp.getLeaveBalance().getVacation(),
                emp.getLeaveBalance().getEmergency());
        System.out.printf("Loan Balance: %.2f%n", emp.getLoanBalance().getBalance());
    }

    /**
     * Records a clock-in for today.
     */
    private static void clockIn(String employeeId) throws SQLException {
        System.out.print("Time in (decimal hours, e.g. 8.5): ");
        double timeIn = Double.parseDouble(scanner.nextLine().trim());
        service.clockIn(employeeId, LocalDate.now(), timeIn);
        System.out.println("Clocked in successfully.");
    }

    /**
     * Records a clock-out for today.
     */
    private static void clockOut(String employeeId) throws SQLException {
        System.out.print("Time out (decimal hours, e.g. 17.5): ");
        double timeOut = Double.parseDouble(scanner.nextLine().trim());
        service.clockOut(employeeId, LocalDate.now(), timeOut);
        System.out.println("Clocked out successfully.");
    }

    /**
     * Submits a payroll request.
     */
    private static void submitPayroll(String employeeId) throws SQLException {
        Submission existing = service.getSubmission(employeeId);
        if (existing != null && existing.getStatus() == Submission.Status.APPROVED) {
            System.out.println("Already approved — cannot resubmit");
            return;
        }

        System.out.print("Leave days: ");
        double leaveDays = Double.parseDouble(scanner.nextLine().trim());

        System.out.print("OT hours: ");
        double otHours = Double.parseDouble(scanner.nextLine().trim());

        System.out.print("Loan deduction: ");
        double loanDeduction = Double.parseDouble(scanner.nextLine().trim());

        boolean success = service.submitPayroll(employeeId, leaveDays, otHours, loanDeduction);
        if (success) {
            System.out.println("Submitted successfully — pending admin approval");
        } else {
            System.out.println("Submission blocked — an approved submission exists");
        }
    }

    /**
     * Views submission status.
     */
    private static void viewSubmissionStatus(String employeeId) throws SQLException {
        System.out.println("--- Submission Status ---");

        Submission sub = service.getSubmission(employeeId);
        if (sub == null) {
            System.out.println("No submission on file");
            return;
        }

        System.out.printf("Status: %s%n", sub.getStatus());
        System.out.printf("Leave days: %.1f | OT hours: %.1f | Loan: %.2f%n",
                sub.getLeaveDays(), sub.getOtHours(), sub.getLoanDeduction());
        System.out.printf("Submitted at: %s%n", sub.getSubmittedAt());
    }

    /**
     * Displays payslip for a period.
     */
    private static void viewPayslip(String employeeId) throws SQLException {
        System.out.print("Cut-off period (1st-15th / 16th-30th): ");
        String period = scanner.nextLine().trim();

        PayrollEntry entry = service.buildPayrollEntry(employeeId, period);
        if (entry == null) {
            System.out.println("Payslip not available — submission must be approved first");
            return;
        }

        Employee emp = entry.getEmployee();

        System.out.println("========================================");
        System.out.printf("Employee: %s (%s)%n", emp.getName(), emp.getTypeName());
        System.out.printf("Period: %s%n", entry.getCutOffPeriod());
        System.out.println("----------------------------------------");
        System.out.printf("Total Hours: %.2f | OT Hours: %.2f%n", entry.getTotalHoursWorked(), entry.getOvertimeHours());
        System.out.printf("Undertime Hours: %.2f | Absent Days: %d%n", entry.getUndertimeHours(), entry.getAbsentDays());
        System.out.println("========================================");
        System.out.printf("Basic Pay:      %10.2f%n", entry.getBasicPay());
        System.out.printf("OT Pay:         %10.2f%n", entry.getOvertimePay());
        System.out.printf("Holiday Pay:    %10.2f%n", entry.getHolidayPay());
        System.out.printf("NSD:            %10.2f%n", entry.getNightShiftDifferential());
        System.out.printf("GROSS PAY:      %10.2f%n", entry.getGrossPay());
        System.out.println("========================================");
        System.out.printf("SSS:            %10.2f%n", entry.getSssDeduction());
        System.out.printf("PhilHealth:     %10.2f%n", entry.getPhilhealthDeduction());
        System.out.printf("Pag-IBIG:       %10.2f%n", entry.getPagibigDeduction());
        System.out.printf("Tax:            %10.2f%n", entry.getTaxDeduction());
        System.out.printf("Loan:           %10.2f%n", entry.getLoanDeduction());
        System.out.printf("Undertime Pnlt: %10.2f%n", entry.getUndertimePenalty());
        System.out.printf("Absence Pnlt:   %10.2f%n", entry.getAbsencePenalty());
        System.out.println("========================================");
        System.out.printf("NET PAY:        %10.2f%n", entry.getNetPay());
        System.out.println("========================================");
    }

    /**
     * Displays leave transaction history.
     */
    private static void viewLeaveHistory(String employeeId) throws SQLException {
        System.out.println("--- Leave History ---");

        List<LeaveTransaction> history = service.getLeaveHistory(employeeId);
        if (history.isEmpty()) {
            System.out.println("No leave transactions on file");
            return;
        }

        for (LeaveTransaction tx : history) {
            System.out.printf("%s | Type: %s | Days: %d | Period: %s%n",
                    tx.getCreatedAt(), tx.getLeaveType(), tx.getDays(), tx.getCutOffPeriod());
        }
    }

    /**
     * Displays loan transaction history.
     */
    private static void viewLoanHistory(String employeeId) throws SQLException {
        System.out.println("--- Loan History ---");

        List<LoanTransaction> history = service.getLoanHistory(employeeId);
        if (history.isEmpty()) {
            System.out.println("No loan transactions on file");
            return;
        }

        for (LoanTransaction tx : history) {
            System.out.printf("%s | Amount: %.2f | Period: %s%n",
                    tx.getCreatedAt(), tx.getAmount(), tx.getCutOffPeriod());
        }
    }
}