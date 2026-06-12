package com.com253.payrollsystem.model;

/**
 * Represents a generic employee with shared payroll attributes.
 */
public abstract class Employee {
    private final String employeeId;
    private final String name;
    private final EmployeeType employeeType;
    private final double monthlyRate;
    private final double hourlyRate;

    /**
     * Creates an employee with the provided details.
     *
     * @param employeeId employee identifier
     * @param name employee name
     * @param employeeType employee classification
     * @param monthlyRate monthly compensation rate
     * @param hourlyRate hourly compensation rate
     */
    public Employee(
            String employeeId,
            String name,
            EmployeeType employeeType,
            double monthlyRate,
            double hourlyRate) {
        this.employeeId = employeeId;
        this.name = name;
        this.employeeType = employeeType;
        this.monthlyRate = monthlyRate;
        this.hourlyRate = hourlyRate;
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
    public EmployeeType getEmployeeType() {
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
        return employeeType.isLeaveEligible();
    }
}