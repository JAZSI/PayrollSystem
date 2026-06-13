package com.com253.payrollsystem.shared.domain;

import com.com253.payrollsystem.shared.domain.employeetypes.PartTimer;
import com.com253.payrollsystem.shared.domain.employeetypes.Regular;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

/**
 * CHARACTERIZATION test: pins the EXACT current outputs of
 * {@link PayrollCalculator#buildPayrollEntry} so behavior-preserving refactors
 * (see .docs/02-cleanup-plan.md and .docs/04-refactoring-roadmap.md) can be
 * verified to change nothing.
 *
 * <p>These values encode today's behavior, which may include bugs that the
 * roadmap will deliberately fix later. When an intentional change (e.g. the
 * Money/BigDecimal rounding migration) shifts a number, update the golden value
 * here in the same commit and record the rationale.
 */
class PayrollCalculatorCharacterizationTest {

    private static final double EPS = 1e-9;

    private static final PayrollSettings SETTINGS = new PayrollSettings(
            26,    // working days per month
            8.0,   // workday start
            17.0,  // overtime start
            11.0,  // lunch start
            5, 5, 0, 0); // leave credits: regular, probationary, contractual, part-timer

    private static TimeRecord day(int d, int in, int out, boolean absent, HolidayType holiday) {
        return new TimeRecord(d, in, out, absent, holiday);
    }

    private static TimeRecord[] fifteenStandardDays() {
        TimeRecord[] r = new TimeRecord[15];
        for (int i = 0; i < 15; i++) {
            r[i] = day(i + 1, 800, 1700, false, HolidayType.NONE);
        }
        return r;
    }

    private static Employee regular() {
        return new Regular("1234-5678-90", "John Christian R. Senoto", 30000.00);
    }

    @Test
    void regularBaseline() {
        PayrollEntry e = PayrollCalculator.buildPayrollEntry(
                regular(), fifteenStandardDays(), "1st-15th", 0.0, SETTINGS);

        assertThat(e.getTotalHoursWorked()).isEqualTo(120.0, within(EPS));
        assertThat(e.getOvertimeHours()).isEqualTo(0.0, within(EPS));
        assertThat(e.getUndertimeHours()).isEqualTo(0.0, within(EPS));
        assertThat(e.getAbsentDays()).isEqualTo(0);
        assertThat(e.getBasicPay()).isEqualTo(15000.0, within(EPS));
        assertThat(e.getOvertimePay()).isEqualTo(0.0, within(EPS));
        assertThat(e.getGrossPay()).isEqualTo(15000.0, within(EPS));
        assertThat(e.getSssDeduction()).isEqualTo(750.0, within(EPS));
        assertThat(e.getPhilhealthDeduction()).isEqualTo(412.5, within(EPS));
        assertThat(e.getPagibigDeduction()).isEqualTo(50.0, within(EPS));
        assertThat(e.getTaxDeduction()).isEqualTo(505.63, within(EPS));
        assertThat(e.getLoanDeduction()).isEqualTo(0.0, within(EPS));
        assertThat(e.getUndertimePenalty()).isEqualTo(0.0, within(EPS));
        assertThat(e.getAbsencePenalty()).isEqualTo(0.0, within(EPS));
        assertThat(e.getNetPay()).isEqualTo(13281.87, within(EPS));
    }

    @Test
    void partTimerBaseline() {
        Employee partTimer = new PartTimer("1111-2222-33", "Jane Doe", 100.00);
        PayrollEntry e = PayrollCalculator.buildPayrollEntry(
                partTimer, fifteenStandardDays(), "1st-15th", 0.0, SETTINGS);

        assertThat(e.getTotalHoursWorked()).isEqualTo(120.0, within(EPS));
        assertThat(e.getBasicPay()).isEqualTo(12000.0, within(EPS));
        assertThat(e.getOvertimePay()).isEqualTo(0.0, within(EPS));
        assertThat(e.getGrossPay()).isEqualTo(12000.0, within(EPS));
        assertThat(e.getSssDeduction()).isEqualTo(600.0, within(EPS));
        assertThat(e.getPhilhealthDeduction()).isEqualTo(330.0, within(EPS));
        assertThat(e.getPagibigDeduction()).isEqualTo(50.0, within(EPS));
        assertThat(e.getTaxDeduction()).isEqualTo(90.5, within(EPS));
        assertThat(e.getNetPay()).isEqualTo(10929.5, within(EPS));
    }

