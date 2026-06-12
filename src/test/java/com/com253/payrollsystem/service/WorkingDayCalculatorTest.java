package com.com253.payrollsystem.service;

import org.junit.jupiter.api.Test;

import java.time.YearMonth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Deterministic test for {@link WorkingDayCalculator} using the fixed-month
 * overload (never {@code YearMonth.now()}), so results don't depend on the clock.
 */
class WorkingDayCalculatorTest {

    // June 2026: 1st is a Monday. First half (1-15) weekdays:
    // 1,2,3,4,5 (Mon-Fri), 8,9,10,11,12, 15.
    private static final YearMonth JUNE_2026 = YearMonth.of(2026, 6);

    @Test
    void firstHalfExcludesWeekends() {
        int[] days = WorkingDayCalculator.getWorkingDays("1st-15th", JUNE_2026);
        assertThat(days).containsExactly(1, 2, 3, 4, 5, 8, 9, 10, 11, 12, 15);
    }

    @Test
    void secondHalfIsCappedAtDay30() {
        int[] days = WorkingDayCalculator.getWorkingDays("16th-30th", JUNE_2026);
        // 16-30, weekdays only: 16,17,18,19, 22,23,24,25,26, 29,30.
        assertThat(days).containsExactly(16, 17, 18, 19, 22, 23, 24, 25, 26, 29, 30);
    }

    @Test
    void februarySecondHalfStopsAtMonthLength() {
        // Feb 2026 has 28 days, so 16th-30th cannot exceed 28.
        int[] days = WorkingDayCalculator.getWorkingDays("16th-30th", YearMonth.of(2026, 2));
        assertThat(days).isNotEmpty();
        assertThat(days[days.length - 1]).isLessThanOrEqualTo(28);
    }

    @Test
    void rejectsUnknownPeriod() {
        assertThatThrownBy(() -> WorkingDayCalculator.getWorkingDays("bogus", JUNE_2026))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
