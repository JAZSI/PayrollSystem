package com.com253.payrollsystem.settings;

import com.com253.payrollsystem.shared.domain.PayrollSettings;
import com.com253.payrollsystem.settings.dto.SettingsRequest;
import com.com253.payrollsystem.settings.dto.SettingsResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Manages the single payroll settings row and exposes it as a domain {@link PayrollSettings}. */
@Service
@Transactional
public class SettingsService {

    private final SettingsRepository repository;

    public SettingsService(SettingsRepository repository) {
        this.repository = repository;
    }

    /** Returns the current settings, creating a sensible default row on first access. */
    public SettingsEntity getOrCreateDefault() {
        return repository.findById(1).orElseGet(() ->
                repository.save(new SettingsEntity(26, 8.0, 17.0, 11.0, 5, 5, 0, 0)));
    }

    @Transactional(readOnly = true)
    public SettingsResponse get() {
        return toResponse(getOrCreateDefault());
    }

    public SettingsResponse update(SettingsRequest req) {
        // Validate the invariants via the domain value object (throws -> HTTP 400).
        toDomain(req.workingDays(), req.workdayStartHour(), req.overtimeStartHour(),
                req.lunchStartHour(), req.leaveRegular(), req.leaveProbationary(),
                req.leaveContractual(), req.leavePartTimer());

        SettingsEntity e = getOrCreateDefault();
        e.setWorkingDays(req.workingDays());
        e.setWorkdayStartHour(req.workdayStartHour());
        e.setOvertimeStartHour(req.overtimeStartHour());
        e.setLunchStartHour(req.lunchStartHour());
        e.setLeaveRegular(req.leaveRegular());
        e.setLeaveProbationary(req.leaveProbationary());
        e.setLeaveContractual(req.leaveContractual());
        e.setLeavePartTimer(req.leavePartTimer());
        return toResponse(repository.save(e));
    }

    /** Builds the domain settings object used by the payroll calculator. */
    public PayrollSettings toDomain(SettingsEntity e) {
        return toDomain(e.getWorkingDays(), e.getWorkdayStartHour(), e.getOvertimeStartHour(),
                e.getLunchStartHour(), e.getLeaveRegular(), e.getLeaveProbationary(),
                e.getLeaveContractual(), e.getLeavePartTimer());
    }

    private PayrollSettings toDomain(int workingDays, double workdayStart, double overtimeStart,
                                     double lunchStart, int lr, int lp, int lc, int lpt) {
        return new PayrollSettings(workingDays, workdayStart, overtimeStart, lunchStart,
                lr, lp, lc, lpt);
    }

    private static SettingsResponse toResponse(SettingsEntity e) {
        return new SettingsResponse(
                e.getWorkingDays(), e.getWorkdayStartHour(), e.getOvertimeStartHour(),
                e.getLunchStartHour(), e.getLeaveRegular(), e.getLeaveProbationary(),
                e.getLeaveContractual(), e.getLeavePartTimer());
    }
}
