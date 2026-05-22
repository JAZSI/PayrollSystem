package com.com253.payrollsystem;

import com.com253.payrollsystem.Model.Employee;
import com.com253.payrollsystem.Model.Employee.EmployeeType;
import com.com253.payrollsystem.Model.EndUser;
import com.com253.payrollsystem.Model.Submission;
import com.com253.payrollsystem.Service.PayrollReportService;
import com.com253.payrollsystem.Util.Database;
import java.time.LocalDate;
import com.com253.payrollsystem.Model.Employee;
import com.com253.payrollsystem.Model.LeaveBalance;
import com.com253.payrollsystem.Model.LoanBalance;
import com.com253.payrollsystem.Model.PayrollEntry;
import com.com253.payrollsystem.Model.PayrollSettings;
import com.com253.payrollsystem.Model.TimeRecord;
import com.com253.payrollsystem.Service.PayrollCalculator;
import com.com253.payrollsystem.Service.PayrollService;

public class Test {

    private static final PayrollSettings SETTINGS = new PayrollSettings(26, 8.0, 17.0, 11.0);
    private static final PayrollService SERVICE = new PayrollService();
    private static final PayrollReportService REPORT_SERVICE = new PayrollReportService();
    private static final String TEST_EMP_ID = "TEST-001";
    private static final String TEST_PERIOD = "1st-15th";

    public static void main(String[] args) throws Exception {
        Database.initialize();

        // Unit tests (pure computation)
        testLoginSuccess();
        testLoginWrongPassword();
        testLoginUserNotFound();
        testBaseline();
        testOvertime();
        testRegularHoliday();
        testRestDayHoliday();
        testNightShiftDifferential();
        testUndertime();
        testAbsence();
        testPartTimer();
        testProbationary();
        testContractual();
        testLoanDeduction();

        // Integration tests (DB)
        testSubmitPayroll();
        testApproveAndDeduct();
        testRejectSubmission();
        testFullPayrollFlow();
        testReportByPeriod();
        testCsvExport();
        testAttendanceCrud();
        testAttendanceUpsert();
        testShortShift();

        System.out.println("\n=== ALL TESTS COMPLETE ===");
    }

    // --- Authentication Tests ---

    private static void testLoginSuccess() throws Exception {
        System.out.println("\n=== LOGIN: SUCCESS ===");
        // Default admin account: username="admin", password="admin123"
        EndUser user = SERVICE.authenticate("admin", "admin123");
        if (user != null) {
            System.out.println("Login successful!");
            System.out.println("Username: " + user.getUsername());
            System.out.println("Role: " + user.getRole());
            System.out.println("Employee ID: " + (user.getLinkedEmployeeId() == null ? "N/A (admin)" : user.getLinkedEmployeeId()));
        } else {
            System.out.println("FAILED: Expected successful login");
        }
    }

    private static void testLoginWrongPassword() throws Exception {
        System.out.println("\n=== LOGIN: WRONG PASSWORD ===");
        EndUser user = SERVICE.authenticate("admin", "wrongpassword");
        if (user == null) {
            System.out.println("Login correctly rejected for wrong password");
        } else {
            System.out.println("FAILED: Should have been rejected");
        }
    }

    private static void testLoginUserNotFound() throws Exception {
        System.out.println("\n=== LOGIN: USER NOT FOUND ===");
        EndUser user = SERVICE.authenticate("nonexistent", "password");
        if (user == null) {
            System.out.println("Login correctly rejected for unknown user");
        } else {
            System.out.println("FAILED: Should have been rejected");
        }
    }

    // --- Payroll Calculator Tests ---

    // Baseline: 15 full days, Regular employee, no extras
    private static void testBaseline() {
        System.out.println("\n=== BASELINE TEST ===");
        System.out.println("Regular employee, 15 days x 8:00-17:00, no OT, no leave, no loan\n");

        Employee emp = new Employee("EMP001", "Juan dela Cruz", 
                EmployeeType.REGULAR,
                30000.00, 0.0, true, 
                new LeaveBalance(0, 0, 0), 
                new LoanBalance(0.0));

        TimeRecord[] records = buildRecords(15, 800, 1700, false, TimeRecord.HOLIDAY_NONE);

        PayrollEntry entry = PayrollCalculator.buildPayrollEntry(
                emp, records, "1st-15th", 0.0, SETTINGS);

        printPayslip(emp, entry);

        // Verify expected values
        if (entry.getOvertimeHours() == 0 && entry.getHolidayPay() == 0 && entry.getBasicPay() == 15000.0) {
            System.out.println("PASSED: Baseline values correct");
        } else {
            System.out.println("FAILED: OT=" + entry.getOvertimeHours() + " Holiday=" + entry.getHolidayPay() + " Basic=" + entry.getBasicPay());
        }
    }

