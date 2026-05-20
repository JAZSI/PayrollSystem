package com.com253.payrollsystem.Model;

/**
 * Represents an employee's leave credit balances.
 * Tracks sick, vacation, and emergency leave separately.
 */
public class LeaveBalance {
    private int sick;
    private int vacation;
    private int emergency;

    /**
     * Creates a leave balance with the given credit counts.
     *
     * @param sick      number of sick leave days
     * @param vacation  number of vacation leave days
     * @param emergency number of emergency leave days
     */
    public LeaveBalance(int sick, int vacation, int emergency) {
        this.sick = sick;
        this.vacation = vacation;
        this.emergency = emergency;
    }


    /**
     * Gets the number of sick leave days remaining.
     *
     * @return sick leave days
     */
    public int getSick() { 
        return sick; 
    }

    /**
     * Gets the number of vacation leave days remaining.
     *
     * @return vacation leave days
     */
    public int getVacation() { 
        return vacation; 
    }

    /**
     * Gets the number of emergency leave days remaining.
     *
     * @return emergency leave days
     */
    public int getEmergency() { 
        return emergency; 
    }

    /**
     * Gets the total combined leave days remaining across all types.
     *
     * @return total leave days
     */
    public int getTotal() { 
        return sick + vacation + emergency; 
    }


    /**
     * Deducts the given number of days from leave balances in order:
     * sick first, then vacation, then emergency.
     *
     * @param days number of leave days to deduct
     */
    public void deduct(int days) {
        int remaining = days;

        int takeSick = Math.min(remaining, sick);
        sick -= takeSick;
        remaining -= takeSick;

        int takeVacation = Math.min(remaining, vacation);
        vacation -= takeVacation;
        remaining -= takeVacation;

        emergency -= Math.min(remaining, emergency);
    }
}

