package com.com253.payrollsystem.Repository;

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
}
