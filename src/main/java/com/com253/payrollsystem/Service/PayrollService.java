package com.com253.payrollsystem.Service;

import com.com253.payrollsystem.Model.Employee;
import com.com253.payrollsystem.Model.EndUser;
import com.com253.payrollsystem.Repository.AccountRepository;
import com.com253.payrollsystem.Repository.EmployeeRepository;
import java.sql.SQLException;
import java.util.List;

/**
 * Central service layer for all payroll operations.
 * Acts as the single point of contact between the UI and the repositories.
 */
public class PayrollService {
    
    private final AccountRepository accountRepository = new AccountRepository();
    private final EmployeeRepository employeeRepository = new EmployeeRepository();
    
    /**
     * Authenticates a user by username and password.
     * Returns the matching EndUser or null if credentials are invalid.
     *
     * @param username entered username
     * @param password entered password
     * @return authenticated EndUser, or null if login fails
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
     * Registers a new employee and creates their login account.
     *
     * @param employee the employee to register
     * @param username login username for the new account
     * @param password login password for the new account
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
    
    /**
     * Deletes an employee and their linked account.
     *
     * @param id employee identifier to delete
     */
    public void deleteEmployee(String id) throws SQLException {
        accountRepository.deleteByEmployeeId(id);
        employeeRepository.delete(id);
    }
}