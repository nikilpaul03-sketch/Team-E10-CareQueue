package com.teame10.healthcare.exception;

/**
 * Thrown when a doctor schedule session is full or a slot time is outside clinic hours.
 */
public class SlotNotAvailableException extends RuntimeException {
    public SlotNotAvailableException(String message) {
        super(message);
    }
}
