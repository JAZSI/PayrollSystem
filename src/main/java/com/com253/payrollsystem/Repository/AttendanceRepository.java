package com.com253.payrollsystem.Repository;

import com.com253.payrollsystem.Model.AttendanceRecord;
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
public class AttendanceRepository extends BaseRepository {

    public void clockIn(String employeeId, LocalDate date, double timeIn) throws SQLException {
        String checkSql = "SELECT 1 FROM attendance WHERE employee_id = ? AND record_date = ?";
        String insertSql = "INSERT INTO attendance (employee_id, record_date, time_in) VALUES (?, ?, ?)";

        Connection connection = getConnection();
        try {
            PreparedStatement check = connection.prepareStatement(checkSql);
            try {
                check.setString(1, employeeId);
                check.setDate(2, Date.valueOf(date));
                ResultSet rs = check.executeQuery();
                try {
                    if (rs.next()) return;
                } finally {
                    rs.close();
                }
            } finally {
                check.close();
            }

            PreparedStatement insert = connection.prepareStatement(insertSql);
            try {
                insert.setString(1, employeeId);
                insert.setDate(2, Date.valueOf(date));
                insert.setDouble(3, timeIn);
                insert.executeUpdate();
            } finally {
                insert.close();
            }
        } finally {
            close(connection);
        }
    }

    public void clockOut(String employeeId, LocalDate date, double timeOut) throws SQLException {
        String updateSql = "UPDATE attendance SET time_out = ? WHERE employee_id = ? AND record_date = ?";
        String insertSql = "INSERT INTO attendance (employee_id, record_date, time_out) VALUES (?, ?, ?)";

        Connection connection = getConnection();
        try {
            PreparedStatement update = connection.prepareStatement(updateSql);
            try {
                update.setDouble(1, timeOut);
                update.setString(2, employeeId);
                update.setDate(3, Date.valueOf(date));
                int rows = update.executeUpdate();
                if (rows == 0) {
                    PreparedStatement insert = connection.prepareStatement(insertSql);
                    try {
                        insert.setString(1, employeeId);
                        insert.setDate(2, Date.valueOf(date));
                        insert.setDouble(3, timeOut);
                        insert.executeUpdate();
                    } finally {
                        insert.close();
                    }
                }
            } finally {
                update.close();
            }
        } finally {
            close(connection);
        }
    }

    public List<AttendanceRecord> getAttendance(String employeeId, LocalDate from, LocalDate to) throws SQLException {
        String sql = "SELECT employee_id, record_date, time_in, time_out "
                   + "FROM attendance WHERE employee_id = ? "
                   + "AND record_date >= ? AND record_date <= ? "
                   + "ORDER BY record_date ASC";

        List<AttendanceRecord> records = new ArrayList<>();
        Connection connection = getConnection();
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            try {
                stmt.setString(1, employeeId);
                stmt.setDate(2, Date.valueOf(from));
                stmt.setDate(3, Date.valueOf(to));
                ResultSet rs = stmt.executeQuery();
                try {
                    while (rs.next()) {
                        Double timeIn = rs.getObject("time_in") != null ? rs.getDouble("time_in") : null;
                        Double timeOut = rs.getObject("time_out") != null ? rs.getDouble("time_out") : null;
                        records.add(new AttendanceRecord(
                                rs.getString("employee_id"),
                                rs.getDate("record_date").toLocalDate(),
                                timeIn,
                                timeOut));
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
        return records;
    }

    public void updateTimeIn(String employeeId, LocalDate date, double timeIn) throws SQLException {
        String sql = "UPDATE attendance SET time_in = ? WHERE employee_id = ? AND record_date = ?";
        Connection connection = getConnection();
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            try {
                stmt.setDouble(1, timeIn);
                stmt.setString(2, employeeId);
                stmt.setDate(3, Date.valueOf(date));
                stmt.executeUpdate();
            } finally {
                stmt.close();
            }
        } finally {
            close(connection);
        }
    }

    public void updateTimeOut(String employeeId, LocalDate date, double timeOut) throws SQLException {
        String sql = "UPDATE attendance SET time_out = ? WHERE employee_id = ? AND record_date = ?";
        Connection connection = getConnection();
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            try {
                stmt.setDouble(1, timeOut);
                stmt.setString(2, employeeId);
                stmt.setDate(3, Date.valueOf(date));
                stmt.executeUpdate();
            } finally {
                stmt.close();
            }
        } finally {
            close(connection);
        }
    }

    public void deleteByEmployeeAndDate(String employeeId, LocalDate date) throws SQLException {
        String sql = "DELETE FROM attendance WHERE employee_id = ? AND record_date = ?";
        Connection connection = getConnection();
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            try {
                stmt.setString(1, employeeId);
                stmt.setDate(2, Date.valueOf(date));
                stmt.executeUpdate();
            } finally {
                stmt.close();
            }
        } finally {
            close(connection);
        }
    }

    public void upsert(String employeeId, LocalDate date, Double timeIn, Double timeOut) throws SQLException {
        String sql = "INSERT INTO attendance (employee_id, record_date, time_in, time_out) "
                   + "VALUES (?, ?, ?, ?) "
                   + "ON CONFLICT(employee_id, record_date) DO UPDATE SET "
                   + "time_in = COALESCE(excluded.time_in, attendance.time_in), "
                   + "time_out = COALESCE(excluded.time_out, attendance.time_out)";
        Connection connection = getConnection();
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            try {
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
                stmt.executeUpdate();
            } finally {
                stmt.close();
            }
        } finally {
            close(connection);
        }
    }
}