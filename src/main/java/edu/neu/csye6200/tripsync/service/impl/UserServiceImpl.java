package edu.neu.csye6200.tripsync.service.impl;

import edu.neu.csye6200.tripsync.dto.SignupRequest;
import edu.neu.csye6200.tripsync.dto.UpdateUserRequest;
import edu.neu.csye6200.tripsync.exception.DuplicateResourceException;
import edu.neu.csye6200.tripsync.exception.ResourceNotFoundException;
import edu.neu.csye6200.tripsync.model.User;
import edu.neu.csye6200.tripsync.repository.TripRepository;
import edu.neu.csye6200.tripsync.repository.UserRepository;
import edu.neu.csye6200.tripsync.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final TripRepository tripRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, TripRepository tripRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tripRepository = tripRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public User createUser(SignupRequest request) {
        if (userRepository.findByUsername(request.username()) != null) {
            throw new DuplicateResourceException("Username is already taken");
        }
        if (userRepository.findByEmail(request.email()) != null) {
            throw new DuplicateResourceException("Email is already taken");
        }

        User user = new User();
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        return userRepository.save(user);
    }

    @Override
    public Optional<User> authenticateUser(String username, String password) {
        if (username == null || password == null) {
            return Optional.empty();
        }
        User user = userRepository.findByUsername(username);
        // Check if the provided password matches the stored encrypted password
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            return Optional.empty();
        }
        return Optional.of(user);
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    @Transactional
    public User updateUser(Long userId, UpdateUserRequest request) {
        User existingUser = getUserById(userId);

        User sameUsername = userRepository.findByUsername(request.username());
        if (sameUsername != null && !sameUsername.getId().equals(userId)) {
            throw new DuplicateResourceException("Username is already taken");
        }
        User sameEmail = userRepository.findByEmail(request.email());
        if (sameEmail != null && !sameEmail.getId().equals(userId)) {
            throw new DuplicateResourceException("Email is already taken");
        }

        existingUser.setFirstName(request.firstName());
        existingUser.setLastName(request.lastName());
        existingUser.setEmail(request.email());
        existingUser.setUsername(request.username());
        // Only change the password when a new one is provided
        if (request.password() != null && !request.password().isBlank()) {
            existingUser.setPassword(passwordEncoder.encode(request.password()));
        }
        return userRepository.save(existingUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User user = getUserById(userId);
        // Trips reference the user, so remove them first (activities and accommodations cascade)
        tripRepository.deleteAll(tripRepository.findByUserId(userId));
        userRepository.delete(user);
    }
}
