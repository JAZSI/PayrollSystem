package com.com253.payrollsystem.Repository;

import com.com253.payrollsystem.Model.LeaveTransaction;
import com.com253.payrollsystem.Model.LeaveTransaction.LeaveType;
import com.com253.payrollsystem.Util.Database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles all database operations for leave transaction ledger.
 */
public class LeaveTransactionRepository {

    /**
     * Saves a new leave transaction.
     *
     * @param employeeId   employee identifier
     * @param leaveType   type of leave used
     * @param days        number of days deducted
     * @param cutOffPeriod cutoff period label
     */
    public void save(String employeeId, LeaveType leaveType, int days, String cutOffPeriod)
            throws SQLException {
        String sql = "INSERT INTO leave_transactions "
                   + "(employee_id, leave_type, days, cutoff_period) "
                   + "VALUES (?, ?, ?, ?)";

        try (Connection connection = Database.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, employeeId);
            stmt.setString(2, leaveType.name());
            stmt.setInt(3, days);
            stmt.setString(4, cutOffPeriod);
            stmt.executeUpdate();
        }
    }

    /**
     * Finds all leave transactions for the given employee.
     *
     * @param employeeId employee identifier
     * @return list of transactions ordered by date descending
     */
    public List<LeaveTransaction> findByEmployeeId(String employeeId) throws SQLException {
        String sql = "SELECT id, employee_id, leave_type, days, cutoff_period, created_at "
                   + "FROM leave_transactions WHERE employee_id = ? "
                   + "ORDER BY created_at DESC";

        List<LeaveTransaction> list = new ArrayList<>();

        try (Connection connection = Database.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, employeeId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(buildLeaveTransaction(rs));
                }
            }
        }
        return list;
    }

    /**
     * Finds all leave transactions for a given employee and cutoff period.
     *
     * @param employeeId    employee identifier
     * @param cutOffPeriod  cutoff period label
     * @return list of matching transactions
     */
    public List<LeaveTransaction> findByEmployeeAndPeriod(String employeeId, String cutOffPeriod)
            throws SQLException {
        String sql = "SELECT id, employee_id, leave_type, days, cutoff_period, created_at "
                   + "FROM leave_transactions "
                   + "WHERE employee_id = ? AND cutoff_period = ? "
                   + "ORDER BY created_at DESC";

        List<LeaveTransaction> list = new ArrayList<>();

        try (Connection connection = Database.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, employeeId);
            stmt.setString(2, cutOffPeriod);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(buildLeaveTransaction(rs));
                }
            }
        }
        return list;
    }

    private LeaveTransaction buildLeaveTransaction(ResultSet rs) throws SQLException {
        return new LeaveTransaction(
                rs.getInt("id"),
                rs.getString("employee_id"),
                LeaveType.valueOf(rs.getString("leave_type")),
                rs.getInt("days"),
                rs.getString("cutoff_period"),
                rs.getString("created_at"));
    }
}