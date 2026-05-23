package com.com253.payrollsystem.Repository;

import com.com253.payrollsystem.Model.LeaveTransaction;
import com.com253.payrollsystem.Model.LeaveTransaction.LeaveType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository for leave transaction ledger entries.
 * Separate from EmployeeRepository to keep leave lifecycle independent.
 */
public class LeaveTransactionRepository extends BaseRepository {

    public void save(String employeeId, LeaveType leaveType, int days, String cutOffPeriod) throws SQLException {
        String sql = "INSERT INTO leave_transactions (employee_id, leave_type, days, cutoff_period) VALUES (?, ?, ?, ?)";
        Connection connection = getConnection();
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            try {
                stmt.setString(1, employeeId);
                stmt.setString(2, leaveType.name());
                stmt.setInt(3, days);
                stmt.setString(4, cutOffPeriod);
                stmt.executeUpdate();
            } finally {
                stmt.close();
            }
        } finally {
            close(connection);
        }
    }

    public List<LeaveTransaction> findByEmployeeId(String employeeId) throws SQLException {
        String sql = "SELECT id, employee_id, leave_type, days, cutoff_period, created_at "
                   + "FROM leave_transactions WHERE employee_id = ? ORDER BY created_at DESC";
        List<LeaveTransaction> list = new ArrayList<>();
        Connection connection = getConnection();
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            try {
                stmt.setString(1, employeeId);
                ResultSet rs = stmt.executeQuery();
                try {
                    while (rs.next()) {
                        list.add(new LeaveTransaction(
                            rs.getInt("id"),
                            rs.getString("employee_id"),
                            LeaveType.valueOf(rs.getString("leave_type")),
                            rs.getInt("days"),
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