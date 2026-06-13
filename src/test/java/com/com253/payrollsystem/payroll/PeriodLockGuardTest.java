package com.com253.payrollsystem.payroll;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PeriodLockGuardTest {

    @Mock PayrollRunRepository runRepository;
    @InjectMocks PeriodLockGuard guard;

    @Test
    void throwsWhenPeriodHasLockedRun() {
        when(runRepository.existsByCutoffPeriodAndStatus("1st-15th", PayrollRunStatus.LOCKED))
                .thenReturn(true);

        assertThatThrownBy(() -> guard.ensureNotLocked("1st-15th"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("locked");
    }

    @Test
    void allowsWhenPeriodNotLocked() {
        when(runRepository.existsByCutoffPeriodAndStatus("16th-30th", PayrollRunStatus.LOCKED))
                .thenReturn(false);

        assertThatCode(() -> guard.ensureNotLocked("16th-30th")).doesNotThrowAnyException();
    }
}
