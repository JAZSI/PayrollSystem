package com.com253.payrollsystem.Service;

import com.com253.payrollsystem.Model.Employee;
import com.com253.payrollsystem.Model.EndUser;
import com.com253.payrollsystem.Repository.AccountRepository;
import com.com253.payrollsystem.Repository.EmployeeRepository;
import java.sql.SQLException;
import java.util.List;

public class PayrollService {
    
    private final AccountRepository accountRepository = new AccountRepository();
    private final EmployeeRepository employeeRepository = new EmployeeRepository();
    
    /**
     * Authentication
     */    
    public EndUser authenticate(String username, String password) throws SQLException {
        EndUser user = accountRepository.findByUsername(username);
        if (user == null) {
            return null;
        }
        if (!user.getPasswordHash().equals(password)) {
            return null;
        }
        return user;
    }
    
    
    /**
     * Employee Management
     */    
    public void registerEmployee(Employee employee) throws SQLException {
        employeeRepository.save(employee);
    }
    
    public Employee findEmployee(String id) throws SQLException {
        return employeeRepository.findById(id);
    }
    
    public List<Employee> getAllEmployees() throws SQLException {
        return employeeRepository.findAll();
    }
    
    public void deleteEmployee(String id) throws SQLException {
        employeeRepository.delete(id);
    }
}