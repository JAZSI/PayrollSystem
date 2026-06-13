package com.com253.payrollsystem.shared.error;

import java.time.Instant;
import java.util.Map;

/** Consistent JSON error body. */
public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> fieldErrors) {
}
