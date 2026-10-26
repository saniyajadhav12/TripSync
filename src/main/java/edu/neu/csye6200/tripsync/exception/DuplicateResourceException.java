package edu.neu.csye6200.tripsync.exception;

// Thrown when a unique value such as a username or email is already taken (mapped to 409)
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
