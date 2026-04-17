package com.com253.payrollsystem.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/**
 * Computes working days by excluding weekends for payroll cut-off periods.
 */
public final class WorkingDayCalculator {

    private WorkingDayCalculator() {
    }

    /**
     * Returns working days for the given cut-off using the current month.
     *
     * @param cutOffPeriod either 1st-15th or 16th-30th
     * @return day numbers excluding Saturdays and Sundays
     */
    public static int[] getWorkingDaysForCurrentMonth(String cutOffPeriod) {
        return getWorkingDays(cutOffPeriod, YearMonth.now());
    }

    /**
     * Returns working days for the given cut-off and month.
     *
     * @param cutOffPeriod either 1st-15th or 16th-30th
     * @param yearMonth target month
     * @return day numbers excluding Saturdays and Sundays
     */
    public static int[] getWorkingDays(String cutOffPeriod, YearMonth yearMonth) {
        int startDay;
        int endDay;

        if ("1st-15th".equals(cutOffPeriod)) {
            startDay = 1;
            endDay = 15;
        } else if ("16th-30th".equals(cutOffPeriod)) {
            startDay = 16;
            endDay = Math.min(30, yearMonth.lengthOfMonth());
        } else {
            throw new IllegalArgumentException("Unsupported cut-off period: " + cutOffPeriod);
        }

        List<Integer> days = new ArrayList<>();
        for (int day = startDay; day <= endDay; day++) {
            LocalDate date = yearMonth.atDay(day);
            DayOfWeek dow = date.getDayOfWeek();
            if (dow != DayOfWeek.SATURDAY && dow != DayOfWeek.SUNDAY) {
                days.add(day);
            }
        }

        int[] workingDays = new int[days.size()];
        for (int i = 0; i < days.size(); i++) {
            workingDays[i] = days.get(i);
        }
        return workingDays;
    }
}