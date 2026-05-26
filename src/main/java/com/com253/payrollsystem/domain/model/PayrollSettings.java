package com.com253.payrollsystem.domain.model;

import com.com253.payrollsystem.domain.policy.PayrollPolicy;

/**
 * Backward-compatible alias for the new PayrollPolicy domain type.
 */
public class PayrollSettings extends PayrollPolicy {

    public PayrollSettings(int workingDaysPerMonth,
                           double workdayStartHour,
                           double overtimeStartHour,
                           double lunchBreakStartHour) {
        super(workingDaysPerMonth, workdayStartHour, overtimeStartHour, lunchBreakStartHour);
    }
}