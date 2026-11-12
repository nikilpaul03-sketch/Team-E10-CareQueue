package com.teame10.healthcare.exception;

/**
 * Thrown when queue token operations (generation, check-in, calling, skipping) fail or violate sequence rules.
 */
public class QueueTokenException extends RuntimeException {
    public QueueTokenException(String message) {
        super(message);
    }
}
