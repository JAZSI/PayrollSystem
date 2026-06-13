package com.com253.payrollsystem.shared.error;

/** Thrown when a request conflicts with existing state, e.g. a duplicate id (maps to HTTP 409). */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
