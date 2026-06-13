package com.com253.payrollsystem.shared.domain.employeetypes;

import com.com253.payrollsystem.shared.domain.Employee;
import com.com253.payrollsystem.shared.domain.EmployeeType;

/** Part-time employee (hourly, no leave). */
public class PartTimer extends Employee {

    public PartTimer(String employeeId, String name, double hourlyRate) {
        super(employeeId, name, EmployeeType.PART_TIMER, 0.0, hourlyRate);
    }
}