    // OT: employee works past 5 PM
    private static void testOvertime() {
        System.out.println("\n=== OVERTIME TEST ===");
        System.out.println("Regular employee, 15 days x 8:00-18:00 (1hr OT each day)\n");

        Employee emp = new Employee("EMP002", "Maria Santos", 
                EmployeeType.REGULAR,
                30000.00, 0.0, true, 
                new LeaveBalance(0, 0, 0), 
                new LoanBalance(0.0));

        // 15 days + 1 hour OT each
        TimeRecord[] records = buildRecords(15, 800, 1800, false, TimeRecord.HOLIDAY_NONE);

        PayrollEntry entry = PayrollCalculator.buildPayrollEntry(
                emp, records, "1st-15th", 0.0, SETTINGS);

        printPayslip(emp, entry);

        // Expected: 15 OT hours (1hr x 15 days)
        System.out.println("OT Hours: " + entry.getOvertimeHours()); // should be 15.0
        System.out.println("OT Pay: Php " + entry.getOvertimePay()); // should be > 0
    }

    // Regular holiday: 200% pay for hours worked
    private static void testRegularHoliday() {
        System.out.println("\n=== REGULAR HOLIDAY TEST ===");
        System.out.println("Regular employee, 1 regular holiday worked\n");

        Employee emp = new Employee("EMP003", "Pedro Cruz", 
                EmployeeType.REGULAR,
                30000.00, 0.0, true, 
                new LeaveBalance(0, 0, 0), 
                new LoanBalance(0.0));

        TimeRecord[] records = buildRecords(15, 800, 1700, false, TimeRecord.HOLIDAY_NONE);
        records[0] = new TimeRecord(1, 800, 1700, false, TimeRecord.HOLIDAY_REGULAR);

        PayrollEntry entry = PayrollCalculator.buildPayrollEntry(
                emp, records, "1st-15th", 0.0, SETTINGS);

        printPayslip(emp, entry);

        // Expected: 8 hours at 200% = 1 extra day's pay as holiday premium
        System.out.println("Holiday Pay: Php " + entry.getHolidayPay()); // should be > 0
    }

    // Night Shift Differential: 10% extra for hours between 10 PM - 6 AM
    private static void testNightShiftDifferential() {
        System.out.println("\n=== NIGHT SHIFT DIFFERENTIAL TEST ===");
        System.out.println("Regular employee, 1 day clocked in at 22:00, out at 06:00\n");

        Employee emp = new Employee("EMP004", "Ana Reyes", 
                EmployeeType.REGULAR,
                30000.00, 0.0, true, 
                new LeaveBalance(0, 0, 0), 
                new LoanBalance(0.0));

        TimeRecord[] records = buildRecords(15, 800, 1700, false, TimeRecord.HOLIDAY_NONE);
        // 10 PM (22:00) to 6 AM next day (6:00) = 8 hours NSD
        records[0] = new TimeRecord(1, 2200, 600, false, TimeRecord.HOLIDAY_NONE);

        PayrollEntry entry = PayrollCalculator.buildPayrollEntry(
                emp, records, "1st-15th", 0.0, SETTINGS);

        printPayslip(emp, entry);

        // Expected: 8 hours of NSD at 10% extra rate
        System.out.println("NSD: Php " + entry.getNightShiftDifferential()); // should be > 0
    }

    // PartTimer: paid hourly, no monthly rate
    private static void testPartTimer() {
        System.out.println("\n=== PART-TIMER TEST ===");
        System.out.println("Part-time employee, 80 hours worked at Php 200/hr\n");

        // PartTimer uses hourly rate, not monthly
        Employee pt = new Employee("EMP005", "Lito Lim", 
                EmployeeType.PARTTIMER,
                0.0, 200.0, false, 
                new LeaveBalance(0, 0, 0), 
                new LoanBalance(0.0));

        TimeRecord[] records = buildRecords(10, 800, 1700, false, TimeRecord.HOLIDAY_NONE);

        PayrollEntry entry = PayrollCalculator.buildPayrollEntry(
                pt, records, "1st-15th", 0.0, SETTINGS);

        printPayslip(pt, entry);

        // Expected: basic pay = hours worked * hourly rate
        System.out.println("Basic Pay (Hourly): Php " + entry.getBasicPay()); // 80 hrs * 200 = 16000
        System.out.println("Expected: Php 16000.00 (80 hrs x Php 200/hr)");
    }

