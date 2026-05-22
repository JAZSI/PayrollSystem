package com.com253.payrollsystem.Model.EmployeeTypes;

import com.com253.payrollsystem.Model.Employee;
import com.com253.payrollsystem.Model.Employee.EmployeeType;
import com.com253.payrollsystem.Model.LeaveBalance;
import com.com253.payrollsystem.Model.LoanBalance;

/**
 * Represents a part-time employee.
 */
public class PartTimer extends Employee {

    /**
     * Creates a part-time employee record.
     *
     * @param employeeId   employee identifier
     * @param name         employee name
     * @param hourlyRate   hourly compensation rate
     * @param leaveBalance employee's leave credit balances
     * @param loanBalance  employee's outstanding loan balance
     */
    public PartTimer(String employeeId, String name, double hourlyRate,
            LeaveBalance leaveBalance, LoanBalance loanBalance) {
        super(employeeId, name, EmployeeType.PARTTIMER, 0.0, hourlyRate, false,
            leaveBalance, loanBalance);
    }
}