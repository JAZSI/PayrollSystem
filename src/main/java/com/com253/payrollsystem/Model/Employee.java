package com.com253.payrollsystem.Model;

/**
 * Represents an employee with shared payroll attributes.
 */
public class Employee {

    /**
     * Employee type classifications used for payroll differentiation.
     */
    public enum EmployeeType {
        REGULAR,
        PROBATIONARY,
        CONTRACTUAL,
        PARTTIMER
    }

    private final String employeeId;
    private final String name;
    private final double monthlyRate;
    private final double hourlyRate;
    private final boolean hasLeave;
    private LeaveBalance leaveBalance;
    private LoanBalance loanBalance;
    private final EmployeeType type;

    /**
     * Creates an employee with the provided details.
     *
     * @param employeeId    employee identifier
     * @param name           employee name
     * @param type           employee type (REGULAR, PROBATIONARY, CONTRACTUAL, PARTTIMER)
     * @param monthlyRate    monthly compensation rate (used for non-part-timers)
     * @param hourlyRate     hourly compensation rate (used for part-timers)
     * @param hasLeave       leave eligibility flag
     * @param leaveBalance   employee's leave credit balances
     * @param loanBalance    employee's outstanding loan balance
     */
    public Employee(
            String employeeId,
            String name,
            EmployeeType type,
            double monthlyRate,
            double hourlyRate,
            boolean hasLeave,
            LeaveBalance leaveBalance,
            LoanBalance loanBalance) {
        this.employeeId = employeeId;
        this.name = name;
        this.type = type;
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
     * Gets the employee type.
     *
     * @return employee type
     */
    public EmployeeType getEmployeeType() {
        return type;
    }

    /**
     * Gets the employee classification as a display-friendly string.
     * Used for database persistence and UI display.
     *
     * @return employee type name
     */
    public String getTypeName() {
        return type.name().charAt(0) + type.name().substring(1).toLowerCase();
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
     * Sets the employee's leave balance object.
     *
     * @param balance new leave balance
     */
    public void setLeaveBalance(LeaveBalance balance) {
        this.leaveBalance = balance;
    }

    /**
     * Sets the employee's loan balance object.
     *
     * @param balance new loan balance
     */
    public void setLoanBalance(LoanBalance balance) {
        this.loanBalance = balance;
    }
}