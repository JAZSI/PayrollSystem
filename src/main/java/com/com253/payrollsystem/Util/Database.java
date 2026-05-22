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
    private static Connection connection;

    private Database() {}

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL);
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("PRAGMA busy_timeout = 3000");
                stmt.execute("PRAGMA journal_mode = WAL");
            }
        }
        return connection;
    }

    public static void initialize() throws SQLException, IOException {
        try (Connection conn = getConnection();
             Statement statement = conn.createStatement()) {
            statement.executeUpdate(loadSchema());
        }
        seedAdminIfEmpty();
    }

    private static void seedAdminIfEmpty() throws SQLException {
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS c FROM accounts")) {
            if (rs.next() && rs.getInt("c") == 0) {
                String hash = BCrypt.hashpw("admin123", BCrypt.gensalt(10));
                try (Statement insert = getConnection().createStatement()) {
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