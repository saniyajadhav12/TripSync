package edu.neu.csye6200.tripsync.exception;

// Thrown when a resource does not exist or does not belong to the current user (mapped to 404)
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
