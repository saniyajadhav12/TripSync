package edu.neu.csye6200.tripsync;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Shared helpers for the API integration tests. The H2 database is shared between tests,
// so every test creates fresh users with unique names.
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
abstract class ApiTestSupport {

    protected static final String PASSWORD = "correct-horse-battery";

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    protected record TestUser(long id, String username, String token) {
    }

    protected static String uniqueUsername(String prefix) {
        return prefix + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    protected String json(Object value) throws Exception {
        return objectMapper.writeValueAsString(value);
    }

    protected static String bearer(TestUser user) {
        return "Bearer " + user.token();
    }

    protected Map<String, Object> signupBody(String username) {
        return Map.of(
                "firstName", "Test",
                "lastName", "User",
                "email", username + "@example.com",
                "username", username,
                "password", PASSWORD);
    }

    protected TestUser registerAndLogin(String prefix) throws Exception {
        String username = uniqueUsername(prefix);
        mockMvc.perform(post("/api/users/signup").contentType(MediaType.APPLICATION_JSON).content(json(signupBody(username))))
                .andExpect(status().isCreated());

        MvcResult login = mockMvc.perform(post("/api/users/login").contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("username", username, "password", PASSWORD))))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode body = objectMapper.readTree(login.getResponse().getContentAsString());
        return new TestUser(body.get("user").get("id").asLong(), username, body.get("token").asText());
    }

    protected long createTrip(TestUser owner, String destination) throws Exception {
        return postAndReturnId(owner, "/api/trips", Map.of(
                "destination", destination,
                "description", "A trip",
                "startDate", "2025-06-01",
                "endDate", "2025-06-10"));
    }

    protected Map<String, Object> activityBody(long tripId) {
        return Map.of(
                "title", "Temple visit", "category", "Sightseeing", "description", "x",
                "startDateTime", "2025-06-02", "endDateTime", "2025-06-02", "cost", 10, "tripId", tripId);
    }

    protected Map<String, Object> accommodationBody(long tripId) {
        return Map.of(
                "name", "Ryokan", "address", "Kyoto", "type", "Hotel",
                "checkInDate", "2025-06-01", "checkOutDate", "2025-06-10", "cost", 500, "tripId", tripId);
    }

    protected long postAndReturnId(TestUser owner, String path, Map<String, Object> body) throws Exception {
        MvcResult result = mockMvc.perform(post(path).header(HttpHeaders.AUTHORIZATION, bearer(owner))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(body)))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }
}
