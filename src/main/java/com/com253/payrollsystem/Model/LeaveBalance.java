package com.com253.payrollsystem.Model;

/**
 * Represents an employee's leave credit balances.
 * Immutable — all operations return new instances.
 */
public class LeaveBalance {
    private final int sick;
    private final int vacation;
    private final int emergency;

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
     * Computes the deduction breakdown without modifying this balance.
     * Deducts in order: sick first, then vacation, then emergency.
     *
     * @param days number of leave days to deduct
     * @return deduction result with per-type breakdown
     */
    public DeductionResult deduct(int days) {
        int remaining = days;

        int takeSick = Math.min(remaining, sick);
        remaining -= takeSick;

        int takeVacation = Math.min(remaining, vacation);
        remaining -= takeVacation;

        int takeEmergency = Math.min(remaining, emergency);

        return new DeductionResult(takeSick, takeVacation, takeEmergency);
    }

    /**
     * Returns a new LeaveBalance with the given deduction applied.
     * Does not modify this instance.
     *
     * @param result the deduction breakdown from deduct()
     * @return new LeaveBalance with reduced balances
     */
    public LeaveBalance apply(DeductionResult result) {
        return new LeaveBalance(
            sick - result.getSick(),
            vacation - result.getVacation(),
            emergency - result.getEmergency()
        );
    }

    /**
     * Holds the breakdown of leave days deducted per type.
     */
    public static class DeductionResult {
        private final int sick;
        private final int vacation;
        private final int emergency;

        public DeductionResult(int sick, int vacation, int emergency) {
            this.sick = sick;
            this.vacation = vacation;
            this.emergency = emergency;
        }

        public int getSick() { return sick; }
        public int getVacation() { return vacation; }
        public int getEmergency() { return emergency; }
        public int getTotal() { return sick + vacation + emergency; }
    }
}