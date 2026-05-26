package com.com253.payrollsystem.infrastructure.persistence.dao;

import com.com253.payrollsystem.app.port.EmployeeRepositoryPort;
import com.com253.payrollsystem.domain.model.Employee;
import com.com253.payrollsystem.domain.model.Employee.EmployeeType;
import com.com253.payrollsystem.domain.model.LeaveBalance;
import com.com253.payrollsystem.domain.model.LoanBalance;
import com.com253.payrollsystem.infrastructure.persistence.DataAccessException;
import com.com253.payrollsystem.infrastructure.persistence.jdbc.JdbcTemplate;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Lightweight JDBC DAO for employee CRUD.
 * Uses SQLite upsert so the GUI can create/edit using one save path.
 */
public class EmployeeDao implements EmployeeRepositoryPort {

    private static final String SELECT_COLUMNS = "id, name, type, rate, sick_leave, vacation_leave, emergency_leave, loan_balance";

    @Override
    public void save(Employee employee) throws SQLException {
        try {
            String sql = "INSERT INTO employees (id, name, type, rate, sick_leave, vacation_leave, emergency_leave, loan_balance) "
                       + "VALUES (?, ?, ?, ?, ?, ?, ?, ?) "
                       + "ON CONFLICT(id) DO UPDATE SET "
                       + "name = excluded.name, "
                       + "type = excluded.type, "
                       + "rate = excluded.rate, "
                       + "sick_leave = excluded.sick_leave, "
                       + "vacation_leave = excluded.vacation_leave, "
                       + "emergency_leave = excluded.emergency_leave, "
                       + "loan_balance = excluded.loan_balance";
            JdbcTemplate.update(sql, stmt -> bindEmployee(stmt, employee));
        } catch (SQLException e) {
            throw new DataAccessException("Failed to save employee: " + employee.getEmployeeId(), e);
        }
    }

    public void update(Employee employee) throws SQLException {
        save(employee);
    }

    @Override
    public Optional<Employee> findById(String id) throws SQLException {
        try {
            String sql = "SELECT " + SELECT_COLUMNS + " FROM employees WHERE id = ?";
            return JdbcTemplate.queryForObject(sql, stmt -> stmt.setString(1, id), this::mapEmployee);
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find employee by id: " + id, e);
        }
    }

    @Override
    public List<Employee> findAll() throws SQLException {
        try {
            String sql = "SELECT " + SELECT_COLUMNS + " FROM employees ORDER BY name";
            return JdbcTemplate.query(sql, this::mapEmployee);
        } catch (SQLException e) {
            throw new DataAccessException("Failed to query all employees", e);
        }
    }

    @Override
    public void delete(String id) throws SQLException {
        try {
            String sql = "DELETE FROM employees WHERE id = ?";
            JdbcTemplate.update(sql, stmt -> stmt.setString(1, id));
        } catch (SQLException e) {
            throw new DataAccessException("Failed to delete employee: " + id, e);
        }
    }

    @Override
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

    @Override
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

    private void bindEmployee(java.sql.PreparedStatement stmt, Employee employee) throws SQLException {
        double rate = employee.getEmployeeType() == EmployeeType.PARTTIMER
                ? employee.getHourlyRate()
                : employee.getMonthlyRate();

        stmt.setString(1, employee.getEmployeeId());
        stmt.setString(2, employee.getName());
        stmt.setString(3, employee.getTypeName());
        stmt.setDouble(4, rate);
        stmt.setInt(5, employee.getLeaveBalance().getSick());
        stmt.setInt(6, employee.getLeaveBalance().getVacation());
        stmt.setInt(7, employee.getLeaveBalance().getEmergency());
        stmt.setDouble(8, employee.getLoanBalance().getBalance());
    }

    private Employee mapEmployee(ResultSet rs) throws SQLException {
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
        boolean hasLeave = type != EmployeeType.PARTTIMER && type != EmployeeType.CONTRACTUAL;
        double monthlyRate = type == EmployeeType.PARTTIMER ? 0.0 : rate;
        double hourlyRate = type == EmployeeType.PARTTIMER ? rate : 0.0;

        return new Employee(id, name, type, monthlyRate, hourlyRate, hasLeave, leave, loan);
    }
}