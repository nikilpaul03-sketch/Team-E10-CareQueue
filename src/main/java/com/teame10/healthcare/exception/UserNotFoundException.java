package com.teame10.healthcare.exception;

/**
 * Thrown when a user record is not found by ID or username.
 */
public class UserNotFoundException extends ResourceNotFoundException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