    // Undertime: employee leaves early (1 hour early each day)
    private static void testUndertime() {
        System.out.println("\n=== UNDERTIME TEST ===");
        System.out.println("Regular employee, 15 days x 8:00-16:00 (1hr undertime each day)\n");

        Employee emp = new Employee("EMP006", "Luis Torres",
                EmployeeType.REGULAR,
                30000.00, 0.0, true,
                new LeaveBalance(0, 0, 0),
                new LoanBalance(0.0));

        // 15 days, 1 hour undertime each (8:00-16:00 instead of 8:00-17:00)
        TimeRecord[] records = buildRecords(15, 800, 1600, false, TimeRecord.HOLIDAY_NONE);

        PayrollEntry entry = PayrollCalculator.buildPayrollEntry(
                emp, records, "1st-15th", 0.0, SETTINGS);

        printPayslip(emp, entry);

        // Expected: 15 undertime hours (1hr x 15 days)
        System.out.println("Total Undertime: " + entry.getUndertimeHours() + " hours");
        System.out.println("Undertime Penalty: Php " + entry.getUndertimePenalty());
    }

    // Absence: employee is absent for 2 days (no leave credits)
    private static void testAbsence() {
        System.out.println("\n=== ABSENCE TEST (No Leave Credits) ===");
        System.out.println("Regular employee, 2 absent days (no leave balance)\n");

        Employee emp = new Employee("EMP007", "Elena Dizon",
                EmployeeType.REGULAR,
                30000.00, 0.0, true,
                new LeaveBalance(0, 0, 0),
                new LoanBalance(0.0));

        TimeRecord[] records = buildRecords(15, 800, 1700, false, TimeRecord.HOLIDAY_NONE);
        records[0] = new TimeRecord(1, 800, 1700, true, TimeRecord.HOLIDAY_NONE);  // absent
        records[1] = new TimeRecord(2, 800, 1700, true, TimeRecord.HOLIDAY_NONE);  // absent

        PayrollEntry entry = PayrollCalculator.buildPayrollEntry(
                emp, records, "1st-15th", 0.0, SETTINGS);

        printPayslip(emp, entry);

        // Expected: 2 absent days charged
        System.out.println("Absent Days: " + entry.getAbsentDays());
        System.out.println("Absence Penalty: Php " + entry.getAbsencePenalty());
    }

    // Rest day / special non-working holiday: 130% pay
    private static void testRestDayHoliday() {
        System.out.println("\n=== SPECIAL DAY / REST DAY HOLIDAY TEST ===");
        System.out.println("Regular employee, 1 rest day worked\n");

        Employee emp = new Employee("EMP008", "Rico Miranda",
                EmployeeType.REGULAR,
                30000.00, 0.0, true,
                new LeaveBalance(0, 0, 0),
                new LoanBalance(0.0));

        TimeRecord[] records = buildRecords(15, 800, 1700, false, TimeRecord.HOLIDAY_NONE);
        records[0] = new TimeRecord(1, 800, 1700, false, TimeRecord.HOLIDAY_REST_DAY);

        PayrollEntry entry = PayrollCalculator.buildPayrollEntry(
                emp, records, "1st-15th", 0.0, SETTINGS);

        printPayslip(emp, entry);

        // Expected: 8 hours at 130% = additional 30% as rest day premium
        System.out.println("Holiday Pay (Rest Day): Php " + entry.getHolidayPay());
    }

    // Probationary employee: same formula as Regular
    private static void testProbationary() {
        System.out.println("\n=== PROBATIONARY EMPLOYEE TEST ===");
        System.out.println("Probationary employee, 15 days x 8:00-17:00\n");

        Employee emp = new Employee("EMP009", "Mark Aquino",
                EmployeeType.PROBATIONARY,
                25000.00, 0.0, true,
                new LeaveBalance(3, 5, 2),
                new LoanBalance(0.0));

        TimeRecord[] records = buildRecords(15, 800, 1700, false, TimeRecord.HOLIDAY_NONE);

        PayrollEntry entry = PayrollCalculator.buildPayrollEntry(
                emp, records, "1st-15th", 0.0, SETTINGS);

        printPayslip(emp, entry);

        // Expected: same computation as Regular, just different rate
        System.out.println("Basic Pay should be: 25000 / 2 = Php 12500.00");
    }

