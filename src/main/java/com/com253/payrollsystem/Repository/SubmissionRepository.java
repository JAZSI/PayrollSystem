package com.com253.payrollsystem.Repository;

import com.com253.payrollsystem.Model.PayrollEntry;
import com.com253.payrollsystem.Model.PayrollReportEntry;
import com.com253.payrollsystem.Model.Submission;
import com.com253.payrollsystem.Util.Database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles all database operations for payroll submissions.
 */
public class SubmissionRepository {

    /**
     * Saves a new submission with PENDING status.
     * If an existing PENDING or REJECTED submission exists for the employee,
     * it is replaced. APPROVED submissions are never overwritten.
     *
     * @param submission the submission to save
     * @return false if an APPROVED submission already exists, true otherwise
     */
    public boolean save(Submission submission) throws SQLException {
        Submission existing = findByEmployeeId(submission.getEmployeeId());
        
        if (existing != null && existing.getStatus() == Submission.Status.APPROVED)
            return false;
        
        if (existing != null) {
            String sql = "UPDATE submissions SET leave_days = ?, ot_hours = ?, "
                       + "loan_deduction = ?, status = 'PENDING', "
                       + "submitted_at = CURRENT_TIMESTAMP WHERE employee_id = ?";
            
            try (Connection connection = Database.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
                
                stmt.setDouble(1, submission.getLeaveDays());
                stmt.setDouble(2, submission.getOtHours());
                stmt.setDouble(3, submission.getLoanDeduction());
                stmt.setString(4, submission.getEmployeeId());
                stmt.executeUpdate();
            }
        } else {
            String sql = "INSERT INTO submissions "
                       + "(employee_id, leave_days, ot_hours, loan_deduction) "
                       + "VALUES (?, ?, ?, ?)";
            
            try (Connection connection = Database.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
                
                stmt.setString(1, submission.getEmployeeId());
                stmt.setDouble(2, submission.getLeaveDays());
                stmt.setDouble(3, submission.getOtHours());
                stmt.setDouble(4, submission.getLoanDeduction());
                stmt.executeUpdate();
            }
        }
        return true;
    }
    
    /**
     * Finds the most recent submission for the given employee.
     *
     * @param employeeId employee identifier
     * @return matching submission, or null if none exists
     */
    public Submission findByEmployeeId(String employeeId) throws SQLException {
        String sql = "SELECT id, employee_id, leave_days, ot_hours, loan_deduction, "
                   + "status, submitted_at FROM submissions WHERE employee_id = ?";
        
        try (Connection connection = Database.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setString(1, employeeId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return buildSubmission(rs);
                }
            }
        }
        return null;    
    }
    
    /**
     * Finds a submission by its ID.
     *
     * @param id submission identifier
     * @return matching submission, or null if not found
     */    
    public Submission findById(int id) throws SQLException {
        String sql = "SELECT id, employee_id, leave_days, ot_hours, loan_deduction, "
                   + "status, submitted_at FROM submissions WHERE id = ?";
        
        try (Connection connection = Database.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return buildSubmission(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Returns all submissions currently in PENDING status.
     *
     * @return list of pending submissions
     */
    public List<Submission> findAllPending() throws SQLException {
        String sql = "SELECT id, employee_id, leave_days, ot_hours, loan_deduction, "
                   + "status, submitted_at FROM submissions WHERE status = 'PENDING'";
         
        List<Submission> list = new ArrayList<>();
         
        try (Connection connection = Database.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                list.add(buildSubmission(rs));
            }
        }
        return list;
    }
    
    /**
     * Updates the status of a submission by its ID.
     *
     * @param submissionId the submission to update
     * @param status       the new status to set
     */   
    public void updateStatus(int submissionId, Submission.Status status) throws SQLException {
        String sql = "UPDATE submissions SET status = ? WHERE id = ?";

        try (Connection connection = Database.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, status.name());
            stmt.setInt(2, submissionId);
            stmt.executeUpdate();
        }
    }

    /**
     * Deletes any submission for the given employee.
     *
     * @param employeeId employee identifier
     */
    public void deleteByEmployeeId(String employeeId) throws SQLException {
        String sql = "DELETE FROM submissions WHERE employee_id = ?";

        try (Connection connection = Database.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, employeeId);
            stmt.executeUpdate();
        }
    }
    
    /**
     * Builds a Submission object from the current row of a ResultSet.
     *
     * @param rs result set positioned at a valid row
     * @return constructed Submission object
     */    
    private Submission buildSubmission(ResultSet rs) throws SQLException {
        return new Submission(
                rs.getInt("id"),
                rs.getString("employee_id"),
                rs.getDouble("leave_days"),
                rs.getDouble("ot_hours"),
                rs.getDouble("loan_deduction"),
                Submission.Status.valueOf(rs.getString("status")),
                rs.getString("submitted_at"));
    }

    public void savePayrollEntry(PayrollEntry entry) throws SQLException {
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
                if (rs.next()) return mapPayrollRow(rs);
            }
        }
        return null;
    }

    public List<PayrollReportEntry> findByPeriod(String cutOffPeriod) throws SQLException {
        String sql = "SELECT pe.id, pe.employee_id, e.name AS employee_name, "
                   + "pe.cutoff_period, pe.total_hours, pe.overtime_hours, pe.undertime_hours, pe.absent_days, "
                   + "pe.basic_pay, pe.overtime_pay, pe.holiday_pay, pe.night_shift_differential, "
                   + "pe.gross_pay, pe.sss_deduction, pe.philhealth_deduction, pe.pagibig_deduction, "
                   + "pe.tax_deduction, pe.loan_deduction, pe.undertime_penalty, pe.absence_penalty, "
                   + "pe.net_pay, pe.created_at "
                   + "FROM payroll_entries pe "
                   + "JOIN employees e ON pe.employee_id = e.id "
                   + "WHERE pe.cutoff_period = ? ORDER BY e.name ASC";
        List<PayrollReportEntry> entries = new ArrayList<>();
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, cutOffPeriod);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) entries.add(mapPayrollRow(rs));
            }
        }
        return entries;
    }

    public List<PayrollReportEntry> findAllReports() throws SQLException {
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
            while (rs.next()) entries.add(mapPayrollRow(rs));
        }
        return entries;
    }

    private PayrollReportEntry mapPayrollRow(ResultSet rs) throws SQLException {
        return new PayrollReportEntry(
            rs.getInt("id"), rs.getString("employee_id"), rs.getString("employee_name"),
            rs.getString("cutoff_period"), rs.getDouble("total_hours"),
            rs.getDouble("overtime_hours"), rs.getDouble("undertime_hours"),
            rs.getInt("absent_days"), rs.getDouble("basic_pay"), rs.getDouble("overtime_pay"),
            rs.getDouble("holiday_pay"), rs.getDouble("night_shift_differential"),
            rs.getDouble("gross_pay"), rs.getDouble("sss_deduction"),
            rs.getDouble("philhealth_deduction"), rs.getDouble("pagibig_deduction"),
            rs.getDouble("tax_deduction"), rs.getDouble("loan_deduction"),
            rs.getDouble("undertime_penalty"), rs.getDouble("absence_penalty"),
            rs.getDouble("net_pay"), rs.getString("created_at"));
    }
}
