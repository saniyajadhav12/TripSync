package edu.neu.csye6200.tripsync.dto;

import edu.neu.csye6200.tripsync.model.User;

// Public view of a user. Never includes the password hash.
public record UserResponse(Long id, String firstName, String lastName, String email, String username) {

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getUsername());
    }
}
