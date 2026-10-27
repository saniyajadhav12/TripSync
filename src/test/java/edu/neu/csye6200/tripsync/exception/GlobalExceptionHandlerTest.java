package edu.neu.csye6200.tripsync.exception;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void dataIntegrityViolationReturns409WithoutSql() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException(
                "could not execute statement [insert into users (email,username) values (?,?)]",
                new SQLIntegrityConstraintViolationException("Duplicate entry 'alice' for key 'users.UK_username'"));

        ResponseEntity<Map<String, String>> response = handler.handleDataIntegrityViolation(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).containsOnlyKeys("message");
        assertThat(response.getBody().get("message"))
                .isEqualTo("The request conflicts with existing data")
                .doesNotContainIgnoringCase("insert")
                .doesNotContainIgnoringCase("duplicate");
    }

    @Test
    void unexpectedExceptionReturns500WithoutDetails() {
        ResponseEntity<Map<String, String>> response =
                handler.handleUnexpected(new IllegalStateException("secret internal detail"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).containsExactly(Map.entry("message", "Unexpected error"));
    }
}
