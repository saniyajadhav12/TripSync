package edu.neu.csye6200.tripsync;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
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

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityIntegrationTest {

    private static final String PASSWORD = "correct-horse-battery";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // The database is shared across tests in this class, so every test uses fresh usernames
    private record TestUser(long id, String username, String token) {
    }

    private static String uniqueUsername(String prefix) {
        return prefix + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private String json(Object value) throws Exception {
        return objectMapper.writeValueAsString(value);
    }

    private static String bearer(TestUser user) {
        return "Bearer " + user.token();
    }

    private Map<String, Object> signupBody(String username) {
        return Map.of(
                "firstName", "Test",
                "lastName", "User",
                "email", username + "@example.com",
                "username", username,
                "password", PASSWORD);
    }

    private TestUser registerAndLogin(String prefix) throws Exception {
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

    private long createTrip(TestUser owner, String destination) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/trips").header(HttpHeaders.AUTHORIZATION, bearer(owner))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "destination", destination,
                                "description", "A trip",
                                "startDate", "2025-06-01",
                                "endDate", "2025-06-10"))))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }

    // a. signup returns 201 and never exposes the password
    @Test
    void signupReturns201WithoutPassword() throws Exception {
        String username = uniqueUsername("signup");
        mockMvc.perform(post("/api/users/signup").contentType(MediaType.APPLICATION_JSON).content(json(signupBody(username))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(content().string(not(containsString("password"))));
    }

    @Test
    void signupWithInvalidDataReturns400() throws Exception {
        Map<String, Object> body = Map.of(
                "firstName", "Test", "lastName", "User", "email", "not-an-email",
                "username", uniqueUsername("invalid"), "password", "short");
        mockMvc.perform(post("/api/users/signup").contentType(MediaType.APPLICATION_JSON).content(json(body)))
                .andExpect(status().isBadRequest());
    }

    // b. duplicate username returns 409 with a JSON message
    @Test
    void duplicateUsernameReturns409() throws Exception {
        String username = uniqueUsername("dup");
        mockMvc.perform(post("/api/users/signup").contentType(MediaType.APPLICATION_JSON).content(json(signupBody(username))))
                .andExpect(status().isCreated());

        Map<String, Object> sameUsernameOtherEmail = Map.of(
                "firstName", "Other", "lastName", "User", "email", "other-" + username + "@example.com",
                "username", username, "password", PASSWORD);
        mockMvc.perform(post("/api/users/signup").contentType(MediaType.APPLICATION_JSON).content(json(sameUsernameOtherEmail)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Username is already taken"));
    }

    // c. login returns a token without the password; a wrong password returns 401
    @Test
    void loginReturnsTokenAndRejectsWrongPassword() throws Exception {
        String username = uniqueUsername("login");
        mockMvc.perform(post("/api/users/signup").contentType(MediaType.APPLICATION_JSON).content(json(signupBody(username))))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/users/login").contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("username", username, "password", PASSWORD))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.user.username").value(username))
                .andExpect(jsonPath("$.user.password").doesNotExist())
                .andExpect(content().string(not(containsString("password"))));

        mockMvc.perform(post("/api/users/login").contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("username", username, "password", "wrong-password"))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid username or password"));
    }

    // d. protected endpoints return 401 JSON without a valid token
    @Test
    void tripsWithoutTokenReturns401Json() throws Exception {
        mockMvc.perform(get("/api/trips"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Authentication required"));

        mockMvc.perform(get("/api/trips").header(HttpHeaders.AUTHORIZATION, "Bearer not-a-valid-token"))
                .andExpect(status().isUnauthorized());
    }

    // e. user B cannot read, update or delete user A's trip
    @Test
    void otherUserGets404OnSomeoneElsesTrip() throws Exception {
        TestUser alice = registerAndLogin("alice");
        TestUser bob = registerAndLogin("bob");
        long tripId = createTrip(alice, "Lisbon");

        mockMvc.perform(get("/api/trips/{id}", tripId).header(HttpHeaders.AUTHORIZATION, bearer(bob)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Trip not found"));
        mockMvc.perform(put("/api/trips/{id}", tripId).header(HttpHeaders.AUTHORIZATION, bearer(bob))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("destination", "Hacked", "description", "x",
                                "startDate", "2025-01-01", "endDate", "2025-01-02"))))
                .andExpect(status().isNotFound());
        mockMvc.perform(delete("/api/trips/{id}", tripId).header(HttpHeaders.AUTHORIZATION, bearer(bob)))
                .andExpect(status().isNotFound());

        // Alice's trip is untouched
        mockMvc.perform(get("/api/trips/{id}", tripId).header(HttpHeaders.AUTHORIZATION, bearer(alice)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.destination").value("Lisbon"));
    }

    // f. user B cannot add an activity or accommodation to user A's trip
    @Test
    void otherUserCannotAddActivityOrAccommodationToSomeoneElsesTrip() throws Exception {
        TestUser alice = registerAndLogin("alice");
        TestUser bob = registerAndLogin("bob");
        long tripId = createTrip(alice, "Kyoto");

        Map<String, Object> activity = Map.of(
                "title", "Temple visit", "category", "Sightseeing", "description", "x",
                "startDateTime", "2025-06-02", "endDateTime", "2025-06-02", "cost", 10, "tripId", tripId);
        Map<String, Object> accommodation = Map.of(
                "name", "Ryokan", "address", "Kyoto", "type", "Hotel",
                "checkInDate", "2025-06-01", "checkOutDate", "2025-06-10", "cost", 500, "tripId", tripId);

        mockMvc.perform(post("/api/activities").header(HttpHeaders.AUTHORIZATION, bearer(bob))
                        .contentType(MediaType.APPLICATION_JSON).content(json(activity)))
                .andExpect(status().isNotFound());
        mockMvc.perform(post("/api/accommodations").header(HttpHeaders.AUTHORIZATION, bearer(bob))
                        .contentType(MediaType.APPLICATION_JSON).content(json(accommodation)))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/activities/trip/{tripId}", tripId).header(HttpHeaders.AUTHORIZATION, bearer(bob)))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/accommodations/trip/{tripId}", tripId).header(HttpHeaders.AUTHORIZATION, bearer(bob)))
                .andExpect(status().isNotFound());

        // The owner can add both
        mockMvc.perform(post("/api/activities").header(HttpHeaders.AUTHORIZATION, bearer(alice))
                        .contentType(MediaType.APPLICATION_JSON).content(json(activity)))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/accommodations").header(HttpHeaders.AUTHORIZATION, bearer(alice))
                        .contentType(MediaType.APPLICATION_JSON).content(json(accommodation)))
                .andExpect(status().isCreated());
    }

    // g. GET /api/trips returns only the current user's trips
    @Test
    void listTripsReturnsOnlyOwnTrips() throws Exception {
        TestUser alice = registerAndLogin("alice");
        TestUser bob = registerAndLogin("bob");
        createTrip(alice, "Paris");
        createTrip(alice, "Rome");
        createTrip(bob, "Berlin");

        mockMvc.perform(get("/api/trips").header(HttpHeaders.AUTHORIZATION, bearer(alice)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].userId", everyItem(is((int) alice.id()))));

        // The legacy by-user route only works for the caller's own ID
        mockMvc.perform(get("/api/trips/user/{userId}", alice.id()).header(HttpHeaders.AUTHORIZATION, bearer(alice)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
        mockMvc.perform(get("/api/trips/user/{userId}", alice.id()).header(HttpHeaders.AUTHORIZATION, bearer(bob)))
                .andExpect(status().isNotFound());
    }

    @Test
    void createTripIgnoresUserIdInBody() throws Exception {
        TestUser alice = registerAndLogin("alice");
        TestUser bob = registerAndLogin("bob");

        mockMvc.perform(post("/api/trips").header(HttpHeaders.AUTHORIZATION, bearer(alice))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("destination", "Oslo", "description", "x",
                                "startDate", "2025-01-01", "endDate", "2025-01-05", "userId", bob.id()))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(alice.id()));

        mockMvc.perform(get("/api/trips").header(HttpHeaders.AUTHORIZATION, bearer(bob)))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // h. the user list endpoint is gone
    @Test
    void userListIsNotExposed() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());

        TestUser alice = registerAndLogin("alice");
        mockMvc.perform(get("/api/users").header(HttpHeaders.AUTHORIZATION, bearer(alice)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(not(startsWith("["))));
    }

    @Test
    void meEndpointsUseTheTokenUser() throws Exception {
        TestUser alice = registerAndLogin("alice");

        mockMvc.perform(get("/api/users/me").header(HttpHeaders.AUTHORIZATION, bearer(alice)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(alice.id()))
                .andExpect(jsonPath("$.password").doesNotExist());

        // A blank password leaves the current password unchanged
        mockMvc.perform(put("/api/users/me").header(HttpHeaders.AUTHORIZATION, bearer(alice))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("firstName", "Alicia", "lastName", "User",
                                "email", alice.username() + "@example.com", "username", alice.username(), "password", ""))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Alicia"))
                .andExpect(jsonPath("$.password").doesNotExist());
        mockMvc.perform(post("/api/users/login").contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("username", alice.username(), "password", PASSWORD))))
                .andExpect(status().isOk());

        createTrip(alice, "Madrid");
        mockMvc.perform(delete("/api/users/me").header(HttpHeaders.AUTHORIZATION, bearer(alice)))
                .andExpect(status().isNoContent());
        mockMvc.perform(post("/api/users/login").contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("username", alice.username(), "password", PASSWORD))))
                .andExpect(status().isUnauthorized());
    }
}
