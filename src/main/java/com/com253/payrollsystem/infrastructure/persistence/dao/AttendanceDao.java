package com.com253.payrollsystem.infrastructure.persistence.dao;

import com.com253.payrollsystem.app.port.AttendanceRepositoryPort;
import com.com253.payrollsystem.domain.model.AttendanceRecord;
import com.com253.payrollsystem.infrastructure.persistence.sqlite.AttendanceRepository;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Thin attendance DAO wrapper so presentation/service code can depend on a
 * stable type while reusing the existing SQLite repository implementation.
 */
public class AttendanceDao implements AttendanceRepositoryPort {

    private final AttendanceRepositoryPort delegate;

    public AttendanceDao() {
        this(new AttendanceRepository());
    }

    public AttendanceDao(AttendanceRepositoryPort delegate) {
        this.delegate = delegate;
    }

    @Override
    public void clockIn(String employeeId, LocalDate date, double timeIn) throws SQLException {
        delegate.clockIn(employeeId, date, timeIn);
    }

    @Override
    public void clockOut(String employeeId, LocalDate date, double timeOut) throws SQLException {
        delegate.clockOut(employeeId, date, timeOut);
    }

    @Override
    public List<AttendanceRecord> getAttendance(String employeeId, LocalDate from, LocalDate to) throws SQLException {
        return delegate.getAttendance(employeeId, from, to);
    }

    @Override
    public void updateTimeIn(String employeeId, LocalDate date, double timeIn) throws SQLException {
        delegate.updateTimeIn(employeeId, date, timeIn);
    }

    @Override
    public void updateTimeOut(String employeeId, LocalDate date, double timeOut) throws SQLException {
        delegate.updateTimeOut(employeeId, date, timeOut);
    }

    @Override
    public void deleteByEmployeeAndDate(String employeeId, LocalDate date) throws SQLException {
        delegate.deleteByEmployeeAndDate(employeeId, date);
    }

    @Override
    public void upsert(String employeeId, LocalDate date, Double timeIn, Double timeOut) throws SQLException {
        delegate.upsert(employeeId, date, timeIn, timeOut);
    }
}