package com.com253.payrollsystem.shared.domain.employeetypes;

import com.com253.payrollsystem.shared.domain.Employee;
import com.com253.payrollsystem.shared.domain.EmployeeType;

/** Contractual employee (monthly, no leave). */
public class Contractual extends Employee {

    public Contractual(String employeeId, String name, double monthlyRate) {
        super(employeeId, name, EmployeeType.CONTRACTUAL, monthlyRate, 0.0);
    }
}
