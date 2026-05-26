package com.com253.payrollsystem.infrastructure.persistence.sqlite;

import com.com253.payrollsystem.app.port.AttendanceRepositoryPort;

import com.com253.payrollsystem.domain.model.AttendanceRecord;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import com.com253.payrollsystem.infrastructure.persistence.jdbc.JdbcTemplate;
import com.com253.payrollsystem.infrastructure.persistence.DataAccessException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;

/**
 * Handles all database operations for employee attendance records.
 */
public class AttendanceRepository implements AttendanceRepositoryPort {

    public void clockIn(String employeeId, LocalDate date, double timeIn) throws SQLException {
        try {
            String checkSql = "SELECT 1 FROM attendance WHERE employee_id = ? AND record_date = ?";
            String insertSql = "INSERT INTO attendance (employee_id, record_date, time_in) VALUES (?, ?, ?)";

            boolean exists = JdbcTemplate.queryForObject(checkSql, stmt -> {
                stmt.setString(1, employeeId);
                stmt.setDate(2, Date.valueOf(date));
            }, rs -> 1).isPresent();

            if (!exists) {
                JdbcTemplate.update(insertSql, stmt -> {
                    stmt.setString(1, employeeId);
                    stmt.setDate(2, Date.valueOf(date));
                    stmt.setDouble(3, timeIn);
                });
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to clock in for employee: " + employeeId, e);
        }
    }

    public void clockOut(String employeeId, LocalDate date, double timeOut) throws SQLException {
        try {
            String updateSql = "UPDATE attendance SET time_out = ? WHERE employee_id = ? AND record_date = ?";
            String insertSql = "INSERT INTO attendance (employee_id, record_date, time_out) VALUES (?, ?, ?)";

            int rows = JdbcTemplate.update(updateSql, stmt -> {
                stmt.setDouble(1, timeOut);
                stmt.setString(2, employeeId);
                stmt.setDate(3, Date.valueOf(date));
            });
            if (rows == 0) {
                JdbcTemplate.update(insertSql, stmt -> {
                    stmt.setString(1, employeeId);
                    stmt.setDate(2, Date.valueOf(date));
                    stmt.setDouble(3, timeOut);
                });
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to clock out for employee: " + employeeId, e);
        }
    }

    public List<AttendanceRecord> getAttendance(String employeeId, LocalDate from, LocalDate to) throws SQLException {
        try {
            String sql = "SELECT employee_id, record_date, time_in, time_out "
                       + "FROM attendance WHERE employee_id = ? "
                       + "AND record_date >= ? AND record_date <= ? "
                       + "ORDER BY record_date ASC";

            return JdbcTemplate.query(sql, stmt -> {
                stmt.setString(1, employeeId);
                stmt.setDate(2, Date.valueOf(from));
                stmt.setDate(3, Date.valueOf(to));
            }, rs -> new AttendanceRecord(
                    rs.getString("employee_id"),
                    parseRecordDate(rs.getObject("record_date")),
                    rs.getObject("time_in") != null ? rs.getDouble("time_in") : null,
                    rs.getObject("time_out") != null ? rs.getDouble("time_out") : null));
        } catch (SQLException e) {
            throw new DataAccessException("Failed to fetch attendance for employee: " + employeeId, e);
        }
    }

    public void updateTimeIn(String employeeId, LocalDate date, double timeIn) throws SQLException {
        try {
            String sql = "UPDATE attendance SET time_in = ? WHERE employee_id = ? AND record_date = ?";
            JdbcTemplate.update(sql, stmt -> {
                stmt.setDouble(1, timeIn);
                stmt.setString(2, employeeId);
                stmt.setDate(3, Date.valueOf(date));
            });
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update time in for: " + employeeId, e);
        }
    }

    public void updateTimeOut(String employeeId, LocalDate date, double timeOut) throws SQLException {
        try {
            String sql = "UPDATE attendance SET time_out = ? WHERE employee_id = ? AND record_date = ?";
            JdbcTemplate.update(sql, stmt -> {
                stmt.setDouble(1, timeOut);
                stmt.setString(2, employeeId);
                stmt.setDate(3, Date.valueOf(date));
            });
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update time out for: " + employeeId, e);
        }
    }

    public void deleteByEmployeeAndDate(String employeeId, LocalDate date) throws SQLException {
        try {
            String sql = "DELETE FROM attendance WHERE employee_id = ? AND record_date = ?";
            JdbcTemplate.update(sql, stmt -> {
                stmt.setString(1, employeeId);
                stmt.setDate(2, Date.valueOf(date));
            });
        } catch (SQLException e) {
            throw new DataAccessException("Failed to delete attendance for: " + employeeId, e);
        }
    }

    public void upsert(String employeeId, LocalDate date, Double timeIn, Double timeOut) throws SQLException {
        try {
            String sql = "INSERT INTO attendance (employee_id, record_date, time_in, time_out) "
                       + "VALUES (?, ?, ?, ?) "
                       + "ON CONFLICT(employee_id, record_date) DO UPDATE SET "
                       + "time_in = COALESCE(excluded.time_in, attendance.time_in), "
                       + "time_out = COALESCE(excluded.time_out, attendance.time_out)";
            JdbcTemplate.update(sql, stmt -> {
                stmt.setString(1, employeeId);
                stmt.setDate(2, Date.valueOf(date));
                if (timeIn != null) {
                    stmt.setDouble(3, timeIn);
                } else {
                    stmt.setNull(3, java.sql.Types.DOUBLE);
                }
                if (timeOut != null) {
                    stmt.setDouble(4, timeOut);
                } else {
                    stmt.setNull(4, java.sql.Types.DOUBLE);
                }
            });
        } catch (SQLException e) {
            throw new DataAccessException("Failed to upsert attendance for: " + employeeId, e);
        }
    }

    private LocalDate parseRecordDate(Object value) throws SQLException {
        if (value == null) {
            throw new SQLException("Attendance record date is null");
        }

        if (value instanceof java.sql.Date sqlDate) {
            return sqlDate.toLocalDate();
        }

        if (value instanceof Number number) {
            return Instant.ofEpochMilli(number.longValue()).atZone(ZoneId.systemDefault()).toLocalDate();
        }

        String text = value.toString().trim();
        try {
            return LocalDate.parse(text);
        } catch (DateTimeParseException ignored) {
            try {
                long epochMillis = Long.parseLong(text);
                return Instant.ofEpochMilli(epochMillis).atZone(ZoneId.systemDefault()).toLocalDate();
            } catch (NumberFormatException ex) {
                throw new SQLException("Unsupported attendance record date format: " + text, ex);
            }
        }
    }
}