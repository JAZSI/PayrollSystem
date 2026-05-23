package com.com253.payrollsystem.Util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.mindrot.jbcrypt.BCrypt;

public final class Database {

    private static final String URL = "jdbc:sqlite:payroll.db";

    private Database() {}

    /**
     * Opens a new SQLite connection with WAL mode and FK enforcement enabled.
     * Caller is responsible for calling close() when done.
     */
    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(URL);
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA busy_timeout = 3000");
            stmt.execute("PRAGMA journal_mode = WAL");
            stmt.execute("PRAGMA foreign_keys = ON");
        }
        return conn;
    }

    /**
     * Closes a connection safely, suppressing checked exceptions.
     */
    public static void close(Connection conn) {
        if (conn != null) {
            try { conn.close(); } catch (SQLException ignored) {}
        }
    }

    /**
     * Initializes the schema and seeds the admin account.
     * Drops all tables first to ensure a clean schema with current FK CASCADE rules.
     */
    public static void initialize() throws SQLException, IOException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS employees");
            stmt.executeUpdate(loadSchema());
        }
        seedAdminIfEmpty();
    }

    /**
     * Deletes the DB file and all WAL/SHM sidecars, then re-initializes.
     * Use this before integration tests to ensure a fresh state.
     */
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
                String hash = BCrypt.hashpw("admin123", BCrypt.gensalt(10));
                try (Statement insert = conn.createStatement()) {
                    insert.executeUpdate(
                        "INSERT INTO accounts (username, password_hash, role) " +
                        "VALUES ('admin', '" + hash + "', 'ADMIN')");
                }
            }
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