package com.com253.payrollsystem.Model.EmployeeTypes;

import com.com253.payrollsystem.Model.Employee;

/**
 * Represents a part-time employee.
 */
public class PartTimer extends Employee {

    public PartTimer(String employeeId, String name, double hourlyRate, 
            int sickLeave, int vacationLeave, int emergencyLeave, double loanBalance) {
        super(employeeId, name, "PartTimer", 0.0, hourlyRate, false,
            sickLeave, vacationLeave, emergencyLeave, loanBalance);
    }

    @Override
    public double computeDailyRate() {
        return getHourlyRate() * 8.0;
    }
}