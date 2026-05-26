package com.com253.payrollsystem.infrastructure.persistence.sqlite;

import com.com253.payrollsystem.app.port.LoanTransactionRepositoryPort;

import com.com253.payrollsystem.domain.model.LoanTransaction;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.com253.payrollsystem.infrastructure.persistence.jdbc.JdbcTemplate;
import com.com253.payrollsystem.infrastructure.persistence.DataAccessException;

/**
 * Repository for loan transaction ledger entries.
 * Separate from EmployeeRepository to keep loan lifecycle independent.
 */
public class LoanTransactionRepository implements LoanTransactionRepositoryPort {

    public void save(String employeeId, double amount, String cutOffPeriod) throws SQLException {
        try {
            String sql = "INSERT INTO loan_transactions (employee_id, amount, cutoff_period) VALUES (?, ?, ?)";
            JdbcTemplate.update(sql, stmt -> {
                stmt.setString(1, employeeId);
                stmt.setDouble(2, amount);
                stmt.setString(3, cutOffPeriod);
            });
        } catch (SQLException e) {
            throw new DataAccessException("Failed to save loan transaction for: " + employeeId, e);
        }
    }

    public List<LoanTransaction> findByEmployeeId(String employeeId) throws SQLException {
        try {
            String sql = "SELECT id, employee_id, amount, cutoff_period, created_at "
                       + "FROM loan_transactions WHERE employee_id = ? ORDER BY created_at DESC";
            return JdbcTemplate.query(sql, stmt -> stmt.setString(1, employeeId), rs -> new LoanTransaction(
                    rs.getInt("id"),
                    rs.getString("employee_id"),
                    rs.getDouble("amount"),
                    rs.getString("cutoff_period"),
                    rs.getString("created_at")));
        } catch (SQLException e) {
            throw new DataAccessException("Failed to query loan transactions for: " + employeeId, e);
        }
    }
}