package com.com253.payrollsystem.model.employeetypes;

import com.com253.payrollsystem.model.Employee;
import com.com253.payrollsystem.model.EmployeeType;

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
        super(employeeId, name, EmployeeType.PART_TIMER, 0.0, hourlyRate);
    }
}