package com.com253.payrollsystem.Repository;

import com.com253.payrollsystem.Model.LoanTransaction;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for loan transaction ledger entries.
 * Separate from EmployeeRepository to keep loan lifecycle independent.
 */
public class LoanTransactionRepository extends BaseRepository {

    public void save(String employeeId, double amount, String cutOffPeriod) throws SQLException {
        String sql = "INSERT INTO loan_transactions (employee_id, amount, cutoff_period) VALUES (?, ?, ?)";
        Connection connection = getConnection();
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            try {
                stmt.setString(1, employeeId);
                stmt.setDouble(2, amount);
                stmt.setString(3, cutOffPeriod);
                stmt.executeUpdate();
            } finally {
                stmt.close();
            }
        } finally {
            close(connection);
        }
    }

    public List<LoanTransaction> findByEmployeeId(String employeeId) throws SQLException {
        String sql = "SELECT id, employee_id, amount, cutoff_period, created_at "
                   + "FROM loan_transactions WHERE employee_id = ? ORDER BY created_at DESC";
        List<LoanTransaction> list = new ArrayList<>();
        Connection connection = getConnection();
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            try {
                stmt.setString(1, employeeId);
                ResultSet rs = stmt.executeQuery();
                try {
                    while (rs.next()) {
                        list.add(new LoanTransaction(
                            rs.getInt("id"),
                            rs.getString("employee_id"),
                            rs.getDouble("amount"),
                            rs.getString("cutoff_period"),
                            rs.getString("created_at")));
                    }
                } finally {
                    rs.close();
                }
            } finally {
                stmt.close();
            }
        } finally {
            close(connection);
        }
        return list;
    }
}