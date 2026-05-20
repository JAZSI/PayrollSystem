package com.com253.payrollsystem.Model;

/**
 * Represents a single loan deduction transaction in the ledger.
 */
public class LoanTransaction {

    private final int id;
    private final String employeeId;
    private final double amount;
    private final String cutOffPeriod;
    private final String createdAt;

    /**
     * Creates a loan transaction record.
     *
     * @param id            transaction identifier
     * @param employeeId    employee identifier
     * @param amount        amount deducted this cut-off
     * @param cutOffPeriod  cutoff period in which this was deducted
     * @param createdAt     timestamp of the transaction
     */
    public LoanTransaction(int id, String employeeId, double amount,
                          String cutOffPeriod, String createdAt) {
        this.id = id;
        this.employeeId = employeeId;
        this.amount = amount;
        this.cutOffPeriod = cutOffPeriod;
        this.createdAt = createdAt;
    }

    /**
     * Gets the transaction identifier.
     *
     * @return id
     */
    public int getId() { 
        return id; 
    }

    /**
     * Gets the employee identifier.
     *
     * @return employee id
     */
    public String getEmployeeId() { 
        return employeeId; 
    }

    /**
     * Gets the amount deducted.
     *
     * @return amount
     */
    public double getAmount() { 
        return amount; 
    }

    /**
     * Gets the cutoff period label.
     *
     * @return cutoff period
     */
    public String getCutOffPeriod() { 
        return cutOffPeriod; 
    }

    /**
     * Gets the creation timestamp.
     *
     * @return created at
     */
    public String getCreatedAt() { 
        return createdAt; 
    }
}