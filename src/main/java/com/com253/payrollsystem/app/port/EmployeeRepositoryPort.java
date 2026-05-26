package com.com253.payrollsystem.app.port;

import com.com253.payrollsystem.domain.model.Employee;
import com.com253.payrollsystem.domain.model.LeaveBalance;
import com.com253.payrollsystem.domain.model.LoanBalance;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface EmployeeRepositoryPort {
    void save(Employee employee) throws SQLException;
    Optional<Employee> findById(String id) throws SQLException;
    List<Employee> findAll() throws SQLException;
    void delete(String id) throws SQLException;
    void updateLeaveBalance(String employeeId, LeaveBalance leaveBalance) throws SQLException;
    void updateLoanBalance(String employeeId, LoanBalance loanBalance) throws SQLException;
}
