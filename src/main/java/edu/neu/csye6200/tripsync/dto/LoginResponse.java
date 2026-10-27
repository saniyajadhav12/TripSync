package edu.neu.csye6200.tripsync.dto;

public record LoginResponse(String token, UserResponse user) {
}
