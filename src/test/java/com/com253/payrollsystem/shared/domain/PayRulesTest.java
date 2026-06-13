package com.com253.payrollsystem.shared.domain;

import com.com253.payrollsystem.shared.domain.employeetypes.Regular;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

/** Night differential, employer-share contributions, and premium stacking. */
class PayRulesTest {

    private static final double EPS = 1e-9;

    private static final PayrollSettings SETTINGS =
            new PayrollSettings(26, 8.0, 17.0, 11.0, 5, 5, 0, 0);

    private static Employee regular() {
        return new Regular("1234-5678-90", "John", 30000.00);
    }

    private static TimeRecord day(int in, int out, HolidayType holiday) {
        return new TimeRecord(1, in, out, false, holiday);
    }

    @Test
    void nightHoursCountWorkInTheNightWindow() {
        assertThat(PayrollCalculator.computeNightHours(day(800, 1700, HolidayType.NONE)))
                .isEqualTo(0.0, within(EPS));
        assertThat(PayrollCalculator.computeNightHours(day(1400, 2300, HolidayType.NONE)))
                .isEqualTo(1.0, within(EPS)); // 22:00–23:00
        assertThat(PayrollCalculator.computeNightHours(day(2000, 2400, HolidayType.NONE)))
                .isEqualTo(2.0, within(EPS)); // 22:00–24:00
    }

    @Test
    void nightDiffIsTenPercentOfHourlyRate() {
        TimeRecord[] records = {day(1400, 2300, HolidayType.NONE)}; // 1 night hour
        double hourly = (30000.0 / 26) / 8.0;
        double expected = hourly * 0.10;

        assertThat(PayrollCalculator.computeNightDiffPay(regular(), records, SETTINGS))
                .isEqualTo(expected, within(1e-6));
    }

    @Test
    void employerSharesAreStoredPerCutoff() {
        PayrollEntry e = PayrollCalculator.buildPayrollEntry(
                regular(), new TimeRecord[]{day(800, 1700, HolidayType.NONE)},
                "1st-15th", SETTINGS, PayContext.of(0, 0));

        assertThat(e.getEmployerSss()).isEqualTo(1500.0, within(EPS));     // 2 x 1500 / 2
        assertThat(e.getEmployerPhilhealth()).isEqualTo(412.5, within(EPS)); // 825 / 2
        assertThat(e.getEmployerPagibig()).isEqualTo(50.0, within(EPS));   // 100 / 2
        assertThat(e.getEmployerEc()).isEqualTo(15.0, within(EPS));        // 30 / 2
    }

    @Test
    void holidayOvertimeStacksHigherThanRestDayAndRegular() {
        TimeRecord[] regularDay = {day(800, 1900, HolidayType.NONE)};
        TimeRecord[] restDay = {day(800, 1900, HolidayType.SPECIAL_OR_REST_DAY)};
        TimeRecord[] holiday = {day(800, 1900, HolidayType.REGULAR_HOLIDAY)};

        double regularOt = PayrollCalculator.computeOvertimePay(regular(), regularDay, SETTINGS);
        double restOt = PayrollCalculator.computeOvertimePay(regular(), restDay, SETTINGS);
        double holidayOt = PayrollCalculator.computeOvertimePay(regular(), holiday, SETTINGS);

        assertThat(holidayOt).isGreaterThan(restOt);
        assertThat(restOt).isGreaterThan(regularOt);
    }
}
