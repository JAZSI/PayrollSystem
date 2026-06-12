package com.com253.payrollsystem.model.employeetypes;

import com.com253.payrollsystem.model.Employee;
import com.com253.payrollsystem.model.EmployeeType;

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
        super(employeeId, name, EmployeeType.CONTRACTUAL, monthlyRate, 0.0);
    }
}