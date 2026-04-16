package com.com253.payrollsystem.Model.EmployeeTypes;

import com.com253.payrollsystem.Model.Employee;

/**
 * Represents a part-time employee.
 */
public class PartTimer extends Employee {

    /**
     * Creates a part-time employee record.
     *
     * @param employeeId employee identifier
     * @param name employee name
     * @param hourlyRate hourly compensation rate
     */
    public PartTimer(String employeeId, String name, double hourlyRate) {
        super(employeeId, name, "PartTimer", 0.0, hourlyRate, false);
    }

    /**
     * Computes the daily rate based on hourly rate.
     *
     * @return daily rate
     */
    @Override
    public double computeDailyRate() {
        return getHourlyRate() * 8.0;
    }
}