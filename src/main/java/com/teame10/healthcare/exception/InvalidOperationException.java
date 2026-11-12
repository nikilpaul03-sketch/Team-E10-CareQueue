package com.teame10.healthcare.exception;

/**
 * Thrown when an illegal business state transition is requested (e.g. canceling or rescheduling completed appointments).
 */
public class InvalidOperationException extends RuntimeException {
    public InvalidOperationException(String message) {
        super(message);
    }
}
