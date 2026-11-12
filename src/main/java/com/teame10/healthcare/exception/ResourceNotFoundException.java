package com.teame10.healthcare.exception;

/**
 * Base exception for requested resources that cannot be located.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
