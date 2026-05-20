package com.com253.payrollsystem.Model;

/**
 * Holds user-defined payroll settings for schedule and leave policies.
 */
public final class PayrollSettings {

    private final int workingDaysPerMonth;
    private final double workdayStartHour;
    private final double overtimeStartHour;
    private final double lunchBreakStartHour;

    /**
     * Creates a payroll settings instance with the given schedule values.
     *
     * @param workingDaysPerMonth  number of working days in a month
     * @param workdayStartHour     workday start time in decimal hours
     * @param overtimeStartHour    overtime start time in decimal hours
     * @param lunchBreakStartHour  lunch break start time in decimal hours
     */
    public PayrollSettings(
            int workingDaysPerMonth,
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
    
    /**
     * Gets the number of working days per month.
     *
     * @return working days per month
     */
    public int getWorkingDaysPerMonth() {
        return workingDaysPerMonth;
    }

    /**
     * Gets the workday start time in decimal hours.
     *
     * @return workday start hour
     */
    public double getWorkdayStartHour() {
        return workdayStartHour;
    }
    
    /**
     * Gets the overtime start time in decimal hours.
     *
     * @return overtime start hour
     */
    public double getOvertimeStartHour() {
        return overtimeStartHour;
    }
    
    /**
     * Gets the lunch break start time in decimal hours.
     *
     * @return lunch break start hour
     */
    public double getLunchBreakStartHour() {
        return lunchBreakStartHour;
    }
}