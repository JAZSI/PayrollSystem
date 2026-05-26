package com.com253.payrollsystem.infrastructure.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.UUID;
import org.mindrot.jbcrypt.BCrypt;

public final class Database {

    private static final String URL = "jdbc:sqlite:payroll.db";

    private Database() {}

    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(URL);
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA busy_timeout = 3000");
            stmt.execute("PRAGMA journal_mode = WAL");
            stmt.execute("PRAGMA foreign_keys = ON");
        }
        return conn;
    }

    public static void close(Connection conn) {
        if (conn != null) {
            try { conn.close(); } catch (SQLException ignored) {}
        }
    }

    public static void initialize() throws SQLException, IOException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(loadSchema());
        }
        seedAdminIfEmpty();
    }

    public static void wipeAndReinitialize() throws SQLException, IOException {
        Connection c = null;
        try { c = getConnection(); } catch (SQLException e) {}
        close(c);
        java.nio.file.Files.deleteIfExists(java.nio.file.Paths.get("payroll.db"));
        java.nio.file.Files.deleteIfExists(java.nio.file.Paths.get("payroll.db-wal"));
        java.nio.file.Files.deleteIfExists(java.nio.file.Paths.get("payroll.db-shm"));
        initialize();
    }

    private static void seedAdminIfEmpty() throws SQLException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS c FROM accounts")) {
            if (rs.next() && rs.getInt("c") == 0) {
                String adminPassword = System.getProperty("payroll.admin.password");
                if (adminPassword == null || adminPassword.trim().isEmpty()) {
                    adminPassword = System.getenv("PAYROLL_ADMIN_PASSWORD");
                }
                if (adminPassword == null || adminPassword.trim().isEmpty()) {
                    adminPassword = UUID.randomUUID().toString().substring(0, 12);
                    System.err.println("[WARNING] No payroll.admin.password or PAYROLL_ADMIN_PASSWORD set. Generated temporary password: " + adminPassword);
                }
                String hash = BCrypt.hashpw(adminPassword, BCrypt.gensalt(10));
                String sql = "INSERT INTO accounts (username, password_hash, role) VALUES (?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, "admin");
                    pstmt.setString(2, hash);
                    pstmt.setString(3, "ADMIN");
                    pstmt.executeUpdate();
                }
            }
        }
    }

    private static void insertEmployee(Connection conn, String id, String name, String type, double rate,
                                       int sickLeave, int vacationLeave, int emergencyLeave, double loanBalance) throws SQLException {
        String sql = "INSERT OR IGNORE INTO employees (id, name, type, rate, sick_leave, vacation_leave, emergency_leave, loan_balance) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.setString(2, name);
            pstmt.setString(3, type);
            pstmt.setDouble(4, rate);
            pstmt.setInt(5, sickLeave);
            pstmt.setInt(6, vacationLeave);
            pstmt.setInt(7, emergencyLeave);
            pstmt.setDouble(8, loanBalance);
            pstmt.executeUpdate();
        }
    }

    private static void insertAttendance(Connection conn, String employeeId, LocalDate date, double timeIn, double timeOut) throws SQLException {
        String sql = "INSERT OR IGNORE INTO attendance (employee_id, record_date, time_in, time_out) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, employeeId);
            pstmt.setString(2, date.toString());
            pstmt.setDouble(3, timeIn);
            pstmt.setDouble(4, timeOut);
            pstmt.executeUpdate();
        }
    }

    private static String loadSchema() throws IOException {
        try (InputStream inputStream = Database.class.getResourceAsStream("/db/schema.sql")) {
            if (inputStream == null) {
                throw new IOException("Schema file not found: /db/schema.sql");
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
