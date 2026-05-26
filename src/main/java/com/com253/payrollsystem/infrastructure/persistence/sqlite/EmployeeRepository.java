package com.com253.payrollsystem.infrastructure.persistence.sqlite;

import com.com253.payrollsystem.app.port.EmployeeRepositoryPort;
import com.com253.payrollsystem.domain.model.Employee;
import com.com253.payrollsystem.domain.model.Employee.EmployeeType;
import com.com253.payrollsystem.domain.model.LeaveBalance;
import com.com253.payrollsystem.domain.model.LoanBalance;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import com.com253.payrollsystem.infrastructure.persistence.jdbc.JdbcTemplate;
import com.com253.payrollsystem.infrastructure.persistence.DataAccessException;

public class EmployeeRepository implements EmployeeRepositoryPort {

    public void save(Employee employee) throws SQLException {
        try {
            Double rate = (employee.getEmployeeType() == EmployeeType.PARTTIMER)
                    ? employee.getHourlyRate()
                    : employee.getMonthlyRate();

            String sql = "INSERT INTO employees (id, name, type, rate, sick_leave, vacation_leave, emergency_leave, loan_balance) "
                       + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            JdbcTemplate.update(sql, stmt -> {
                stmt.setString(1, employee.getEmployeeId());
                stmt.setString(2, employee.getName());
                stmt.setString(3, employee.getTypeName());
                stmt.setDouble(4, rate);
                stmt.setInt(5, employee.getLeaveBalance().getSick());
                stmt.setInt(6, employee.getLeaveBalance().getVacation());
                stmt.setInt(7, employee.getLeaveBalance().getEmergency());
                stmt.setDouble(8, employee.getLoanBalance().getBalance());
            });
        } catch (SQLException e) {
            throw new DataAccessException("Failed to save employee: " + employee.getEmployeeId(), e);
        }
    }

    public Optional<Employee> findById(String id) throws SQLException {
        try {
            String sql = "SELECT id, name, type, rate, sick_leave, vacation_leave, emergency_leave, loan_balance FROM employees WHERE id = ?";
            return JdbcTemplate.queryForObject(sql, stmt -> stmt.setString(1, id), this::buildEmployee);
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find employee by id: " + id, e);
        }
    }

    public List<Employee> findAll() throws SQLException {
        try {
            String sql = "SELECT id, name, type, rate, sick_leave, vacation_leave, emergency_leave, loan_balance FROM employees";
            return JdbcTemplate.query(sql, this::buildEmployee);
        } catch (SQLException e) {
            throw new DataAccessException("Failed to query all employees", e);
        }
    }

    public void delete(String id) throws SQLException {
        try {
            String sql = "DELETE FROM employees WHERE id = ?";
            JdbcTemplate.update(sql, stmt -> stmt.setString(1, id));
        } catch (SQLException e) {
            throw new DataAccessException("Failed to delete employee: " + id, e);
        }
    }

    public void updateLeaveBalance(String employeeId, LeaveBalance leaveBalance) throws SQLException {
        try {
            String sql = "UPDATE employees SET sick_leave = ?, vacation_leave = ?, emergency_leave = ? WHERE id = ?";
            JdbcTemplate.update(sql, stmt -> {
                stmt.setInt(1, leaveBalance.getSick());
                stmt.setInt(2, leaveBalance.getVacation());
                stmt.setInt(3, leaveBalance.getEmergency());
                stmt.setString(4, employeeId);
            });
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update leave balance for: " + employeeId, e);
        }
    }

    public void updateLoanBalance(String employeeId, LoanBalance loanBalance) throws SQLException {
        try {
            String sql = "UPDATE employees SET loan_balance = ? WHERE id = ?";
            JdbcTemplate.update(sql, stmt -> {
                stmt.setDouble(1, loanBalance.getBalance());
                stmt.setString(2, employeeId);
            });
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update loan balance for: " + employeeId, e);
        }
    }

    private Employee buildEmployee(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String name = rs.getString("name");
        String typeStr = rs.getString("type");
        double rate = rs.getDouble("rate");
        LeaveBalance leave = new LeaveBalance(
                rs.getInt("sick_leave"),
                rs.getInt("vacation_leave"),
                rs.getInt("emergency_leave"));
        LoanBalance loan = new LoanBalance(rs.getDouble("loan_balance"));

        EmployeeType type = EmployeeType.valueOf(typeStr.toUpperCase());
        boolean hasLeave = (type != EmployeeType.PARTTIMER && type != EmployeeType.CONTRACTUAL);

        double monthlyRate = (type == EmployeeType.PARTTIMER) ? 0.0 : rate;
        double hourlyRate = (type == EmployeeType.PARTTIMER) ? rate : 0.0;

        return new Employee(id, name, type, monthlyRate, hourlyRate, hasLeave, leave, loan);
    }
}