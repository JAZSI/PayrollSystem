package com.com253.payrollsystem.presentation.cli.menu;

import com.com253.payrollsystem.app.service.HolidayService;
import com.com253.payrollsystem.app.service.PayrollReportService;
import com.com253.payrollsystem.app.service.PayrollService;
import com.com253.payrollsystem.domain.model.Employee;
import com.com253.payrollsystem.domain.model.Employee.EmployeeType;
import com.com253.payrollsystem.domain.model.LeaveBalance;
import com.com253.payrollsystem.domain.model.LoanBalance;
import com.com253.payrollsystem.domain.model.PayrollEntry;
import com.com253.payrollsystem.domain.model.PayrollReportEntry;
import com.com253.payrollsystem.domain.model.Submission;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class SimulationMenu {

    private final PayrollService payrollService;
    private final PayrollReportService reportService;
    private final HolidayService holidayService;
    private final Scanner scanner;
    private final String currentCutoffPeriod;
    private final String demoEmployeeId;
    private final String demoUsername;
    private final String demoPassword = "Demo123!";
    private PayrollEntry lastEntry;

    public SimulationMenu(PayrollService payrollService, Scanner scanner) {
        this.payrollService = payrollService;
        this.reportService = new PayrollReportService();
        this.holidayService = new HolidayService();
        this.scanner = scanner;
        this.currentCutoffPeriod = currentCutoffPeriod();

        String stamp = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS").format(LocalDateTime.now());
        this.demoEmployeeId = "SIM-" + stamp;
        this.demoUsername = "sim_" + stamp;
    }

    public void run() {
        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> showHolidayChecks();
                    case "2" -> registerDemoEmployee();
                    case "3" -> listEmployees();
                    case "4" -> seedAttendanceForCurrentCutoff();
                    case "5" -> runClockInOutToday();
                    case "6" -> runSubmissionApproval();
                    case "7" -> buildPayrollEntry();
                    case "8" -> printReportSummary();
                    case "9" -> runFullSimulation();
                    case "0" -> running = false;
                    default -> System.out.println("Unknown choice: " + choice);
                }
            } catch (Exception e) {
                System.out.println("Simulation error: " + e.getMessage());
            }
        }

        System.out.println("Simulation menu closed.");
    }

    private void printMenu() {
        System.out.println();
        System.out.println("=== Payroll Simulation Menu ===");
        System.out.println("1) Show holiday checks");
        System.out.println("2) Register demo employee");
        System.out.println("3) List employees");
        System.out.println("4) Seed attendance for payroll");
        System.out.println("5) Clock in and out today");
        System.out.println("6) Submit and approve payroll");
        System.out.println("7) Build payroll entry");
        System.out.println("8) View payroll report summary");
        System.out.println("9) Run full simulation");
        System.out.println("0) Exit");
        System.out.print("Choice: ");
    }

    private void runFullSimulation() throws Exception {
        authenticateAdmin();
        showHolidayChecks();
        registerDemoEmployee();
        listEmployees();
        seedAttendanceForCurrentCutoff();
        runClockInOutToday();
        runSubmissionApproval();
        buildPayrollEntry();
        printReportSummary();
    }

    private void authenticateAdmin() throws SQLException {
        String adminPassword = System.getProperty("payroll.admin.password", "admin123");
        var user = payrollService.authenticate("admin", adminPassword);
        if (user == null) {
            System.out.println("Admin authentication failed.");
        } else {
            System.out.println("Admin authentication succeeded for user: " + user.getUsername());
        }
    }

    private void showHolidayChecks() {
        LocalDate today = LocalDate.now();
        LocalDate[] samples = new LocalDate[] {
            today,
            LocalDate.of(today.getYear(), Month.JANUARY, 1),
            LocalDate.of(today.getYear(), Month.JUNE, 12),
            LocalDate.of(today.getYear(), Month.DECEMBER, 25),
            LocalDate.of(today.getYear(), Month.DECEMBER, 30),
            lastMondayOfAugust(today.getYear())
        };

        System.out.println();
        System.out.println("Holiday checks using the hardcoded holiday service:");
        for (LocalDate sample : samples) {
            System.out.println("- " + sample + " => " + holidayService.getHolidayType(sample));
        }
    }

    private Employee registerDemoEmployee() throws SQLException {
        Employee existing = payrollService.findEmployee(demoEmployeeId);
        if (existing != null) {
            System.out.println("Demo employee already exists: " + existing.getEmployeeId());
            return existing;
        }

        Employee employee = new Employee(
                demoEmployeeId,
                "Simulation Employee",
                EmployeeType.REGULAR,
                32000.0,
                0.0,
                true,
                new LeaveBalance(5, 5, 5),
                new LoanBalance(5000.0));

        payrollService.registerEmployee(employee, demoUsername, demoPassword);
        System.out.println("Registered demo employee: " + demoEmployeeId + " / " + demoUsername);
        return employee;
    }

    private void listEmployees() throws SQLException {
        List<Employee> employees = payrollService.getAllEmployees();
        System.out.println();
        System.out.println("Employees:");
        for (Employee employee : employees) {
            System.out.println("- " + employee.getEmployeeId() + " | " + employee.getName() + " | " + employee.getTypeName());
        }
    }

    private void seedAttendanceForCurrentCutoff() throws Exception {
        ensureDemoEmployee();

        LocalDate from = cutoffStart(LocalDate.now(), currentCutoffPeriod);
        LocalDate to = cutoffEnd(LocalDate.now(), currentCutoffPeriod);

        double[][] shifts = {
            {8.00, 17.50},
            {8.25, 18.00},
            {8.00, 17.00},
            {8.00, 18.00},
            {8.50, 17.50}
        };

        int seeded = 0;
        LocalDate date = from;
        while (!date.isAfter(to) && seeded < shifts.length) {
            if (isWeekday(date)) {
                payrollService.upsertAttendance(
                        demoEmployeeId,
                        date,
                        shifts[seeded][0],
                        shifts[seeded][1]);
                seeded++;
            }
            date = date.plusDays(1);
        }

        System.out.println("Seeded " + seeded + " attendance record(s) for cutoff " + currentCutoffPeriod + ".");
    }

    private void runClockInOutToday() throws Exception {
        ensureDemoEmployee();
        LocalDate today = LocalDate.now();
        payrollService.clockIn(demoEmployeeId, today, 8.0);
        payrollService.clockOut(demoEmployeeId, today, 17.5);
        System.out.println("Clock in/out simulated for today: " + today);
    }

    private void runSubmissionApproval() throws Exception {
        ensureDemoEmployee();

        boolean submitted = payrollService.submitPayroll(demoEmployeeId, 1.0, 2.0, 500.0);
        System.out.println("Submission created: " + submitted);

        Submission submission = payrollService.getSubmission(demoEmployeeId);
        if (submission == null) {
            System.out.println("No submission found for demo employee.");
            return;
        }

        payrollService.updateSubmissionStatus(submission.id(), Submission.Status.APPROVED, currentCutoffPeriod);
        System.out.println("Submission approved for cutoff " + currentCutoffPeriod + ".");
    }

    private void buildPayrollEntry() throws Exception {
        ensureDemoEmployee();
        lastEntry = payrollService.buildPayrollEntry(demoEmployeeId, currentCutoffPeriod);
        if (lastEntry == null) {
            System.out.println("Payroll entry could not be built.");
            return;
        }

        System.out.println();
        System.out.println("Payroll entry built:");
        printPayrollEntry(lastEntry);
    }

    private void printReportSummary() throws Exception {
        List<PayrollReportEntry> entries = reportService.getReportByPeriod(currentCutoffPeriod);
        double[] totals = reportService.computePeriodTotals(entries);

        System.out.println();
        System.out.println("Payroll report summary for " + currentCutoffPeriod + ":");
        System.out.println("- Entries: " + entries.size());
        System.out.println("- Total Gross: " + formatPeso(totals[0]));
        System.out.println("- Total Deductions: " + formatPeso(totals[1]));
        System.out.println("- Total Net: " + formatPeso(totals[2]));
    }

    private void ensureDemoEmployee() throws Exception {
        if (payrollService.findEmployee(demoEmployeeId) == null) {
            registerDemoEmployee();
        }
    }

    private void printPayrollEntry(PayrollEntry entry) {
        System.out.println("Employee: " + entry.employee().getEmployeeId() + " / " + entry.employee().getName());
        System.out.println("Cut-off: " + entry.cutOffPeriod());
        System.out.println("Total Hours: " + String.format("%.2f", entry.totalHoursWorked()));
        System.out.println("OT Hours: " + String.format("%.2f", entry.overtimeHours()));
        System.out.println("Undertime Hours: " + String.format("%.2f", entry.undertimeHours()));
        System.out.println("Absent Days: " + entry.absentDays());
        System.out.println("Basic Pay: " + formatPeso(entry.basicPay()));
        System.out.println("OT Pay: " + formatPeso(entry.overtimePay()));
        System.out.println("Holiday Pay: " + formatPeso(entry.holidayPay()));
        System.out.println("NSD: " + formatPeso(entry.nightShiftDifferential()));
        System.out.println("Gross Pay: " + formatPeso(entry.grossPay()));
        System.out.println("SSS: " + formatPeso(entry.sssDeduction()));
        System.out.println("PhilHealth: " + formatPeso(entry.philhealthDeduction()));
        System.out.println("Pag-IBIG: " + formatPeso(entry.pagibigDeduction()));
        System.out.println("Tax: " + formatPeso(entry.taxDeduction()));
        System.out.println("Loan: " + formatPeso(entry.loanDeduction()));
        System.out.println("Undertime Penalty: " + formatPeso(entry.undertimePenalty()));
        System.out.println("Absence Penalty: " + formatPeso(entry.absencePenalty()));
        System.out.println("Net Pay: " + formatPeso(entry.netPay()));
    }

    private static String currentCutoffPeriod() {
        return LocalDate.now().getDayOfMonth() <= 15 ? "1st-15th" : "16th-30th";
    }

    private static LocalDate cutoffStart(LocalDate now, String cutoffPeriod) {
        YearMonth month = YearMonth.of(now.getYear(), now.getMonth());
        return cutoffPeriod.equals("1st-15th") ? month.atDay(1) : month.atDay(16);
    }

    private static LocalDate cutoffEnd(LocalDate now, String cutoffPeriod) {
        YearMonth month = YearMonth.of(now.getYear(), now.getMonth());
        return cutoffPeriod.equals("1st-15th") ? month.atDay(15) : month.atEndOfMonth();
    }

    private static boolean isWeekday(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY;
    }

    private static LocalDate lastMondayOfAugust(int year) {
        LocalDate date = LocalDate.of(year, Month.AUGUST, 31);
        while (date.getDayOfWeek() != DayOfWeek.MONDAY) {
            date = date.minusDays(1);
        }
        return date;
    }

    private static String formatPeso(double amount) {
        return String.format("PHP %,.2f", amount);
    }
}