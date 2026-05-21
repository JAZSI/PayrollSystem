package com.com253.payrollsystem.Repository;

import com.com253.payrollsystem.Model.PayrollEntry;
import com.com253.payrollsystem.Model.PayrollReportEntry;
import com.com253.payrollsystem.Util.Database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles all database operations for payroll history records.
 */
public class PayrollRepository {

    /**
     * Saves a payroll result to history with full field breakdown.
     * Silently ignored if a record already exists for this employee and period.
     *
     * @param entry the computed payroll result to save
     */
    public void save(PayrollEntry entry) throws SQLException {
        String sql = "INSERT OR IGNORE INTO payroll_entries "
                   + "(employee_id, cutoff_period, "
                   + "total_hours, overtime_hours, undertime_hours, absent_days, "
                   + "basic_pay, overtime_pay, holiday_pay, night_shift_differential, "
                   + "gross_pay, sss_deduction, philhealth_deduction, pagibig_deduction, "
                   + "tax_deduction, loan_deduction, undertime_penalty, absence_penalty, "
                   + "net_pay) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = Database.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1,  entry.getEmployee().getEmployeeId());
            stmt.setString(2,  entry.getCutOffPeriod());
            stmt.setDouble(3,  entry.getTotalHoursWorked());
            stmt.setDouble(4,  entry.getOvertimeHours());
            stmt.setDouble(5,  entry.getUndertimeHours());
            stmt.setInt(6,     entry.getAbsentDays());
            stmt.setDouble(7,  entry.getBasicPay());
            stmt.setDouble(8,  entry.getOvertimePay());
            stmt.setDouble(9,  entry.getHolidayPay());
            stmt.setDouble(10, entry.getNightShiftDifferential());
            stmt.setDouble(11, entry.getGrossPay());
            stmt.setDouble(12, entry.getSssDeduction());
            stmt.setDouble(13, entry.getPhilhealthDeduction());
            stmt.setDouble(14, entry.getPagibigDeduction());
            stmt.setDouble(15, entry.getTaxDeduction());
            stmt.setDouble(16, entry.getLoanDeduction());
            stmt.setDouble(17, entry.getUndertimePenalty());
            stmt.setDouble(18, entry.getAbsencePenalty());
            stmt.setDouble(19, entry.getNetPay());
            stmt.executeUpdate();
        }
    }
    
    /**
     * Finds a saved payroll for an employee and cutoff period.
     *
     * @param employeeId   employee identifier
     * @param cutOffPeriod cutoff period label
     * @return report entry if found, or null
     */
    public PayrollReportEntry findByEmployeeAndPeriod(String employeeId, String cutOffPeriod) throws SQLException {
        String sql = "SELECT pe.id, pe.employee_id, e.name AS employee_name, "
                   + "pe.cutoff_period, pe.total_hours, pe.overtime_hours, pe.undertime_hours, pe.absent_days, "
                   + "pe.basic_pay, pe.overtime_pay, pe.holiday_pay, pe.night_shift_differential, "
                   + "pe.gross_pay, pe.sss_deduction, pe.philhealth_deduction, pe.pagibig_deduction, "
                   + "pe.tax_deduction, pe.loan_deduction, pe.undertime_penalty, pe.absence_penalty, "
                   + "pe.net_pay, pe.created_at "
                   + "FROM payroll_entries pe "
                   + "JOIN employees e ON pe.employee_id = e.id "
                   + "WHERE pe.employee_id = ? AND pe.cutoff_period = ?";

        try (Connection connection = Database.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, employeeId);
            stmt.setString(2, cutOffPeriod);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
            return null;
        }
    }

    /**
     * Retrieves all payroll entries for a cutoff period, joined with employee names.
     *
     * @param cutOffPeriod cutoff period label
     * @return list of report entries sorted by employee name
     */
    public List<PayrollReportEntry> findByPeriod(String cutOffPeriod) throws SQLException {
        String sql = "SELECT pe.id, pe.employee_id, e.name AS employee_name, "
                   + "pe.cutoff_period, pe.total_hours, pe.overtime_hours, pe.undertime_hours, pe.absent_days, "
                   + "pe.basic_pay, pe.overtime_pay, pe.holiday_pay, pe.night_shift_differential, "
                   + "pe.gross_pay, pe.sss_deduction, pe.philhealth_deduction, pe.pagibig_deduction, "
                   + "pe.tax_deduction, pe.loan_deduction, pe.undertime_penalty, pe.absence_penalty, "
                   + "pe.net_pay, pe.created_at "
                   + "FROM payroll_entries pe "
                   + "JOIN employees e ON pe.employee_id = e.id "
                   + "WHERE pe.cutoff_period = ? "
                   + "ORDER BY e.name ASC";

        List<PayrollReportEntry> entries = new ArrayList<>();
        try (Connection connection = Database.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, cutOffPeriod);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    entries.add(mapRow(rs));
                }
            }
        }
        return entries;
    }

    /**
     * Retrieves all payroll entries across all periods, joined with employee names.
     *
     * @return list of all report entries sorted by period then employee name
     */
    public List<PayrollReportEntry> findAll() throws SQLException {
        String sql = "SELECT pe.id, pe.employee_id, e.name AS employee_name, "
                   + "pe.cutoff_period, pe.total_hours, pe.overtime_hours, pe.undertime_hours, pe.absent_days, "
                   + "pe.basic_pay, pe.overtime_pay, pe.holiday_pay, pe.night_shift_differential, "
                   + "pe.gross_pay, pe.sss_deduction, pe.philhealth_deduction, pe.pagibig_deduction, "
                   + "pe.tax_deduction, pe.loan_deduction, pe.undertime_penalty, pe.absence_penalty, "
                   + "pe.net_pay, pe.created_at "
                   + "FROM payroll_entries pe "
                   + "JOIN employees e ON pe.employee_id = e.id "
                   + "ORDER BY pe.cutoff_period ASC, e.name ASC";

        List<PayrollReportEntry> entries = new ArrayList<>();
        try (Connection connection = Database.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                entries.add(mapRow(rs));
            }
        }
        return entries;
    }

    /**
     * Maps the current result set row to a PayrollReportEntry.
     *
     * @param rs open result set positioned at a row
     * @return populated report entry
     */
    private PayrollReportEntry mapRow(ResultSet rs) throws SQLException {
        return new PayrollReportEntry(
            rs.getInt("id"),
            rs.getString("employee_id"),
            rs.getString("employee_name"),
            rs.getString("cutoff_period"),
            rs.getDouble("total_hours"),
            rs.getDouble("overtime_hours"),
            rs.getDouble("undertime_hours"),
            rs.getInt("absent_days"),
            rs.getDouble("basic_pay"),
            rs.getDouble("overtime_pay"),
            rs.getDouble("holiday_pay"),
            rs.getDouble("night_shift_differential"),
            rs.getDouble("gross_pay"),
            rs.getDouble("sss_deduction"),
            rs.getDouble("philhealth_deduction"),
            rs.getDouble("pagibig_deduction"),
            rs.getDouble("tax_deduction"),
            rs.getDouble("loan_deduction"),
            rs.getDouble("undertime_penalty"),
            rs.getDouble("absence_penalty"),
            rs.getDouble("net_pay"),
            rs.getString("created_at")
        );
    }
}
