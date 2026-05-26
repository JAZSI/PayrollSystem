package com.com253.payrollsystem.domain.service;

import com.com253.payrollsystem.domain.model.Employee;
import com.com253.payrollsystem.domain.model.Employee.EmployeeType;
import com.com253.payrollsystem.domain.model.PayrollSettings;
import com.com253.payrollsystem.domain.model.TimeRecord;

public final class EarningsCalculator {

    private static final double REGULAR_DAY_OT_MULTIPLIER = 1.25;
    private static final double REGULAR_HOLIDAY_MULTIPLIER = 2.00;
    private static final double REGULAR_HOLIDAY_OT_PREMIUM = 0.30;
    private static final double SPECIAL_DAY_MULTIPLIER = 1.30;
    private static final double SPECIAL_DAY_OT_PREMIUM = 0.30;
    private static final double NSD_RATE = 0.10;
    private static final double STANDARD_HOURS_PER_DAY = 8.0;

    private EarningsCalculator() {}

    public static double computeGrossPay(Employee employee, TimeRecord[] records, PayrollSettings settings) {
        return computeBasicPay(employee, records, settings)
             + computeNSD(employee, records, settings)
             + computeHolidayPay(employee, records, settings)
             + computeOvertimePay(employee, records, settings);
    }

    public static double computeBasicPay(Employee employee, TimeRecord[] records, PayrollSettings settings) {
        if (employee.getEmployeeType() == EmployeeType.PARTTIMER) {
            double totalHours = WorkHoursCalculator.computeTotalHours(records, settings);
            return totalHours * employee.getHourlyRate();
        }
        return employee.getMonthlyRate() / 2.0;
    }

    public static double computeOvertimePay(Employee employee, TimeRecord[] records, PayrollSettings settings) {
        double overtimePay = 0.0;
        double hourlyRate = getHourlyRate(employee, settings);

        for (TimeRecord record : records) {
            if (record.isAbsent()) {
                continue;
            }

            double overtimeHours = WorkHoursCalculator.computeOvertimeHoursForRecord(record, settings);
            double multiplier = computeOtMultiplier(record);
            overtimePay += overtimeHours * hourlyRate * (multiplier - 1.0);
        }
        return overtimePay;
    }

    public static double computeHolidayPay(Employee employee, TimeRecord[] records, PayrollSettings settings) {
        double holidayPay = 0.0;
        double hourlyRate = getHourlyRate(employee, settings);

        for (TimeRecord record : records) {
            if (record.isAbsent() || !record.isHoliday()) {
                continue;
            }

            double hoursWorked = WorkHoursCalculator.computeHoursWorked(record, settings);
            if (record.isRegularHoliday()) {
                holidayPay += hoursWorked * hourlyRate * (REGULAR_HOLIDAY_MULTIPLIER - 1.0);
            } else if (record.isRestDayHoliday()) {
                holidayPay += hoursWorked * hourlyRate * (SPECIAL_DAY_MULTIPLIER - 1.0);
            }
        }
        return holidayPay;
    }

    public static double computeNSD(Employee employee, TimeRecord[] records, PayrollSettings settings) {
        double nsdPay = 0.0;
        double hourlyRate = getHourlyRate(employee, settings);
        final double NSD_START_HOUR = 22.0;
        final double NSD_END_HOUR = 30.0;

        for (TimeRecord record : records) {
            if (record.isAbsent()) {
                continue;
            }

            double timeIn = (record.getTimeIn() / 100) + (record.getTimeIn() % 100) / 60.0;
            double timeOut = (record.getTimeOut() / 100) + (record.getTimeOut() % 100) / 60.0;
            if (timeOut <= timeIn) {
                timeOut += 24.0;
            }

            double nsdHours = Math.max(0.0, Math.min(timeOut, NSD_END_HOUR) - Math.max(timeIn, NSD_START_HOUR));
            if (nsdHours > 0.0) {
                nsdPay += nsdHours * hourlyRate * NSD_RATE;
            }
        }
        return nsdPay;
    }

    private static double computeOtMultiplier(TimeRecord record) {
        if (record.isRegularHoliday()) {
            return 1.0 + (REGULAR_HOLIDAY_MULTIPLIER * REGULAR_HOLIDAY_OT_PREMIUM);
        }
        if (record.isRestDayHoliday()) {
            return 1.0 + (SPECIAL_DAY_MULTIPLIER * SPECIAL_DAY_OT_PREMIUM);
        }
        return REGULAR_DAY_OT_MULTIPLIER;
    }

    private static double getHourlyRate(Employee employee, PayrollSettings settings) {
        if (employee.getEmployeeType() == EmployeeType.PARTTIMER) {
            return employee.getHourlyRate();
        }
        return computeDailyRate(employee, settings) / STANDARD_HOURS_PER_DAY;
    }

    private static double computeDailyRate(Employee employee, PayrollSettings settings) {
        if (employee.getEmployeeType() == EmployeeType.PARTTIMER) {
            return employee.getHourlyRate() * STANDARD_HOURS_PER_DAY;
        }
        return employee.getMonthlyRate() / settings.getWorkingDaysPerMonth();
    }
}
