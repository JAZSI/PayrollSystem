package com.com253.payrollsystem.Repository;

import com.com253.payrollsystem.Util.Database;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Base class for all repositories. Provides per-query connections that close
 * immediately after use — safe for multi-threaded JavaFX background tasks.
 * WAL mode makes SQLite handle concurrent reads without interference.
 */
public abstract class BaseRepository {

    /**
     * Opens a fresh connection with recommended SQLite PRAGMAs.
     * Caller must call {@link Database#close(Connection)} when done.
     */
    protected static Connection getConnection() throws SQLException {
        return Database.getConnection();
    }

    /**
     * Safely closes a connection, swallowing any SQLException.
     */
    protected static void close(Connection c) {
        Database.close(c);
    }
}