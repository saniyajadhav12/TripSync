package edu.neu.csye6200.tripsync.controller;

import edu.neu.csye6200.tripsync.dto.LoginDTO;
import edu.neu.csye6200.tripsync.dto.LoginResponse;
import edu.neu.csye6200.tripsync.dto.SignupRequest;
import edu.neu.csye6200.tripsync.dto.UpdateUserRequest;
import edu.neu.csye6200.tripsync.dto.UserResponse;
import edu.neu.csye6200.tripsync.model.User;
import edu.neu.csye6200.tripsync.security.JwtService;
import edu.neu.csye6200.tripsync.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    @Autowired
    public UserController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    // Create a new user
    @PostMapping("/signup")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody SignupRequest request) {
        User user = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.from(user));
    }

    // Authenticate user (login) and issue a JWT
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginDTO credentials) {
        return userService.authenticateUser(credentials.getUsername(), credentials.getPassword())
                .<ResponseEntity<?>>map(user -> ResponseEntity.ok(
                        new LoginResponse(jwtService.generateToken(user), UserResponse.from(user))))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Invalid username or password")));
    }

    // Get the current user
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(UserResponse.from(userService.getUserById(userId)));
    }

    // Update the current user
    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateCurrentUser(@AuthenticationPrincipal Long userId,
                                                          @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(UserResponse.from(userService.updateUser(userId, request)));
    }

    // Delete the current user and their trips
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteCurrentUser(@AuthenticationPrincipal Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
