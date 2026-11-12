package com.teame10.healthcare.exception;

/**
 * Thrown when an appointment is not found by ID or reference code.
 */
public class AppointmentNotFoundException extends ResourceNotFoundException {
    public AppointmentNotFoundException(String message) {
        super(message);
    }
}
