package com.com253.payrollsystem.domain.model;

/**
 * Represents an employee's outstanding loan balance.
 * Immutable — all operations return new instances.
 */
public class LoanBalance {

    private final double balance;

    /**
     * Creates a loan balance with the given outstanding amount.
     *
     * @param balance the initial loan amount owed
     */
    public LoanBalance(double balance) {
        this.balance = balance;
    }

    /**
     * Gets the current outstanding loan balance.
     *
     * @return remaining loan balance
     */
    public double getBalance() {
        return balance;
    }

    /**
     * Computes the deduction amount without modifying this balance.
     * Will not deduct more than the remaining balance.
     *
     * @param amount requested deduction amount
     * @return actual amount that would be deducted
     */
    public double deduct(double amount) {
        return Math.min(amount, balance);
    }

    /**
     * Returns a new LoanBalance with the given deduction applied.
     * Does not modify this instance.
     *
     * @param amount amount to deduct
     * @return new LoanBalance with reduced balance
     */
    public LoanBalance apply(double amount) {
        return new LoanBalance(balance - deduct(amount));
    }
}