package com.com253.payrollsystem;

import com.com253.payrollsystem.Model.Employee;
import com.com253.payrollsystem.Model.Employee.EmployeeType;
import com.com253.payrollsystem.Model.LeaveBalance;
import com.com253.payrollsystem.Model.LoanBalance;
import com.com253.payrollsystem.Model.PayrollEntry;
import com.com253.payrollsystem.Model.PayrollSettings;
import com.com253.payrollsystem.Model.TimeRecord;
import com.com253.payrollsystem.Service.PayrollCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pure unit tests for PayrollCalculator.
 * No database — only deterministic salary computation.
 */
class PayrollCalculatorTest {

    private static final PayrollSettings SETTINGS = new PayrollSettings(26, 8.0, 17.0, 11.0);

    // Baseline: 15 full working days, Regular, Php 30000/month, no leave, no loan
    @Test
    void testBaseline() {
        Employee emp = new Employee("EMP001", "Juan dela Cruz",
                EmployeeType.REGULAR, 30000.0, 0.0, true,
                new LeaveBalance(0, 0, 0), new LoanBalance(0.0));
        TimeRecord[] records = buildRecords(15, 800, 1700, false, TimeRecord.HOLIDAY_NONE);

        PayrollEntry entry = PayrollCalculator.buildPayrollEntry(
                emp, records, "1st-15th", 0.0, SETTINGS);

        assertEquals(0.0, entry.overtimeHours(), "no overtime");
        assertEquals(0.0, entry.holidayPay(), "no holidays worked");
        assertEquals(15000.0, entry.basicPay(), 0.01, "bi-monthly basic = 30000/2");
        assertEquals(120.0, entry.totalHoursWorked(), 0.01, "15 days × 8 hrs");
        assertEquals(0, entry.absentDays());
        assertTrue(entry.netPay() > 0, "positive net pay");
    }

    @Test
    void testOvertime() {
        Employee emp = new Employee("EMP002", "Maria Santos",
                EmployeeType.REGULAR, 30000.0, 0.0, true,
                new LeaveBalance(0, 0, 0), new LoanBalance(0.0));
        // 8:00-18:00 = 9 hrs/day → 1 hr OT each day
        TimeRecord[] records = buildRecords(15, 800, 1800, false, TimeRecord.HOLIDAY_NONE);

        PayrollEntry entry = PayrollCalculator.buildPayrollEntry(
                emp, records, "1st-15th", 0.0, SETTINGS);

        assertEquals(15.0, entry.overtimeHours(), 0.01, "1 hr OT × 15 days");
        assertTrue(entry.overtimePay() > 0, "OT pay should be charged");
    }

    @Test
    void testRegularHoliday() {
        Employee emp = new Employee("EMP003", "Pedro Cruz",
                EmployeeType.REGULAR, 30000.0, 0.0, true,
                new LeaveBalance(0, 0, 0), new LoanBalance(0.0));
        TimeRecord[] records = buildRecords(15, 800, 1700, false, TimeRecord.HOLIDAY_NONE);
        records[0] = new TimeRecord(1, 800, 1700, false, TimeRecord.HOLIDAY_REGULAR);

        PayrollEntry entry = PayrollCalculator.buildPayrollEntry(
                emp, records, "1st-15th", 0.0, SETTINGS);

        assertTrue(entry.holidayPay() > 0, "worked holiday should earn holiday premium");
    }

    @Test
    void testRestDayHoliday() {
        Employee emp = new Employee("EMP008", "Rico Miranda",
                EmployeeType.REGULAR, 30000.0, 0.0, true,
                new LeaveBalance(0, 0, 0), new LoanBalance(0.0));
        TimeRecord[] records = buildRecords(15, 800, 1700, false, TimeRecord.HOLIDAY_NONE);
        records[0] = new TimeRecord(1, 800, 1700, false, TimeRecord.HOLIDAY_REST_DAY);

        PayrollEntry entry = PayrollCalculator.buildPayrollEntry(
                emp, records, "1st-15th", 0.0, SETTINGS);

        assertTrue(entry.holidayPay() > 0, "rest day worked should earn 130% premium");
    }

    @Test
    void testNightShiftDifferential() {
        Employee emp = new Employee("EMP004", "Ana Reyes",
                EmployeeType.REGULAR, 30000.0, 0.0, true,
                new LeaveBalance(0, 0, 0), new LoanBalance(0.0));
        TimeRecord[] records = buildRecords(15, 800, 1700, false, TimeRecord.HOLIDAY_NONE);
        // 22:00 to 06:00 => 8 hrs of night shift differential
        records[0] = new TimeRecord(1, 2200, 600, false, TimeRecord.HOLIDAY_NONE);

        PayrollEntry entry = PayrollCalculator.buildPayrollEntry(
                emp, records, "1st-15th", 0.0, SETTINGS);

        assertTrue(entry.nightShiftDifferential() > 0, "NSD hours should earn 10% premium");
    }

    @Test
    void testPartTimer() {
        Employee pt = new Employee("EMP005", "Lito Lim",
                EmployeeType.PARTTIMER, 0.0, 200.0, false,
                new LeaveBalance(0, 0, 0), new LoanBalance(0.0));
        // 10 days × 8:00-17:00 = 80 hrs at Php 200/hr
        TimeRecord[] records = buildRecords(10, 800, 1700, false, TimeRecord.HOLIDAY_NONE);

        PayrollEntry entry = PayrollCalculator.buildPayrollEntry(
                pt, records, "1st-15th", 0.0, SETTINGS);

        assertEquals(16000.0, entry.basicPay(), 0.01, "80 hrs × 200/hr");
    }

