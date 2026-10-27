package edu.neu.csye6200.tripsync.security;

import edu.neu.csye6200.tripsync.model.User;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@Service
public class JwtService {

    static final String SECRET_PROPERTY = "app.jwt.secret";
    static final String EXPIRATION_PROPERTY = "app.jwt.expiration-minutes";
    private static final int MIN_SECRET_BYTES = 32; // HS256 needs a key of at least 256 bits

    private final SecretKey signingKey;
    private final Duration expiration;

    public JwtService(Environment environment) {
        String secret = readSecret(environment);
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("JWT secret is not configured. Set the JWT_SECRET environment variable ("
                    + SECRET_PROPERTY + ") to a random value of at least " + MIN_SECRET_BYTES + " bytes.");
        }
        byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (secretBytes.length < MIN_SECRET_BYTES) {
            throw new IllegalStateException("JWT secret is too short. JWT_SECRET (" + SECRET_PROPERTY + ") must be at least "
                    + MIN_SECRET_BYTES + " bytes, but is " + secretBytes.length + " bytes.");
        }
        this.signingKey = Keys.hmacShaKeyFor(secretBytes);
        this.expiration = Duration.ofMinutes(environment.getProperty(EXPIRATION_PROPERTY, Long.class, 60L));
    }

    private static String readSecret(Environment environment) {
        try {
            return environment.getProperty(SECRET_PROPERTY);
        } catch (IllegalArgumentException e) {
            return null; // the ${JWT_SECRET} placeholder could not be resolved
        }
    }

    public String generateToken(User user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .claim("username", user.getUsername())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expiration)))
                .signWith(signingKey)
                .compact();
    }

    // Returns the user ID from a valid token, or empty if the token is malformed, tampered with or expired
    public Optional<Long> extractUserId(String token) {
        try {
            String subject = Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
            return Optional.of(Long.valueOf(subject));
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
