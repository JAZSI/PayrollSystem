package com.com253.payrollsystem.Model;

/**
 * Represents a generic employee with shared payroll attributes.
 */
public abstract class Employee {
    private String employeeId;
    private String name;
    private String employeeType;
    private double monthlyRate;
    private double hourlyRate;
    private boolean hasLeave;
    private LeaveBalance leaveBalance;
    private LoanBalance loanBalance;

    /**
     * Creates an employee with the provided details.
     *
     * @param employeeId    employee identifier
     * @param name          employee name
     * @param employeeType  employee classification
     * @param monthlyRate   monthly compensation rate
     * @param hourlyRate    hourly compensation rate
     * @param hasLeave      leave eligibility flag
     * @param leaveBalance  employee's leave credit balances
     * @param loanBalance   employee's outstanding loan balance
     */
    public Employee(
            String employeeId,
            String name,
            String employeeType,
            double monthlyRate,
            double hourlyRate,
            boolean hasLeave,
            LeaveBalance leaveBalance,
            LoanBalance loanBalance) {
        this.employeeId = employeeId;
        this.name = name;
        this.employeeType = employeeType;
        this.monthlyRate = monthlyRate;
        this.hourlyRate = hourlyRate;
        this.hasLeave = hasLeave;
        this.leaveBalance = leaveBalance;
        this.loanBalance = loanBalance;
    }

    /**
     * Gets the employee identifier.
     *
     * @return employee identifier
     */
    public String getEmployeeId() {
        return employeeId;
    }

    /**
     * Gets the employee name.
     *
     * @return employee name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Gets the employee classification.
     *
     * @return employee type
     */
    public String getEmployeeType() {
        return employeeType;
    }

    /**
     * Gets the monthly rate.
     *
     * @return monthly rate
     */
    public double getMonthlyRate() {
        return monthlyRate;
    }

    /**
     * Gets the hourly rate.
     *
     * @return hourly rate
     */
    public double getHourlyRate() {
        return hourlyRate;
    }

    /**
     * Indicates whether the employee has leave benefits.
     *
     * @return true if leave is available; otherwise false
     */
    public boolean isHasLeave() {
        return hasLeave;
    }
    
    /**
     * Gets the employee's leave balance object.
     *
     * @return leave balance
     */
    public LeaveBalance getLeaveBalance() {
        return leaveBalance;
    }
    
    /**
     * Gets the employee's loan balance object.
     *
     * @return loan balance
     */
    public LoanBalance getLoanBalance() {
        return loanBalance;
    }
    
    /**
     * Computes the equivalent daily rate.
     *
     * @return daily rate value
     */
    public abstract double computeDailyRate();
}