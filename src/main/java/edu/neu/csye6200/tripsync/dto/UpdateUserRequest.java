package edu.neu.csye6200.tripsync.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

// A null or blank password leaves the current password unchanged
public record UpdateUserRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank @Email String email,
        @NotBlank String username,
        @Pattern(regexp = "^\\s*$|^.{8,}$", message = "must be blank (unchanged) or at least 8 characters")
        String password) {
}