    // Contractual employee: same formula as Regular
    private static void testContractual() {
        System.out.println("\n=== CONTRACTUAL EMPLOYEE TEST ===");
        System.out.println("Contractual employee, 15 days x 8:00-17:00\n");

        Employee emp = new Employee("EMP010", "Sofia Reyes",
                EmployeeType.CONTRACTUAL,
                28000.00, 0.0, true,
                new LeaveBalance(0, 0, 0),
                new LoanBalance(0.0));

        TimeRecord[] records = buildRecords(15, 800, 1700, false, TimeRecord.HOLIDAY_NONE);

        PayrollEntry entry = PayrollCalculator.buildPayrollEntry(
                emp, records, "1st-15th", 0.0, SETTINGS);

        printPayslip(emp, entry);

        // Expected: same computation as Regular, just different rate
        System.out.println("Basic Pay should be: 28000 / 2 = Php 14000.00");
    }

    // Loan deduction: Php 1000 deducted from net pay
    private static void testLoanDeduction() {
        System.out.println("\n=== LOAN DEDUCTION TEST ===");
        System.out.println("Regular employee with Php 1000 loan deduction\n");

        Employee emp = new Employee("EMP011", "Nina Cruz",
                EmployeeType.REGULAR,
                30000.00, 0.0, true,
                new LeaveBalance(0, 0, 0),
                new LoanBalance(5000.0));

        TimeRecord[] records = buildRecords(15, 800, 1700, false, TimeRecord.HOLIDAY_NONE);

        PayrollEntry entry = PayrollCalculator.buildPayrollEntry(
                emp, records, "1st-15th", 1000.0, SETTINGS);  // Php 1000 loan deduction

        printPayslip(emp, entry);

        // Expected: loan deduction = 1000, deducted from net pay
        System.out.println("Loan Deduction Applied: Php " + entry.getLoanDeduction());
        System.out.println("Net Pay should be less by Php 1000");
    }

    // Helper: build array of identical time records
    private static TimeRecord[] buildRecords(int days, int timeIn, int timeOut,
            boolean absent, String holiday) {
        TimeRecord[] records = new TimeRecord[days];
        for (int i = 0; i < days; i++) {
            records[i] = new TimeRecord(i + 1, timeIn, timeOut, absent, holiday);
        }
        return records;
    }

    // Print payroll summary
    private static void printPayslip(Employee emp, PayrollEntry entry) {
        System.out.println("========================================");
        System.out.println("Employee: " + emp.getName() + " (" + emp.getTypeName() + ")");
        System.out.println("Period:   " + entry.getCutOffPeriod());
        System.out.println("----------------------------------------");
        System.out.printf("Total Hours:     %10.2f%n", entry.getTotalHoursWorked());
        System.out.printf("OT Hours:       %10.2f%n", entry.getOvertimeHours());
        System.out.printf("Undertime Hrs:  %10.2f%n", entry.getUndertimeHours());
        System.out.printf("Absent Days:    %10d%n", entry.getAbsentDays());
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
        System.out.println("========================================\n");
    }

    // ============================================================
    // INTEGRATION TESTS (Database)
    // ============================================================

    private static void setupTestEmployee() throws Exception {
        System.out.println("\n=== SETUP: Register Test Employee ===");
        SERVICE.deleteSubmission(TEST_EMP_ID);
        try {
            SERVICE.deleteEmployee(TEST_EMP_ID);
        } catch (Exception e) {}
        Employee emp = new Employee(TEST_EMP_ID, "Test Employee",
                EmployeeType.REGULAR,
                30000.00, 0.0, true,
                new LeaveBalance(5, 10, 3),
                new LoanBalance(3000.0));
        SERVICE.registerEmployee(emp, "testuser", "testpass");
        System.out.println("Registered: " + TEST_EMP_ID + " (testuser/testpass)");
        System.out.println("Leave: sick=5, vacation=10, emergency=3 | Loan: 3000.0");
    }

    private static void testSubmitPayroll() throws Exception {
        System.out.println("\n=== SUBMISSION: File Payroll ===");
        setupTestEmployee();

        boolean submitted = SERVICE.submitPayroll(TEST_EMP_ID, 2.0, 5.0, 500.0);
        if (submitted) {
            System.out.println("Submission filed! Leave=2, OT=5hrs, Loan=500");
        } else {
            System.out.println("FAILED: Could not file");
        }

        Submission sub = SERVICE.getSubmission(TEST_EMP_ID);
        if (sub != null) System.out.println("Status: " + sub.getStatus());
    }

