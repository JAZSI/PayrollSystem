package com.com253.payrollsystem.Model.EmployeeTypes;

import com.com253.payrollsystem.Model.Employee;

/**
 * Represents a probationary employee.
 */
public class Probationary extends Employee {

    public Probationary(String employeeId, String name, double monthlyRate,
            int sickLeave, int vacationLeave, int emergencyLeave, double loanBalance) {
        super(employeeId, name, "Probationary", monthlyRate, 0.0, true,
            sickLeave, vacationLeave, emergencyLeave, loanBalance);
    }

    @Override
    public double computeDailyRate() {
        return getMonthlyRate() / 26.0;
    }
}