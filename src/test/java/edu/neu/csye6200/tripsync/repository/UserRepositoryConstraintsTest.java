package edu.neu.csye6200.tripsync.repository;

import edu.neu.csye6200.tripsync.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

// The database itself rejects duplicates, even if the service-level check is bypassed
@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryConstraintsTest {

    @Autowired
    private UserRepository userRepository;

    private static User user(String username, String email) {
        User user = new User();
        user.setFirstName("Test");
        user.setLastName("User");
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword("not-a-real-hash");
        return user;
    }

    @Test
    void usernameMustBeUnique() {
        userRepository.saveAndFlush(user("same-name", "first@example.com"));
        assertThatThrownBy(() -> userRepository.saveAndFlush(user("same-name", "second@example.com")))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void emailMustBeUnique() {
        userRepository.saveAndFlush(user("first-name", "same@example.com"));
        assertThatThrownBy(() -> userRepository.saveAndFlush(user("second-name", "same@example.com")))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
