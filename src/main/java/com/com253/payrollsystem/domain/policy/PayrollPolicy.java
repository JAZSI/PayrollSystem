package com.com253.payrollsystem.domain.policy;

public class PayrollPolicy {

    private final int workingDaysPerMonth;
    private final double workdayStartHour;
    private final double overtimeStartHour;
    private final double lunchBreakStartHour;

    public PayrollPolicy(int workingDaysPerMonth,
                         double workdayStartHour,
                         double overtimeStartHour,
                         double lunchBreakStartHour) {
        if (workingDaysPerMonth <= 0) {
            throw new IllegalArgumentException("Working days per month must be greater than 0.");
        }
        if (overtimeStartHour <= workdayStartHour) {
            throw new IllegalArgumentException("Overtime start must be later than workday start.");
        }

        this.workingDaysPerMonth = workingDaysPerMonth;
        this.workdayStartHour = workdayStartHour;
        this.overtimeStartHour = overtimeStartHour;
        this.lunchBreakStartHour = lunchBreakStartHour;
    }

    public int getWorkingDaysPerMonth() {
        return workingDaysPerMonth;
    }

    public double getWorkdayStartHour() {
        return workdayStartHour;
    }

    public double getOvertimeStartHour() {
        return overtimeStartHour;
    }

    public double getLunchBreakStartHour() {
        return lunchBreakStartHour;
    }
}
