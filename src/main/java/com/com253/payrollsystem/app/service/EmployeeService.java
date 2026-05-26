package com.com253.payrollsystem.app.service;

import com.com253.payrollsystem.domain.model.Employee;
import com.com253.payrollsystem.domain.model.EndUser;
import com.com253.payrollsystem.app.port.AccountRepositoryPort;
import com.com253.payrollsystem.app.port.EmployeeRepositoryPort;
import com.com253.payrollsystem.infrastructure.persistence.sqlite.AccountRepository;
import com.com253.payrollsystem.infrastructure.persistence.dao.EmployeeDao;
import java.sql.SQLException;
import java.util.List;
import org.mindrot.jbcrypt.BCrypt;

public class EmployeeService {

    private final AccountRepositoryPort accountRepository;
    private final EmployeeRepositoryPort employeeRepository;

    public EmployeeService() {
        this(new AccountRepository(), new EmployeeDao());
    }

    public EmployeeService(AccountRepositoryPort accountRepository, EmployeeRepositoryPort employeeRepository) {
        this.accountRepository = accountRepository;
        this.employeeRepository = employeeRepository;
    }

    public void registerEmployee(Employee employee, String username, String password) throws SQLException {
        employeeRepository.save(employee);
        String hash = BCrypt.hashpw(password, BCrypt.gensalt(10));
        EndUser account = new EndUser(username, hash, EndUser.Role.EMPLOYEE, employee.getEmployeeId());
        accountRepository.save(account);
    }

    public void saveEmployee(Employee employee) throws SQLException {
        employeeRepository.save(employee);
    }

    public void updateEmployee(Employee employee) throws SQLException {
        employeeRepository.save(employee);
    }

    public Employee findEmployee(String id) throws SQLException {
        return employeeRepository.findById(id).orElse(null);
    }

    public List<Employee> getAllEmployees() throws SQLException {
        return employeeRepository.findAll();
    }

    public void deleteEmployee(String id) throws SQLException {
        accountRepository.deleteByEmployeeId(id);
        employeeRepository.delete(id);
    }
}
