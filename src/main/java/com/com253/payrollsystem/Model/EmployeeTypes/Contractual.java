package com.com253.payrollsystem.Model.EmployeeTypes;

import com.com253.payrollsystem.Model.Employee;

/**
 * Represents a contractual employee.
 */
public class Contractual extends Employee {

    public Contractual(String employeeId, String name, double monthlyRate, 
            int sickLeave, int vacationLeave, int emergencyLeave, double loanBalance) {
        super(employeeId, name, "Contractual", monthlyRate, 0.0, false,
            sickLeave, vacationLeave, emergencyLeave, loanBalance);
    }

    @Override
    public double computeDailyRate() {
        return getMonthlyRate() / 26.0;
    }
}