package com.com253.payrollsystem.Model.EmployeeTypes;

import com.com253.payrollsystem.Model.Employee;
import com.com253.payrollsystem.Model.LeaveBalance;
import com.com253.payrollsystem.Model.LoanBalance;

/**
 * Represents a contractual employee.
 */
public class Contractual extends Employee {

    /**
     * Creates a contractual employee record.
     *
     * @param employeeId   employee identifier
     * @param name         employee name
     * @param monthlyRate  monthly compensation rate
     * @param leaveBalance employee's leave credit balances
     * @param loanBalance  employee's outstanding loan balance
     */
    public Contractual(String employeeId, String name, double monthlyRate, 
            LeaveBalance leaveBalance, LoanBalance loanBalance) {
        super(employeeId, name, monthlyRate, 0.0, false,
            leaveBalance, loanBalance);
    }

    @Override
    public double computeDailyRate() {
        return getMonthlyRate() / 26.0;
    }
}