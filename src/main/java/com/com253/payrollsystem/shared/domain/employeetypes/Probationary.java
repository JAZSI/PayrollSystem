package com.com253.payrollsystem.shared.domain.employeetypes;

import com.com253.payrollsystem.shared.domain.Employee;
import com.com253.payrollsystem.shared.domain.EmployeeType;

/** Probationary employee (monthly, leave-eligible). */
public class Probationary extends Employee {

    public Probationary(String employeeId, String name, double monthlyRate) {
        super(employeeId, name, EmployeeType.PROBATIONARY, monthlyRate, 0.0);
    }
}