    private static void testApproveAndDeduct() throws Exception {
        System.out.println("\n=== APPROVAL: Approve & Deduct ===");

        Employee empBefore = SERVICE.findEmployee(TEST_EMP_ID);
        System.out.println("BEFORE: Sick=" + empBefore.getLeaveBalance().getSick()
                + " Loan=" + empBefore.getLoanBalance().getBalance());

        SERVICE.updateSubmissionStatus(
            SERVICE.getSubmission(TEST_EMP_ID).getId(),
            Submission.Status.APPROVED, TEST_PERIOD);
        System.out.println("Submission APPROVED");

        Employee empAfter = SERVICE.findEmployee(TEST_EMP_ID);
        System.out.println("AFTER: Sick=" + empAfter.getLeaveBalance().getSick()
                + " Loan=" + empAfter.getLoanBalance().getBalance());

        System.out.println("Leave transactions: " + SERVICE.getLeaveHistory(TEST_EMP_ID).size());
        System.out.println("Loan transactions: " + SERVICE.getLoanHistory(TEST_EMP_ID).size());
    }

    private static void testRejectSubmission() throws Exception {
        System.out.println("\n=== REJECTION: Reject Submission ===");
        SERVICE.deleteSubmission(TEST_EMP_ID);
        try { SERVICE.deleteEmployee(TEST_EMP_ID); } catch (Exception e) {}

        SERVICE.registerEmployee(
        new Employee(TEST_EMP_ID, "Test",
                EmployeeType.REGULAR,
                30000.00, 0.0, true,
                new LeaveBalance(5, 10, 3),
                new LoanBalance(3000.0)),
                "testuser2", "testpass");

        boolean filed = SERVICE.submitPayroll(TEST_EMP_ID, 3.0, 0.0, 0.0);
        if (!filed) {
            System.out.println("FAILED: Could not file (existing approved submission)");
            return;
        }

        SERVICE.updateSubmissionStatus(
            SERVICE.getSubmission(TEST_EMP_ID).getId(),
            Submission.Status.REJECTED, TEST_PERIOD);

        System.out.println("Rejection complete. Status: " + SERVICE.getSubmission(TEST_EMP_ID).getStatus());
    }

    private static void testFullPayrollFlow() throws Exception {
        System.out.println("\n=== FULL FLOW: Build Payroll Entry ===");
        SERVICE.deleteEmployee(TEST_EMP_ID);
        SERVICE.registerEmployee(
        new Employee(TEST_EMP_ID, "Test",
                EmployeeType.REGULAR,
                30000.00, 0.0, true,
                new LeaveBalance(0, 0, 0),
                new LoanBalance(0.0)),
                "testuser", "testpass");

        SERVICE.submitPayroll(TEST_EMP_ID, 0.0, 0.0, 0.0);
        SERVICE.updateSubmissionStatus(
            SERVICE.getSubmission(TEST_EMP_ID).getId(),
            Submission.Status.APPROVED, TEST_PERIOD);

        PayrollEntry entry = SERVICE.buildPayrollEntry(TEST_EMP_ID, TEST_PERIOD);
        if (entry != null) {
            System.out.println("Payroll built! Basic=" + entry.getBasicPay()
                    + " Gross=" + entry.getGrossPay() + " Net=" + entry.getNetPay());
        } else {
            System.out.println("FAILED: null entry");
        }
    }

    private static void testReportByPeriod() throws Exception {
        System.out.println("\n=== REPORT: Query by Period ===");
        var entries = REPORT_SERVICE.getReportByPeriod(TEST_PERIOD);
        System.out.println("Entries: " + entries.size());
        var totals = REPORT_SERVICE.computePeriodTotals(entries);
        System.out.printf("Totals - Gross: %.2f | Net: %.2f%n", totals[0], totals[2]);
    }

    private static void testCsvExport() throws Exception {
        System.out.println("\n=== REPORT: CSV Format Demo ===");
        var entries = REPORT_SERVICE.getAllReports();
        System.out.println("Records: " + entries.size());
        if (!entries.isEmpty()) {
            System.out.println("CSV Header: Employee ID,Name,Period,Total Hours,OT Hours,Undertime Hours,"
                    + "Absent Days,Basic Pay,OT Pay,Holiday Pay,NSD,Gross Pay,"
                    + "SSS,PhilHealth,Pag-IBIG,Tax,Loan,Undertime Penalty,Absence Penalty,Net Pay");
        }
    }

