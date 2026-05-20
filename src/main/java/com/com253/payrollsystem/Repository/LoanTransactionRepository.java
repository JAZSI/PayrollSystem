package com.com253.payrollsystem.Repository;

import com.com253.payrollsystem.Model.LoanTransaction;
import com.com253.payrollsystem.Util.Database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles all database operations for loan transaction ledger.
 */
public class LoanTransactionRepository {

    /**
     * Saves a new loan transaction.
     *
     * @param employeeId    employee identifier
     * @param amount       amount deducted this cut-off
     * @param cutOffPeriod cutoff period label
     */
    public void save(String employeeId, double amount, String cutOffPeriod) throws SQLException {
        String sql = "INSERT INTO loan_transactions "
                   + "(employee_id, amount, cutoff_period) "
                   + "VALUES (?, ?, ?)";

        try (Connection connection = Database.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, employeeId);
            stmt.setDouble(2, amount);
            stmt.setString(3, cutOffPeriod);
            stmt.executeUpdate();
        }
    }

    /**
     * Finds all loan transactions for the given employee.
     *
     * @param employeeId employee identifier
     * @return list of transactions ordered by date descending
     */
    public List<LoanTransaction> findByEmployeeId(String employeeId) throws SQLException {
        String sql = "SELECT id, employee_id, amount, cutoff_period, created_at "
                   + "FROM loan_transactions WHERE employee_id = ? "
                   + "ORDER BY created_at DESC";

        List<LoanTransaction> list = new ArrayList<>();

        try (Connection connection = Database.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, employeeId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(buildLoanTransaction(rs));
                }
            }
        }
        return list;
    }

    /**
     * Finds all loan transactions for a given employee and cutoff period.
     *
     * @param employeeId    employee identifier
     * @param cutOffPeriod  cutoff period label
     * @return list of matching transactions
     */
    public List<LoanTransaction> findByEmployeeAndPeriod(String employeeId, String cutOffPeriod)
            throws SQLException {
        String sql = "SELECT id, employee_id, amount, cutoff_period, created_at "
                   + "FROM loan_transactions "
                   + "WHERE employee_id = ? AND cutoff_period = ? "
                   + "ORDER BY created_at DESC";

        List<LoanTransaction> list = new ArrayList<>();

        try (Connection connection = Database.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, employeeId);
            stmt.setString(2, cutOffPeriod);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(buildLoanTransaction(rs));
                }
            }
        }
        return list;
    }

    private LoanTransaction buildLoanTransaction(ResultSet rs) throws SQLException {
        return new LoanTransaction(
                rs.getInt("id"),
                rs.getString("employee_id"),
                rs.getDouble("amount"),
                rs.getString("cutoff_period"),
                rs.getString("created_at"));
    }
}