package com.com253.payrollsystem.infrastructure.persistence.jdbc;

import com.com253.payrollsystem.infrastructure.config.Database;
import com.com253.payrollsystem.infrastructure.config.TransactionManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class JdbcTemplate {

    private JdbcTemplate() {}

    @FunctionalInterface
    public interface PreparedStatementSetter {
        void setValues(PreparedStatement ps) throws SQLException;
    }

    @FunctionalInterface
    public interface RowMapper<T> {
        T mapRow(ResultSet rs) throws SQLException;
    }

    public static int update(String sql) throws SQLException {
        return update(sql, null);
    }

    public static int update(String sql, PreparedStatementSetter setter) throws SQLException {
        Connection conn = TransactionManager.getCurrentConnection();
        boolean externalConn = (conn != null);
        if (!externalConn) conn = Database.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            if (setter != null) setter.setValues(ps);
            return ps.executeUpdate();
        } finally {
            if (!externalConn) {
                try { conn.close(); } catch (SQLException ignored) {}
            }
        }
    }

    public static <T> List<T> query(String sql, RowMapper<T> mapper) throws SQLException {
        return query(sql, null, mapper);
    }

    public static <T> List<T> query(String sql, PreparedStatementSetter setter, RowMapper<T> mapper) throws SQLException {
        List<T> list = new ArrayList<>();
        Connection conn = TransactionManager.getCurrentConnection();
        boolean externalConn = (conn != null);
        if (!externalConn) conn = Database.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            if (setter != null) setter.setValues(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapper.mapRow(rs));
                }
            }
        } finally {
            if (!externalConn) {
                try { conn.close(); } catch (SQLException ignored) {}
            }
        }
        return list;
    }

    public static <T> Optional<T> queryForObject(String sql, PreparedStatementSetter setter, RowMapper<T> mapper) throws SQLException {
        Connection conn = TransactionManager.getCurrentConnection();
        boolean externalConn = (conn != null);
        if (!externalConn) conn = Database.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            if (setter != null) setter.setValues(ps);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.ofNullable(mapper.mapRow(rs));
            }
        } finally {
            if (!externalConn) {
                try { conn.close(); } catch (SQLException ignored) {}
            }
        }
        return Optional.empty();
    }
}
