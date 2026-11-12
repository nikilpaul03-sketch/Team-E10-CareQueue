package com.teame10.healthcare.exception;

/**
 * Thrown when an appointment booking conflicts with an already reserved slot for a doctor or patient.
 */
public class DuplicateBookingException extends RuntimeException {
    public DuplicateBookingException(String message) {
        super(message);
    }
}
