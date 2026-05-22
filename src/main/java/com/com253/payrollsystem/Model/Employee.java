package com.com253.payrollsystem.Model;

/**
 * Represents a generic employee with shared payroll attributes.
 */
public abstract class Employee {

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
     * @param name          employee name
     * @param type          employee type (REGULAR, PROBATIONARY, CONTRACTUAL, PARTTIMER)
     * @param monthlyRate   monthly compensation rate
     * @param hourlyRate    hourly compensation rate
     * @param hasLeave      leave eligibility flag
     * @param leaveBalance  employee's leave credit balances
     * @param loanBalance   employee's outstanding loan balance
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
        return getClass().getSimpleName();
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

    /**
     * Computes the hourly rate for this employee.
     * For PartTimer, this is the direct hourly rate stored in the field.
     * For all other types, this is derived from the daily rate divided by 8 standard hours.
     *
     * @return hourly rate
     */
    public double computeHourlyRate() {
        if (type == EmployeeType.PARTTIMER) {
            return hourlyRate;
        }
        return computeDailyRate() / 8.0;
    }

    /**
     * Computes the equivalent daily rate for this employee.
     * For PartTimer, this is the hourly rate multiplied by 8 standard hours.
     * For all other types, this is the monthly rate divided by 26 working days.
     *
     * @return daily rate value
     */
    public double computeDailyRate() {
        if (type == EmployeeType.PARTTIMER) {
            return hourlyRate * 8.0;
        }
        return monthlyRate / 26.0;
    }
}