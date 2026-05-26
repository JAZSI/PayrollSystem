package com.com253.payrollsystem.domain.service;

import com.com253.payrollsystem.domain.model.Employee;
import com.com253.payrollsystem.domain.model.PayrollSettings;

public final class PenaltyCalculator {

    private PenaltyCalculator() {}

    public static double computeUndertimePenalty(double undertimeHours, double hourlyRate) {
        return undertimeHours * hourlyRate;
    }

    public static double computeAbsencePenalty(Employee employee, int absentDays, PayrollSettings settings) {
        if (absentDays <= 0) {
            return 0.0;
        }

        int leaveCredits = employee.isHasLeave() ? employee.getLeaveBalance().getTotal() : 0;
        int chargeableDays = Math.max(0, absentDays - leaveCredits);
        if (chargeableDays == 0) {
            return 0.0;
        }

        return chargeableDays * computeDailyRate(employee, settings);
    }

    private static double computeDailyRate(Employee employee, PayrollSettings settings) {
        if (employee.getEmployeeType() == com.com253.payrollsystem.domain.model.Employee.EmployeeType.PARTTIMER) {
            return employee.getHourlyRate() * 8.0;
        }
        return employee.getMonthlyRate() / settings.getWorkingDaysPerMonth();
    }
}
