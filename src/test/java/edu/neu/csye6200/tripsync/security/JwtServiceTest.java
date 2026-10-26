package edu.neu.csye6200.tripsync.security;

import edu.neu.csye6200.tripsync.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    // Test-only value, at least 32 bytes
    private static final String TEST_SECRET = "test-only-jwt-secret-do-not-use-in-production-0123456789";

    @Test
    void failsWhenSecretIsMissing() {
        assertThatThrownBy(() -> new JwtService(new MockEnvironment()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("JWT_SECRET");
    }

    @Test
    void failsWhenSecretPlaceholderCannotBeResolved() {
        MockEnvironment environment = new MockEnvironment().withProperty(JwtService.SECRET_PROPERTY, "${JWT_SECRET}");
        assertThatThrownBy(() -> new JwtService(environment))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("not configured");
    }

    @Test
    void failsWhenSecretIsShorterThan32Bytes() {
        MockEnvironment environment = new MockEnvironment().withProperty(JwtService.SECRET_PROPERTY, "too-short");
        assertThatThrownBy(() -> new JwtService(environment))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("at least 32 bytes");
    }

    @Test
    void createsAndValidatesToken() {
        JwtService jwtService = new JwtService(new MockEnvironment().withProperty(JwtService.SECRET_PROPERTY, TEST_SECRET));
        User user = new User(42L);
        user.setUsername("alice");

        String token = jwtService.generateToken(user);

        assertThat(jwtService.extractUserId(token)).contains(42L);
        assertThat(jwtService.extractUserId("not-a-token")).isEmpty();

        // A token signed with a different key is rejected
        JwtService otherService = new JwtService(new MockEnvironment()
                .withProperty(JwtService.SECRET_PROPERTY, "another-test-only-secret-that-is-long-enough-123"));
        assertThat(jwtService.extractUserId(otherService.generateToken(user))).isEmpty();
    }

    @Test
    void rejectsExpiredToken() {
        JwtService jwtService = new JwtService(new MockEnvironment()
                .withProperty(JwtService.SECRET_PROPERTY, TEST_SECRET)
                .withProperty(JwtService.EXPIRATION_PROPERTY, "-1"));
        User user = new User(42L);
        user.setUsername("alice");

        assertThat(jwtService.extractUserId(jwtService.generateToken(user))).isEmpty();
    }
}
