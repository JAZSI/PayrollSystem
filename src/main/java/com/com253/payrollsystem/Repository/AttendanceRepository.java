package com.com253.payrollsystem.Repository;

import com.com253.payrollsystem.Model.AttendanceRecord;
import com.com253.payrollsystem.Util.Database;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles all database operations for employee attendance records.
 */
public class AttendanceRepository {

    /**
     * Records a clock-in for the given employee on the given date.
     * Ignored if the employee has already clocked in that day.
     *
     * @param employeeId employee identifier
     * @param date       date of clock-in
     * @param timeIn     clock-in time in decimal hours
     */
    public void clockIn(String employeeId, LocalDate date, double timeIn) throws SQLException {
        String checkSql = "SELECT 1 FROM attendance WHERE employee_id = ? AND record_date = ?";
        String insertSql = "INSERT INTO attendance (employee_id, record_date, time_in) "
                + "VALUES (?, ?, ?)";
        
        try (Connection connection = Database.getConnection()) {
            
            try (PreparedStatement check = connection.prepareStatement(checkSql)) {
                check.setString(1, employeeId);
                check.setDate(2, Date.valueOf(date));
                
                try (ResultSet rs = check.executeQuery()) {
                    if (rs.next()) {
                        return; // already clocked in
                    }
                }
            }
            
            try (PreparedStatement insert = connection.prepareStatement(insertSql)) {
                insert.setString(1, employeeId);
                insert.setDate(2, Date.valueOf(date));
                insert.setDouble(3, timeIn);
                insert.executeUpdate();
            }
        }
    }
    /**
     * Records a clock-out for the given employee on the given date.
     * If no clock-in exists for that day, inserts a new row with only time-out.
     *
     * @param employeeId employee identifier
     * @param date       date of clock-out
     * @param timeOut    clock-out time in decimal hours
     */
    public void clockOut(String employeeId, LocalDate date, double timeOut) throws SQLException {
        String updateSql = "UPDATE attendance SET time_out = ? "
                + "WHERE employee_id = ? AND record_date = ?";
        String insertSql = "INSERT INTO attendance (employee_id, record_date, time_out) "
                + "VALUES (?, ?, ?)";
        
        try (Connection connection = Database.getConnection()) {
            
            try (PreparedStatement update = connection.prepareStatement(updateSql)) {
                update.setDouble(1, timeOut);
                update.setString(2, employeeId);
                update.setDate(3, Date.valueOf(date));
                int rows = update.executeUpdate();
                
                if (rows == 0) {
                    
                    try (PreparedStatement insert = connection.prepareStatement(insertSql)) {
                        insert.setString(1, employeeId);
                        insert.setDate(2, Date.valueOf(date));
                        insert.setDouble(3, timeOut);
                        insert.executeUpdate();
                    }
                }
            }
        }
    }
    
    /**
     * Fetches attendance records for an employee within a date range.
     *
     * @param employeeId employee identifier
     * @param from       start date inclusive
     * @param to         end date inclusive
     * @return list of attendance records ordered by date
     */
    public List<AttendanceRecord> getAttendance(String employeeId, LocalDate from, LocalDate to) throws SQLException {
        String sql = "SELECT employee_id, record_date, time_in, time_out "
                   + "FROM attendance WHERE employee_id = ? "
                   + "AND record_date >= ? AND record_date <= ? "
                   + "ORDER BY record_date ASC";
        
        List<AttendanceRecord> records = new ArrayList<>();
        
        try (Connection connection = Database.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setString(1, employeeId);
            stmt.setDate(2, Date.valueOf(from));
            stmt.setDate(3, Date.valueOf(to));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Double timeIn = rs.getObject("time_in") != null ? rs.getDouble("time_in") : null;
                    Double timeOut = rs.getObject("time_out") != null ? rs.getDouble("time_out") : null;
                
                    records.add(new AttendanceRecord(
                            rs.getString("employee_id"),
                            rs.getDate("record_date").toLocalDate(),
                            timeIn,
                            timeOut));
                }
            }
        }
        return records;
    }
}