    // Attendance CRUD
    private static void testAttendanceCrud() throws Exception {
        System.out.println("\n=== ATTENDANCE: CRUD Operations ===");
        LocalDate today = LocalDate.now();

        // Add clock-in
        SERVICE.upsertAttendance(TEST_EMP_ID, today, 8.0, null);
        System.out.println("Added clock-in: employee=" + TEST_EMP_ID + " date=" + today + " timeIn=8.0");

        // Update: add clock-out (correct a mistake)
        SERVICE.updateTimeOut(TEST_EMP_ID, today, 17.5);
        System.out.println("Updated clock-out: timeOut=17.5");

        // Update: correct clock-in
        SERVICE.updateTimeIn(TEST_EMP_ID, today, 8.5);
        System.out.println("Updated clock-in: timeIn=8.5");

        // Verify
        var records = SERVICE.getAttendanceHistory(TEST_EMP_ID, today, today);
        if (!records.isEmpty()) {
            var rec = records.get(0);
            System.out.println("Verified: timeIn=" + rec.getTimeIn() + " timeOut=" + rec.getTimeOut());
        }

        // Delete
        SERVICE.deleteAttendance(TEST_EMP_ID, today);
        System.out.println("Deleted record for: " + today);
    }

    private static void testAttendanceUpsert() throws Exception {
        System.out.println("\n=== ATTENDANCE: Upsert (Add New & Update) ===");
        LocalDate testDate = LocalDate.now().minusDays(1);

        // Upsert new record
        SERVICE.upsertAttendance(TEST_EMP_ID, testDate, 9.0, 18.0);
        System.out.println("Upserted new: " + testDate + " in=9.0 out=18.0");

        // Upsert again (update existing)
        SERVICE.upsertAttendance(TEST_EMP_ID, testDate, 8.5, 17.5);
        System.out.println("Upserted (update): " + testDate + " in=8.5 out=17.5");

        var records = SERVICE.getAttendanceHistory(TEST_EMP_ID, testDate, testDate);
        if (!records.isEmpty()) {
            System.out.println("Fetched: timeIn=" + records.get(0).getTimeIn() + " timeOut=" + records.get(0).getTimeOut());
        }
    }

    private static void testShortShift() {
        System.out.println("\n=== SHORT SHIFT RULE (<1 hour = 0 pay, no penalty) ===");

        // 8:00 AM to 8:30 AM = ~0.5 hours worked → should return 0
        TimeRecord shortRecord = new TimeRecord(1, 800, 830, false, TimeRecord.HOLIDAY_NONE);
        TimeRecord[] records = {shortRecord};

        double hours = PayrollCalculator.computeHoursWorked(shortRecord, SETTINGS);
        double undertime = PayrollCalculator.computeUndertimeHours(records, SETTINGS);
        int absent = PayrollCalculator.computeAbsentDays(records);

        boolean hoursOk = hours == 0.0;
        boolean undertimeOk = undertime == 0.0;
        boolean absentOk = absent == 0;

        System.out.println("computeHoursWorked(8:00-8:30): " + hours + " == 0.0 → " + (hoursOk ? "PASS" : "FAIL"));
        System.out.println("computeUndertimeHours: " + undertime + " == 0.0 → " + (undertimeOk ? "PASS" : "FAIL"));
        System.out.println("computeAbsentDays: " + absent + " == 0 → " + (absentOk ? "PASS" : "FAIL"));

        // Just-under 1 hour: 8:00 to 8:55 = ~0.92 hrs → 0
        TimeRecord almost = new TimeRecord(2, 800, 855, false, TimeRecord.HOLIDAY_NONE);
        double almostHours = PayrollCalculator.computeHoursWorked(almost, SETTINGS);
        boolean almostOk = almostHours == 0.0;
        System.out.println("computeHoursWorked(8:00-8:55): " + almostHours + " == 0.0 → " + (almostOk ? "PASS" : "FAIL"));

        // Exactly 1 hour: 8:00 to 9:00 = 1.0 hrs → 1.0 (no lunch, outHours <= lunchBreakStart)
        TimeRecord exactlyOne = new TimeRecord(3, 800, 900, false, TimeRecord.HOLIDAY_NONE);
        double exactlyOneHours = PayrollCalculator.computeHoursWorked(exactlyOne, SETTINGS);
        boolean exactlyOneOk = exactlyOneHours == 1.0;
        System.out.println("computeHoursWorked(8:00-9:00): " + exactlyOneHours + " == 1.0 → " + (exactlyOneOk ? "PASS" : "FAIL"));
    }
}