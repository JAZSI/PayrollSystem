package com.com253.payrollsystem.infrastructure.persistence.sqlite;

import com.com253.payrollsystem.app.port.LeaveTransactionRepositoryPort;

import com.com253.payrollsystem.domain.model.LeaveTransaction;
import com.com253.payrollsystem.domain.model.LeaveTransaction.LeaveType;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.com253.payrollsystem.infrastructure.persistence.jdbc.JdbcTemplate;
import com.com253.payrollsystem.infrastructure.persistence.DataAccessException;

/**
 * Repository for leave transaction ledger entries.
 * Separate from EmployeeRepository to keep leave lifecycle independent.
 */
public class LeaveTransactionRepository implements LeaveTransactionRepositoryPort {

    public void save(String employeeId, LeaveType leaveType, int days, String cutOffPeriod) throws SQLException {
        try {
            String sql = "INSERT INTO leave_transactions (employee_id, leave_type, days, cutoff_period) VALUES (?, ?, ?, ?)";
            JdbcTemplate.update(sql, stmt -> {
                stmt.setString(1, employeeId);
                stmt.setString(2, leaveType.name());
                stmt.setInt(3, days);
                stmt.setString(4, cutOffPeriod);
            });
        } catch (SQLException e) {
            throw new DataAccessException("Failed to save leave transaction for: " + employeeId, e);
        }
    }

    public List<LeaveTransaction> findByEmployeeId(String employeeId) throws SQLException {
        try {
            String sql = "SELECT id, employee_id, leave_type, days, cutoff_period, created_at "
                       + "FROM leave_transactions WHERE employee_id = ? ORDER BY created_at DESC";
            return JdbcTemplate.query(sql, stmt -> stmt.setString(1, employeeId), rs -> new LeaveTransaction(
                    rs.getInt("id"),
                    rs.getString("employee_id"),
                    LeaveType.valueOf(rs.getString("leave_type")),
                    rs.getInt("days"),
                    rs.getString("cutoff_period"),
                    rs.getString("created_at")));
        } catch (SQLException e) {
            throw new DataAccessException("Failed to query leave transactions for: " + employeeId, e);
        }
    }
}