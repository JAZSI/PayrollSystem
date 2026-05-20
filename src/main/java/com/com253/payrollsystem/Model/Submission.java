package com.com253.payrollsystem.Model;

/**
 * Represents a payroll submission filed by an employee for a cutoff period.
 * Starts as PENDING and moves to APPROVED or REJECTED by an admin.
 */
public class Submission {
    
    /**
     * Represents the current state of a payroll submission.
     */
    public enum Status {
        PENDING,
        APPROVED,
        REJECTED
    }
    
    private final int id;
    private final String employeeId;
    private final double leaveDays;
    private final double otHours;
    private final double loanDeduction;
    private final Status status;
    private final String submittedAt;
    
    
    /**
     * Creates a submission record with all fields.
     *
     * @param id            unique submission identifier
     * @param employeeId    the employee who filed this submission
     * @param leaveDays     number of leave days applied this cutoff
     * @param otHours       overtime hours filed this cutoff
     * @param loanDeduction loan amount to deduct this cutoff
     * @param status        current approval status
     * @param submittedAt   timestamp when the submission was filed
     */
    public Submission(int id, String employeeId, double leaveDays,
                     double otHours, double loanDeduction,
                     Status status, String submittedAt) {
        this.id = id;
        this.employeeId = employeeId;
        this.leaveDays = leaveDays;
        this.otHours = otHours;
        this.loanDeduction = loanDeduction;
        this.status = status;
        this.submittedAt = submittedAt;
    }

    /**
     * Gets the submission ID.
     *
     * @return submission ID
     */    
    public int getId() {
        return id;
    }
    
    /**
     * Gets the employee ID this submission belongs to.
     *
     * @return employee ID
     */    
    public String getEmployeeId() {
        return employeeId;
    }
    
    /**
     * Gets the number of leave days filed.
     *
     * @return leave days
     */    
    public double getLeaveDays() {
        return leaveDays;
    }

    /**
     * Gets the overtime hours filed.
     *
     * @return overtime hours
     */    
    public double getOtHours() {
        return otHours;
    }
    
    /**
     * Gets the loan deduction amount filed.
     *
     * @return loan deduction
     */    
    public double getLoanDeduction() {
        return loanDeduction;
    }
    
    /**
     * Gets the current approval status.
     *
     * @return submission status
     */    
    public Status getStatus() {
        return status;
    }

    /**
     * Gets the timestamp when this submission was filed.
     *
     * @return submitted at timestamp
     */
    public String getSubmittedAt() {
        return submittedAt;
    }
}
