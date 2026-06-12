package com.com253.payrollsystem.model.employeetypes;

import com.com253.payrollsystem.model.Employee;
import com.com253.payrollsystem.model.EmployeeType;

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
        super(employeeId, name, EmployeeType.REGULAR, monthlyRate, 0.0);
    }
}