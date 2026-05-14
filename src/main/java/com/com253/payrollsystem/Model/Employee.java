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
    private int sickLeave;
    private int vacationLeave;
    private int emergencyLeave;
    private double loanBalance;

    /**
     * Creates an employee with the provided details.
     *
     * @param employeeId employee identifier
     * @param name employee name
     * @param employeeType employee classification
     * @param monthlyRate monthly compensation rate
     * @param hourlyRate hourly compensation rate
     * @param hasLeave leave eligibility flag
     */
    public Employee(
            String employeeId,
            String name,
            String employeeType,
            double monthlyRate,
            double hourlyRate,
            boolean hasLeave,
            int sickLeave,
            int vacationLeave,
            int emergencyLeave,
            double loanBalance) {
        this.employeeId = employeeId;
        this.name = name;
        this.employeeType = employeeType;
        this.monthlyRate = monthlyRate;
        this.hourlyRate = hourlyRate;
        this.hasLeave = hasLeave;
        this.sickLeave = sickLeave;
        this.vacationLeave = vacationLeave;
        this.emergencyLeave = emergencyLeave;
        this.loanBalance = loanBalance;
    }

    /**
     * Gets the variable identifier.
     *
     * @return variable identifier
     */
    public String getEmployeeId() {
        return employeeId;
    }

    public String getName() {
        return name;
    }

    public String getEmployeeType() {
        return employeeType;
    }

    public double getMonthlyRate() {
        return monthlyRate;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public boolean isHasLeave() {
        return hasLeave;
    }

    public int getSickLeave() {
        return sickLeave;
    }
    
    public int getVacationLeave() {
        return vacationLeave;
    }
    
    public int getEmergencyLeave() {
        return emergencyLeave;
    }
    
    public double getLoanBalance() {
        return loanBalance;
    }
    
    public abstract double computeDailyRate();
}