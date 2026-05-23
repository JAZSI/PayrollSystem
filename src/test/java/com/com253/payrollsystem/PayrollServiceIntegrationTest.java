package com.com253.payrollsystem;

import com.com253.payrollsystem.Model.Employee;
import com.com253.payrollsystem.Model.Employee.EmployeeType;
import com.com253.payrollsystem.Model.EndUser;
import com.com253.payrollsystem.Model.PayrollEntry;
import com.com253.payrollsystem.Model.Submission;
import com.com253.payrollsystem.Service.PayrollReportService;
import com.com253.payrollsystem.Service.PayrollService;
import com.com253.payrollsystem.Util.Database;
import org.junit.jupiter.api.*;

/**
 * Integration tests for PayrollService and PayrollReportService.
 * Requires a live SQLite database — tagged so they run separately.
 *
 * Run with: mvn test -Dgroups=integration
 * Or skip:  mvn test -Dgroups=!integration
 */
@Tag("integration")
class PayrollServiceIntegrationTest {

    private static final PayrollService SERVICE = new PayrollService();
    private static final PayrollReportService REPORT_SERVICE = new PayrollReportService();
    private static final String TEST_EMP_ID = "TEST-001";
    private static final String TEST_PERIOD = "1st-15th";

    @BeforeAll
    static void init() throws Exception {
        Database.wipeAndReinitialize();
        // Admin account seeded by wipeAndReinitialize() for auth tests
    }

    @BeforeEach
    void setup() throws Exception {
        cleanup();
    }

    @AfterEach
    void teardown() throws Exception {
        cleanup();
    }

    private void cleanup() {
        try {
            SERVICE.deleteEmployee(TEST_EMP_ID);
        } catch (Exception e) {
            // ignore if not found
        }
    }

    // --- Authentication ---

    @Test
    void testLoginSuccess() throws Exception {
        EndUser user = SERVICE.authenticate("admin", "admin123");
        Assertions.assertNotNull(user, "default admin login succeeds");
        Assertions.assertEquals("admin", user.getUsername());
    }

    @Test
    void testLoginWrongPassword() throws Exception {
        EndUser user = SERVICE.authenticate("admin", "wrongpassword");
        Assertions.assertNull(user, "wrong password rejected");
    }

    @Test
    void testLoginUserNotFound() throws Exception {
        EndUser user = SERVICE.authenticate("nobody", "password");
        Assertions.assertNull(user, "unknown user rejected");
    }

    // --- Submission workflow ---

    @Test
    void testSubmitPayroll() throws Exception {
        registerTestEmployee();

        SERVICE.submitPayroll(TEST_EMP_ID, 2.0, 5.0, 500.0);
        Submission sub = SERVICE.getSubmission(TEST_EMP_ID);
        Assertions.assertNotNull(sub, "submission exists");
        Assertions.assertEquals(Submission.Status.PENDING, sub.status());
    }

    @Test
    void testApproveAndDeduct() throws Exception {
        registerTestEmployee();
        SERVICE.submitPayroll(TEST_EMP_ID, 2.0, 0.0, 0.0);

        SERVICE.updateSubmissionStatus(
                SERVICE.getSubmission(TEST_EMP_ID).id(),
                Submission.Status.APPROVED, TEST_PERIOD);

        Submission sub = SERVICE.getSubmission(TEST_EMP_ID);
        Assertions.assertEquals(Submission.Status.APPROVED, sub.status());
    }

    @Test
    void testRejectSubmission() throws Exception {
        registerTestEmployee();
        SERVICE.submitPayroll(TEST_EMP_ID, 3.0, 0.0, 0.0);

        SERVICE.updateSubmissionStatus(
                SERVICE.getSubmission(TEST_EMP_ID).id(),
                Submission.Status.REJECTED, TEST_PERIOD);

        Submission sub = SERVICE.getSubmission(TEST_EMP_ID);
        Assertions.assertEquals(Submission.Status.REJECTED, sub.status());
    }

    @Test
    void testFullPayrollFlow() throws Exception {
        registerTestEmployee();
        SERVICE.submitPayroll(TEST_EMP_ID, 0.0, 0.0, 0.0);
        SERVICE.updateSubmissionStatus(
                SERVICE.getSubmission(TEST_EMP_ID).id(),
                Submission.Status.APPROVED, TEST_PERIOD);

        PayrollEntry entry = SERVICE.buildPayrollEntry(TEST_EMP_ID, TEST_PERIOD);
        Assertions.assertNotNull(entry, "payroll entry built from approved submission");
        Assertions.assertTrue(entry.basicPay() > 0, "basic pay computed");
        Assertions.assertTrue(entry.netPay() > 0, "net pay computed");
    }

    @Test
    void testReportByPeriod() throws Exception {
        // Setup payslip data first
        registerTestEmployee();
        SERVICE.submitPayroll(TEST_EMP_ID, 0.0, 0.0, 0.0);
        SERVICE.updateSubmissionStatus(
                SERVICE.getSubmission(TEST_EMP_ID).id(),
                Submission.Status.APPROVED, TEST_PERIOD);
        SERVICE.buildPayrollEntry(TEST_EMP_ID, TEST_PERIOD);

        var entries = REPORT_SERVICE.getReportByPeriod(TEST_PERIOD);
        Assertions.assertFalse(entries.isEmpty(), "entries found for period");

        double[] totals = REPORT_SERVICE.computePeriodTotals(entries);
        Assertions.assertEquals(3, totals.length);
        Assertions.assertTrue(totals[0] > 0, "total gross > 0");
        Assertions.assertTrue(totals[2] > 0, "total net > 0");
    }

    @Test
    void testCsvExport() throws Exception {
        registerTestEmployee();
        SERVICE.submitPayroll(TEST_EMP_ID, 0.0, 0.0, 0.0);
        SERVICE.updateSubmissionStatus(
                SERVICE.getSubmission(TEST_EMP_ID).id(),
                Submission.Status.APPROVED, TEST_PERIOD);
        SERVICE.buildPayrollEntry(TEST_EMP_ID, TEST_PERIOD);

        var entries = REPORT_SERVICE.getAllReports();
        // Just verify we can get all reports without exception
        Assertions.assertNotNull(entries);
    }

    private void registerTestEmployee() throws Exception {
        Employee emp = new Employee(TEST_EMP_ID, "Test Employee",
                EmployeeType.REGULAR, 30000.0, 0.0, true,
                new com.com253.payrollsystem.Model.LeaveBalance(5, 10, 3),
                new com.com253.payrollsystem.Model.LoanBalance(3000.0));
        SERVICE.registerEmployee(emp, "testuser", "testpass");
    }
}