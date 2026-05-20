package com.com253.payrollsystem.Model;

/**
 * Represents an employee's outstanding loan balance.
 * Tracks the remaining amount owed and handles per-cutoff deductions.
 */
public class LoanBalance {
    
    private double balance;
    
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
     * Deducts the given amount from the loan balance.
     * Will not deduct more than the remaining balance.
     *
     * @param amount amount to deduct this cutoff
     * @return the actual amount deducted
     */
    public double deduct(double amount) {
        double deducted = Math.min(amount, balance);
        balance -= deducted;
        return deducted;
    }
}
