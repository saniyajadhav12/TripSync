package edu.neu.csye6200.tripsync.controller;

import edu.neu.csye6200.tripsync.dto.LoginDTO;
import edu.neu.csye6200.tripsync.model.User;
import edu.neu.csye6200.tripsync.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Get all users
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // Get a user by ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Create a new user
    @PostMapping("/signup")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        if (userService.isUsernameTaken(user.getUsername())) {
            return ResponseEntity.status(400).body("Username is already taken!");
        }
        if (userService.isEmailTaken(user.getEmail())) {
            return ResponseEntity.status(400).body("Email is already taken!");
        }
        userService.createUser(user);
        return ResponseEntity.status(201).body(user);
    }

    // Update an existing user
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        try {
            User user = userService.updateUser(id, updatedUser);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete a user by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Authenticate user (login)
    @PostMapping("/login")
    public ResponseEntity<User> authenticateUser(@RequestBody LoginDTO credentials, HttpSession session) {
        try {
            User user = userService.authenticateUser(credentials.getUsername(), credentials.getPassword());
            session.setAttribute("user", user);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(null);  // Unauthorized
        }
    }
}
