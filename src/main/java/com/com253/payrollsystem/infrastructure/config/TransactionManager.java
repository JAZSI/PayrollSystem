package com.com253.payrollsystem.infrastructure.config;

import java.sql.Connection;
import java.sql.SQLException;

public final class TransactionManager {
    private static final ThreadLocal<Connection> CURRENT = new ThreadLocal<>();

    private TransactionManager() {}

    public static void begin(Connection conn) throws SQLException {
        conn.setAutoCommit(false);
        CURRENT.set(conn);
    }

    public static void commit(Connection conn) throws SQLException {
        try {
            conn.commit();
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
            CURRENT.remove();
        }
    }

    public static void rollback(Connection conn) {
        try {
            conn.rollback();
        } catch (SQLException ignored) {
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
            CURRENT.remove();
        }
    }

    /**
     * Returns the current thread-bound connection if a transaction has been begun,
     * otherwise null.
     */
    public static Connection getCurrentConnection() {
        return CURRENT.get();
    }
}
