package com.com253.payrollsystem.infrastructure.persistence;

import com.com253.payrollsystem.infrastructure.config.Database;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Small database helper kept intentionally thin for the assignment build.
 */
public final class DbUtil {

    private DbUtil() {
    }

    public static Connection getConnection() throws SQLException {
        return Database.getConnection();
    }

    public static void initialize() throws SQLException, IOException {
        Database.initialize();
    }

    public static void wipeAndReinitialize() throws SQLException, IOException {
        Database.wipeAndReinitialize();
    }
}