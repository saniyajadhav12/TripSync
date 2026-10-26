package edu.neu.csye6200.tripsync.service;

import edu.neu.csye6200.tripsync.dto.SignupRequest;
import edu.neu.csye6200.tripsync.dto.UpdateUserRequest;
import edu.neu.csye6200.tripsync.model.User;

import java.util.Optional;

public interface UserService {
    User createUser(SignupRequest request);
    Optional<User> authenticateUser(String username, String password);
    User getUserById(Long id);
    User updateUser(Long id, UpdateUserRequest request);
    void deleteUser(Long id);
}