    @Test
    void regularWithOvertime() {
        TimeRecord[] ot = fifteenStandardDays();
        ot[0] = day(1, 800, 1900, false, HolidayType.NONE);
        ot[1] = day(2, 800, 1900, false, HolidayType.NONE);

        PayrollEntry e = PayrollCalculator.buildPayrollEntry(
                regular(), ot, "1st-15th", 0.0, SETTINGS);

        assertThat(e.getTotalHoursWorked()).isEqualTo(124.0, within(EPS));
        assertThat(e.getOvertimeHours()).isEqualTo(4.0, within(EPS));
        assertThat(e.getOvertimePay()).isEqualTo(721.15, within(EPS));
        assertThat(e.getGrossPay()).isEqualTo(15721.15, within(EPS));
        assertThat(e.getTaxDeduction()).isEqualTo(613.80, within(EPS));
        assertThat(e.getNetPay()).isEqualTo(13894.85, within(EPS));
    }

    @Test
    void regularWithAbsenceAndLoan() {
        // Absence is fully covered by the 5 leave credits, so no absence penalty.
        TimeRecord[] absent = fifteenStandardDays();
        absent[2] = day(3, 0, 0, true, HolidayType.NONE);

        PayrollEntry e = PayrollCalculator.buildPayrollEntry(
                regular(), absent, "1st-15th", 1000.0, SETTINGS);

        assertThat(e.getTotalHoursWorked()).isEqualTo(112.0, within(EPS));
        assertThat(e.getAbsentDays()).isEqualTo(1);
        assertThat(e.getAbsencePenalty()).isEqualTo(0.0, within(EPS));
        assertThat(e.getGrossPay()).isEqualTo(15000.0, within(EPS));
        assertThat(e.getLoanDeduction()).isEqualTo(1000.0, within(EPS));
        assertThat(e.getNetPay()).isEqualTo(12281.87, within(EPS));
    }

    @Test
    void regularWithUndertime() {
        TimeRecord[] under = fifteenStandardDays();
        under[3] = day(4, 800, 1500, false, HolidayType.NONE);

        PayrollEntry e = PayrollCalculator.buildPayrollEntry(
                regular(), under, "1st-15th", 0.0, SETTINGS);

        assertThat(e.getTotalHoursWorked()).isEqualTo(118.0, within(EPS));
        assertThat(e.getUndertimeHours()).isEqualTo(2.0, within(EPS));
        assertThat(e.getUndertimePenalty()).isEqualTo(288.46, within(EPS));
        assertThat(e.getNetPay()).isEqualTo(12993.41, within(EPS));
    }

    @Test
    void regularHolidayWithOvertime() {
        TimeRecord[] holiday = fifteenStandardDays();
        holiday[4] = day(5, 800, 1900, false, HolidayType.REGULAR_HOLIDAY);

        PayrollEntry e = PayrollCalculator.buildPayrollEntry(
                regular(), holiday, "1st-15th", 0.0, SETTINGS);

        assertThat(e.getTotalHoursWorked()).isEqualTo(122.0, within(EPS));
        assertThat(e.getOvertimeHours()).isEqualTo(2.0, within(EPS));
        assertThat(e.getOvertimePay()).isEqualTo(750.0, within(EPS));
        assertThat(e.getGrossPay()).isEqualTo(15750.0, within(EPS));
        assertThat(e.getNetPay()).isEqualTo(13919.37, within(EPS));
    }
}
