package com.com253.payrollsystem.domain.service;

import com.com253.payrollsystem.domain.model.PayrollSettings;
import com.com253.payrollsystem.domain.model.TimeRecord;

public final class WorkHoursCalculator {

    private static final double STANDARD_HOURS_PER_DAY = 8.0;
    private static final double MINIMUM_PAID_HOURS = 1.0;

    private WorkHoursCalculator() {}

    public static double computeHoursWorked(TimeRecord record, PayrollSettings settings) {
        if (record.isAbsent()) {
            return 0.0;
        }

        int timeIn = record.getTimeIn();
        int timeOut = record.getTimeOut();

        double inHours = (timeIn / 100) + (timeIn % 100) / 60.0;
        double outHours = (timeOut / 100) + (timeOut % 100) / 60.0;

        if (outHours <= inHours) {
            outHours += 24.0;
        }

        double effectiveStartHour = Math.max(settings.getWorkdayStartHour(), inHours);
        double hoursWorked = outHours - effectiveStartHour;
        if (outHours > settings.getLunchBreakStartHour()) {
            hoursWorked -= 1.0;
        }

        hoursWorked = Math.max(0.0, hoursWorked);
        if (hoursWorked < MINIMUM_PAID_HOURS) {
            return 0.0;
        }

        return hoursWorked;
    }

    public static double computeTotalHours(TimeRecord[] records, PayrollSettings settings) {
        double total = 0.0;
        for (TimeRecord record : records) {
            total += computeHoursWorked(record, settings);
        }
        return total;
    }

    public static double computeOvertimeHours(TimeRecord[] records, PayrollSettings settings) {
        double overtimeTotal = 0.0;
        for (TimeRecord record : records) {
            if (!record.isAbsent()) {
                overtimeTotal += computeOvertimeHoursForRecord(record, settings);
            }
        }
        return overtimeTotal;
    }

    public static double computeUndertimeHours(TimeRecord[] records, PayrollSettings settings) {
        double undertimeTotal = 0.0;
        for (TimeRecord record : records) {
            if (!record.isAbsent()) {
                double hoursWorked = computeHoursWorked(record, settings);
                if (hoursWorked == 0.0) {
                    continue;
                }
                if (hoursWorked < STANDARD_HOURS_PER_DAY) {
                    undertimeTotal += (STANDARD_HOURS_PER_DAY - hoursWorked);
                }
            }
        }
        return undertimeTotal;
    }

    public static int computeAbsentDays(TimeRecord[] records) {
        int count = 0;
        for (TimeRecord record : records) {
            if (record.isAbsent()) {
                count++;
            }
        }
        return count;
    }

    static double computeOvertimeHoursForRecord(TimeRecord record, PayrollSettings settings) {
        double outHours = (record.getTimeOut() / 100) + (record.getTimeOut() % 100) / 60.0;
        return Math.max(0.0, outHours - settings.getOvertimeStartHour());
    }
}
