package com.com253.payrollsystem.shared.domain.employeetypes;

import com.com253.payrollsystem.shared.domain.Employee;
import com.com253.payrollsystem.shared.domain.EmployeeType;

/** Regular employee (monthly, leave-eligible). */
public class Regular extends Employee {

    public Regular(String employeeId, String name, double monthlyRate) {
        super(employeeId, name, EmployeeType.REGULAR, monthlyRate, 0.0);
    }
}
