package com.com253.payrollsystem.Repository;

import com.com253.payrollsystem.Model.PayrollEntry;
import com.com253.payrollsystem.Util.Database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Handles all database operations for payroll history records.
 */
public class PayrollRepository {

    /**
     * Saves a payroll result to history.
     * Silently ignored if a record already exists for this employee and period.
     *
     * @param result the computed payroll result to save
     */
    public void save(PayrollEntry result) throws SQLException {
        String sql = "INSERT OR IGNORE INTO payroll_entries "
                   + "(employee_id, cutoff_period, gross_pay, net_pay) "
                   + "VALUES (?, ?, ?, ?)";
        
        try (Connection connection = Database.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setString(1, result.getEmployee().getEmployeeId());
            stmt.setString(2, result.getCutOffPeriod());
            stmt.setDouble(3, result.getGrossPay());
            stmt.setDouble(4, result.getNetPay());
            stmt.executeUpdate();
        }
    }
    
    /**
     * Finds a saved payroll for an employee and cutoff period.
     *
     * @param employeeId   employee identifier
     * @param cutOffPeriod cutoff period label
     * @return gross pay if found, or null if no record exists
     */
    public Double findByEmployeeAndPeriod(String employeeId, String cutOffPeriod) throws SQLException {
        String sql = "SELECT gross_pay, net_pay FROM payroll_entries "
                   + "WHERE employee_id = ? AND cutoff_period = ?";
        
        try (Connection connection = Database.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setString(1, employeeId);
            stmt.setString(2, cutOffPeriod);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("gross_pay");
                }
            }
            return null;
        }
    }
}
