package com.com253.payrollsystem.model.employeetypes;

import com.com253.payrollsystem.model.Employee;
import com.com253.payrollsystem.model.EmployeeType;

/**
 * Represents a probationary employee.
 */
public class Probationary extends Employee {

    /**
     * Creates a probationary employee record.
     *
     * @param employeeId employee identifier
     * @param name employee name
     * @param monthlyRate monthly compensation rate
     */
    public Probationary(String employeeId, String name, double monthlyRate) {
        super(employeeId, name, EmployeeType.PROBATIONARY, monthlyRate, 0.0);
    }
}