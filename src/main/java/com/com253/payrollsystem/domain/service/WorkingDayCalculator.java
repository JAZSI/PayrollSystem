package com.com253.payrollsystem.domain.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public final class WorkingDayCalculator {

    private WorkingDayCalculator() {}

    public static int[] getWorkingDaysForCurrentMonth(String cutOffPeriod) {
        return getWorkingDays(cutOffPeriod, YearMonth.now());
    }

    public static int[] getWorkingDays(String cutOffPeriod, YearMonth yearMonth) {
        int startDay;
        int endDay;

        com.com253.payrollsystem.domain.model.CutoffPeriod cp = com.com253.payrollsystem.domain.model.CutoffPeriod.fromLabel(cutOffPeriod);
        startDay = cp.startDay();
        endDay = cp.endDay() == -1 ? yearMonth.lengthOfMonth() : Math.min(cp.endDay(), yearMonth.lengthOfMonth());

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
