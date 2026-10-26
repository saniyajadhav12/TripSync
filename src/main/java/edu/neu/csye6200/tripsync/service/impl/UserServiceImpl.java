package edu.neu.csye6200.tripsync.service.impl;

import edu.neu.csye6200.tripsync.model.User;
import edu.neu.csye6200.tripsync.repository.UserRepository;
import edu.neu.csye6200.tripsync.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void createUser(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);
    }

    @Override
    public User authenticateUser(String username, String password) {
        User user = userRepository.findByUsername(username);
        if(user == null) {
            throw new RuntimeException("Invalid username or password");
        }

        // Check if the provided password matches the stored encrypted password
        if (passwordEncoder.matches(password, user.getPassword())) {
            return user;
        } else {
            throw new RuntimeException("Invalid username or password");
        }
    }

    // Update user details (Example)
    public User updateUser(Long userId, User updatedUser) {
        User existingUser = getUserById(userId);
        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setUsername(updatedUser.getUsername());
        String encodedPassword = passwordEncoder.encode(updatedUser.getPassword());
        existingUser.setPassword(encodedPassword);
        return userRepository.save(existingUser);
    }

    // Delete user by ID
    public void deleteUser(Long userId) {
        User user = getUserById(userId);
        userRepository.delete(user);
    }

    // Get user by ID
    public User getUserById(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new IllegalArgumentException("User with ID " + userId + " not found");
        }
    }

    // Get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public boolean isUsernameTaken(String username) {
        User existingUser = userRepository.findByUsername(username);
        if (existingUser != null) {
            System.out.println("Username already exists: " + username);
            return true;
        }
        return false;
    }

    @Override
    public boolean isEmailTaken(String email) {
        User existingUser = userRepository.findByEmail(email);
        if (existingUser != null) {
            System.out.println("Email already exists: " + email);
            return true;
        }
        return false;
    }

}
