package com.com253.payrollsystem.Model.EmployeeTypes;

import com.com253.payrollsystem.Model.Employee;

/**
 * Represents a regular employee.
 */
public class Regular extends Employee {

    /**
     * Creates a regular employee record.
     *
     * @param employeeId employee identifier
     * @param name employee name
     * @param monthlyRate monthly compensation rate
     */
    public Regular(String employeeId, String name, double monthlyRate) {
        super(employeeId, name, "Regular", monthlyRate, 0.0, true);
    }

    /**
     * Computes the daily rate based on monthly rate.
     *
     * @return daily rate
     */
    @Override
    public double computeDailyRate() {
        return getMonthlyRate() / 26.0;
    }
}