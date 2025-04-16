package edu.neu.csye6200.tripsync.service;

import edu.neu.csye6200.tripsync.model.User;
import java.util.List;

public interface UserService {
    void createUser(User user);
    User authenticateUser(String username, String password);
    User updateUser(Long id, User user);
    void deleteUser(Long id);
    User getUserById(Long id);
    List<User> getAllUsers();
    boolean isUsernameTaken(String username);
    boolean isEmailTaken(String email);
}