    @Test
    void testUndertime() {
        Employee emp = new Employee("EMP006", "Luis Torres",
                EmployeeType.REGULAR, 30000.0, 0.0, true,
                new LeaveBalance(0, 0, 0), new LoanBalance(0.0));
        // 15 days × 8:00-16:00 = 8 hrs - 1 hr undertime/day
        TimeRecord[] records = buildRecords(15, 800, 1600, false, TimeRecord.HOLIDAY_NONE);

        PayrollEntry entry = PayrollCalculator.buildPayrollEntry(
                emp, records, "1st-15th", 0.0, SETTINGS);

        assertTrue(entry.undertimeHours() > 0, "undertime hours detected");
        assertTrue(entry.undertimePenalty() > 0, "undertime penalty charged");
    }

    @Test
    void testAbsence() {
        Employee emp = new Employee("EMP007", "Elena Dizon",
                EmployeeType.REGULAR, 30000.0, 0.0, true,
                new LeaveBalance(0, 0, 0), new LoanBalance(0.0));
        TimeRecord[] records = buildRecords(15, 800, 1700, false, TimeRecord.HOLIDAY_NONE);
        records[0] = new TimeRecord(1, 800, 1700, true, TimeRecord.HOLIDAY_NONE);
        records[1] = new TimeRecord(2, 800, 1700, true, TimeRecord.HOLIDAY_NONE);

        PayrollEntry entry = PayrollCalculator.buildPayrollEntry(
                emp, records, "1st-15th", 0.0, SETTINGS);

        assertEquals(2, entry.absentDays(), "2 absent days charged");
        assertTrue(entry.absencePenalty() > 0, "absence penalty applied");
    }

    @Test
    void testLoanDeduction() {
        Employee emp = new Employee("EMP011", "Nina Cruz",
                EmployeeType.REGULAR, 30000.0, 0.0, true,
                new LeaveBalance(0, 0, 0), new LoanBalance(5000.0));
        TimeRecord[] records = buildRecords(15, 800, 1700, false, TimeRecord.HOLIDAY_NONE);

        PayrollEntry base = PayrollCalculator.buildPayrollEntry(
                emp, records, "1st-15th", 0.0, SETTINGS);

        PayrollEntry withLoan = PayrollCalculator.buildPayrollEntry(
                emp, records, "1st-15th", 1000.0, SETTINGS);

        assertEquals(1000.0, withLoan.loanDeduction(), 0.01);
        double diff = base.netPay() - withLoan.netPay();
        assertEquals(1000.0, diff, 0.01, "net pay reduced by exactly loan amount");
    }

    @Test
    void testProbationary() {
        Employee emp = new Employee("EMP009", "Mark Aquino",
                EmployeeType.PROBATIONARY, 25000.0, 0.0, true,
                new LeaveBalance(3, 5, 2), new LoanBalance(0.0));
        TimeRecord[] records = buildRecords(15, 800, 1700, false, TimeRecord.HOLIDAY_NONE);

        PayrollEntry entry = PayrollCalculator.buildPayrollEntry(
                emp, records, "1st-15th", 0.0, SETTINGS);

        assertEquals(12500.0, entry.basicPay(), 0.01, "25000/2 bi-monthly rate");
    }

    @Test
    void testContractual() {
        Employee emp = new Employee("EMP010", "Sofia Reyes",
                EmployeeType.CONTRACTUAL, 28000.0, 0.0, true,
                new LeaveBalance(0, 0, 0), new LoanBalance(0.0));
        TimeRecord[] records = buildRecords(15, 800, 1700, false, TimeRecord.HOLIDAY_NONE);

        PayrollEntry entry = PayrollCalculator.buildPayrollEntry(
                emp, records, "1st-15th", 0.0, SETTINGS);

        assertEquals(14000.0, entry.basicPay(), 0.01, "28000/2 bi-monthly rate");
    }

    @Test
    void testShortShiftUnderOneHour() {
        TimeRecord shortRecord = new TimeRecord(1, 800, 830, false, TimeRecord.HOLIDAY_NONE);

        double hours = PayrollCalculator.computeHoursWorked(shortRecord, SETTINGS);
        assertEquals(0.0, hours, 0.01, "less than 1 hr → 0 hours counted");

        TimeRecord[] records = {shortRecord};
        assertEquals(0.0, PayrollCalculator.computeUndertimeHours(records, SETTINGS), 0.01);
        assertEquals(0, PayrollCalculator.computeAbsentDays(records));
    }

    @Test
    void testShortShiftAlmostOneHour() {
        TimeRecord almost = new TimeRecord(2, 800, 855, false, TimeRecord.HOLIDAY_NONE);
        double hours = PayrollCalculator.computeHoursWorked(almost, SETTINGS);
        assertEquals(0.0, hours, 0.01, "55 min < 1 hr → 0 hours counted");
    }

    @Test
    void testShortShiftExactlyOneHour() {
        // exactly 1 hour with no lunch considered (outHours <= lunchStart)
        TimeRecord exactly = new TimeRecord(3, 800, 900, false, TimeRecord.HOLIDAY_NONE);
        double hours = PayrollCalculator.computeHoursWorked(exactly, SETTINGS);
        assertEquals(1.0, hours, 0.01, "exactly 1 hr should count as 1");
    }

    private static TimeRecord[] buildRecords(int days, int timeIn, int timeOut,
                                              boolean absent, String holiday) {
        TimeRecord[] records = new TimeRecord[days];
        for (int i = 0; i < days; i++) {
            records[i] = new TimeRecord(i + 1, timeIn, timeOut, absent, holiday);
        }
        return records;
    }
}