package com.com253.payrollsystem.shared.domain;

/** Base employee with shared payroll attributes. */
public abstract class Employee {

    private final String employeeId;
    private final String name;
    private final EmployeeType employeeType;
    private final double monthlyRate;
    private final double hourlyRate;

    public Employee(String employeeId, String name, EmployeeType employeeType,
                    double monthlyRate, double hourlyRate) {
        this.employeeId = employeeId;
        this.name = name;
        this.employeeType = employeeType;
        this.monthlyRate = monthlyRate;
        this.hourlyRate = hourlyRate;
    }

    public String getEmployeeId() { return employeeId; }
    public String getName() { return name; }
    public EmployeeType getEmployeeType() { return employeeType; }
    public double getMonthlyRate() { return monthlyRate; }
    public double getHourlyRate() { return hourlyRate; }

    public boolean isHasLeave() { return employeeType.isLeaveEligible(); }
}
