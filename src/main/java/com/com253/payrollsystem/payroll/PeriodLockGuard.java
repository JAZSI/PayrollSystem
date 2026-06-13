package com.com253.payrollsystem.payroll;

import org.springframework.stereotype.Component;

/** Blocks edits and runs once a period is LOCKED. */
@Component
public class PeriodLockGuard {

    private final PayrollRunRepository runRepository;

    public PeriodLockGuard(PayrollRunRepository runRepository) {
        this.runRepository = runRepository;
    }

    public boolean isLocked(String period) {
        return runRepository.existsByCutoffPeriodAndStatus(period, PayrollRunStatus.LOCKED);
    }

    /** Throws {@link IllegalStateException} (HTTP 400) if the period is locked. */
    public void ensureNotLocked(String period) {
        if (isLocked(period)) {
            throw new IllegalStateException(
                    "Cut-off period '" + period + "' is locked; no further changes are allowed.");
        }
    }
}
