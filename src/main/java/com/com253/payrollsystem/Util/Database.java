package com.com253.payrollsystem.Util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class Database {

    private static final String URL = "jdbc:sqlite:payroll.db";

    private Database() {
    }
    
    

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void initialize() throws SQLException, IOException {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(loadSchema());
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