package com.com253.payrollsystem.model;

/**
 * Holds user-defined payroll settings for schedule and leave policies.
 */
public final class PayrollSettings {

    private final int workingDaysPerMonth;
    private final double workdayStartHour;
    private final double overtimeStartHour;
    private final double lunchBreakStartHour;
    private final int regularLeaveCredits;
    private final int probationaryLeaveCredits;
    private final int contractualLeaveCredits;
    private final int partTimerLeaveCredits;

    public PayrollSettings(int workingDaysPerMonth,
                           double workdayStartHour,
                           double overtimeStartHour,
                           double lunchBreakStartHour,
                           int regularLeaveCredits,
                           int probationaryLeaveCredits,
                           int contractualLeaveCredits,
                           int partTimerLeaveCredits) {
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
        this.regularLeaveCredits = regularLeaveCredits;
        this.probationaryLeaveCredits = probationaryLeaveCredits;
        this.contractualLeaveCredits = contractualLeaveCredits;
        this.partTimerLeaveCredits = partTimerLeaveCredits;
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

    public int getLeaveCreditsFor(Employee employee) {
        return switch (employee.getEmployeeType()) {
            case REGULAR -> regularLeaveCredits;
            case PROBATIONARY -> probationaryLeaveCredits;
            case CONTRACTUAL -> contractualLeaveCredits;
            case PART_TIMER -> partTimerLeaveCredits;
        };
    }
}