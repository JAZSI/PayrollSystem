package com.com253.payrollsystem.Model.EmployeeTypes;

import com.com253.payrollsystem.Model.Employee;

/**
 * Represents a contractual employee.
 */
public class Contractual extends Employee {

    /**
     * Creates a contractual employee record.
     *
     * @param employeeId employee identifier
     * @param name employee name
     * @param monthlyRate monthly compensation rate
     */
    public Contractual(String employeeId, String name, double monthlyRate) {
        super(employeeId, name, "Contractual", monthlyRate, 0.0, false);
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