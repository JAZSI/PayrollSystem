package com.com253.payrollsystem.shared.error;

/** Thrown when a requested resource does not exist (maps to HTTP 404). */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
