package edu.neu.csye6200.tripsync;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ValidationAndErrorHandlingTest extends ApiTestSupport {

    private static Map<String, Object> with(Map<String, Object> base, String key, Object value) {
        Map<String, Object> copy = new HashMap<>(base);
        if (value == null) {
            copy.remove(key);
        } else {
            copy.put(key, value);
        }
        return copy;
    }

    // --- Trips ---

    @Test
    void tripValidationErrorsReturn400WithMessageAndErrors() throws Exception {
        TestUser alice = registerAndLogin("alice");

        mockMvc.perform(post("/api/trips").header(HttpHeaders.AUTHORIZATION, bearer(alice))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("description", "no destination", "startDate", "06/01/2025", "endDate", "2025-02-30"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("destination: must not be blank")))
                .andExpect(jsonPath("$.errors.destination").value("must not be blank"))
                .andExpect(jsonPath("$.errors.startDate").value("must be a date in yyyy-MM-dd format"))
                .andExpect(jsonPath("$.errors.endDate").value("must be a date in yyyy-MM-dd format"));
    }

    @Test
    void tripEndDateBeforeStartDateReturns400() throws Exception {
        TestUser alice = registerAndLogin("alice");
        Map<String, Object> trip = Map.of("destination", "Rome", "startDate", "2025-06-10", "endDate", "2025-06-01");

        mockMvc.perform(post("/api/trips").header(HttpHeaders.AUTHORIZATION, bearer(alice))
                        .contentType(MediaType.APPLICATION_JSON).content(json(trip)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("endDate: must not be before startDate"))
                .andExpect(jsonPath("$.errors.endDate").value("must not be before startDate"));

        // The same rule applies on update
        long tripId = createTrip(alice, "Rome");
        mockMvc.perform(put("/api/trips/{id}", tripId).header(HttpHeaders.AUTHORIZATION, bearer(alice))
                        .contentType(MediaType.APPLICATION_JSON).content(json(trip)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.endDate").exists());
    }

    // --- Activities ---

    @Test
    void activityValidationErrorsReturn400() throws Exception {
        TestUser alice = registerAndLogin("alice");

        Map<String, Object> invalid = Map.of("description", "missing fields", "cost", -5);
        mockMvc.perform(post("/api/activities").header(HttpHeaders.AUTHORIZATION, bearer(alice))
                        .contentType(MediaType.APPLICATION_JSON).content(json(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.errors.title").value("must not be blank"))
                .andExpect(jsonPath("$.errors.category").value("must not be null"))
                .andExpect(jsonPath("$.errors.startDateTime").value("must not be blank"))
                .andExpect(jsonPath("$.errors.cost").value("must be greater than or equal to 0"))
                .andExpect(jsonPath("$.errors.tripId").value("must not be null"));

        // Cost is required
        long tripId = createTrip(alice, "Kyoto");
        mockMvc.perform(post("/api/activities").header(HttpHeaders.AUTHORIZATION, bearer(alice))
                        .contentType(MediaType.APPLICATION_JSON).content(json(with(activityBody(tripId), "cost", null))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.cost").value("must not be null"));
    }

    @Test
    void activityUpdateDoesNotRequireTripId() throws Exception {
        TestUser alice = registerAndLogin("alice");
        long tripId = createTrip(alice, "Kyoto");
        long activityId = postAndReturnId(alice, "/api/activities", activityBody(tripId));

        mockMvc.perform(put("/api/activities/{id}", activityId).header(HttpHeaders.AUTHORIZATION, bearer(alice))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(with(with(activityBody(tripId), "tripId", null), "title", "Updated"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated"))
                .andExpect(jsonPath("$.tripId").value(tripId));
    }

    // --- Accommodations ---

    @Test
    void accommodationValidationErrorsReturn400() throws Exception {
        TestUser alice = registerAndLogin("alice");
        long tripId = createTrip(alice, "Kyoto");

        Map<String, Object> invalid = Map.of("address", "Kyoto", "checkInDate", "2025-06-10",
                "checkOutDate", "2025-06-01", "cost", -1, "tripId", tripId);
        mockMvc.perform(post("/api/accommodations").header(HttpHeaders.AUTHORIZATION, bearer(alice))
                        .contentType(MediaType.APPLICATION_JSON).content(json(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").value("must not be blank"))
                .andExpect(jsonPath("$.errors.type").value("must not be null"))
                .andExpect(jsonPath("$.errors.cost").value("must be greater than or equal to 0"))
                .andExpect(jsonPath("$.errors.checkOutDate").value("must not be before checkInDate"));

        // tripId is required on create; cost is optional
        mockMvc.perform(post("/api/accommodations").header(HttpHeaders.AUTHORIZATION, bearer(alice))
                        .contentType(MediaType.APPLICATION_JSON).content(json(with(accommodationBody(tripId), "tripId", null))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.tripId").value("must not be null"));
        mockMvc.perform(post("/api/accommodations").header(HttpHeaders.AUTHORIZATION, bearer(alice))
                        .contentType(MediaType.APPLICATION_JSON).content(json(with(accommodationBody(tripId), "cost", null))))
                .andExpect(status().isCreated());
    }

    // --- Ownership of activity / accommodation update and delete ---

    @Test
    void activityUpdateAndDeleteOnlyByOwner() throws Exception {
        TestUser alice = registerAndLogin("alice");
        TestUser bob = registerAndLogin("bob");
        long tripId = createTrip(alice, "Kyoto");
        long activityId = postAndReturnId(alice, "/api/activities", activityBody(tripId));
        String update = json(with(activityBody(tripId), "title", "Changed"));

        mockMvc.perform(put("/api/activities/{id}", activityId).header(HttpHeaders.AUTHORIZATION, bearer(bob))
                        .contentType(MediaType.APPLICATION_JSON).content(update))
                .andExpect(status().isNotFound());
        mockMvc.perform(delete("/api/activities/{id}", activityId).header(HttpHeaders.AUTHORIZATION, bearer(bob)))
                .andExpect(status().isNotFound());

        mockMvc.perform(put("/api/activities/{id}", activityId).header(HttpHeaders.AUTHORIZATION, bearer(alice))
                        .contentType(MediaType.APPLICATION_JSON).content(update))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Changed"));
        mockMvc.perform(delete("/api/activities/{id}", activityId).header(HttpHeaders.AUTHORIZATION, bearer(alice)))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/activities/{id}", activityId).header(HttpHeaders.AUTHORIZATION, bearer(alice)))
                .andExpect(status().isNotFound());
    }

    @Test
    void accommodationUpdateAndDeleteOnlyByOwner() throws Exception {
        TestUser alice = registerAndLogin("alice");
        TestUser bob = registerAndLogin("bob");
        long tripId = createTrip(alice, "Kyoto");
        long accommodationId = postAndReturnId(alice, "/api/accommodations", accommodationBody(tripId));
        String update = json(with(accommodationBody(tripId), "name", "Changed"));

        mockMvc.perform(put("/api/accommodations/{id}", accommodationId).header(HttpHeaders.AUTHORIZATION, bearer(bob))
                        .contentType(MediaType.APPLICATION_JSON).content(update))
                .andExpect(status().isNotFound());
        mockMvc.perform(delete("/api/accommodations/{id}", accommodationId).header(HttpHeaders.AUTHORIZATION, bearer(bob)))
                .andExpect(status().isNotFound());

        mockMvc.perform(put("/api/accommodations/{id}", accommodationId).header(HttpHeaders.AUTHORIZATION, bearer(alice))
                        .contentType(MediaType.APPLICATION_JSON).content(update))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Changed"));
        mockMvc.perform(delete("/api/accommodations/{id}", accommodationId).header(HttpHeaders.AUTHORIZATION, bearer(alice)))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/accommodations/{id}", accommodationId).header(HttpHeaders.AUTHORIZATION, bearer(alice)))
                .andExpect(status().isNotFound());
    }

    // --- Signup and malformed requests ---

    @Test
    void signupValidationErrorHasReadableMessageAndNoStackTrace() throws Exception {
        Map<String, Object> body = with(signupBody(uniqueUsername("bademail")), "email", "not-an-email");

        MvcResult result = mockMvc.perform(post("/api/users/signup").contentType(MediaType.APPLICATION_JSON).content(json(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("email: must be a well-formed email address"))
                .andExpect(jsonPath("$.errors.email").value("must be a well-formed email address"))
                .andExpect(jsonPath("$.trace").doesNotExist())
                .andExpect(jsonPath("$.exception").doesNotExist())
                .andReturn();
        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        org.assertj.core.api.Assertions.assertThat(json.fieldNames()).toIterable().containsExactlyInAnyOrder("message", "errors");
    }

    @Test
    void duplicateEmailReturns409() throws Exception {
        String first = uniqueUsername("email");
        mockMvc.perform(post("/api/users/signup").contentType(MediaType.APPLICATION_JSON).content(json(signupBody(first))))
                .andExpect(status().isCreated());

        Map<String, Object> sameEmail = with(signupBody(uniqueUsername("other")), "email", first + "@example.com");
        mockMvc.perform(post("/api/users/signup").contentType(MediaType.APPLICATION_JSON).content(json(sameEmail)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email is already taken"));
    }

    @Test
    void malformedJsonAndUnknownEnumReturn400Json() throws Exception {
        TestUser alice = registerAndLogin("alice");
        long tripId = createTrip(alice, "Kyoto");

        mockMvc.perform(post("/api/trips").header(HttpHeaders.AUTHORIZATION, bearer(alice))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"destination\": "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid request body"));

        mockMvc.perform(post("/api/activities").header(HttpHeaders.AUTHORIZATION, bearer(alice))
                        .contentType(MediaType.APPLICATION_JSON).content(json(with(activityBody(tripId), "category", "Skydiving"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid request body"));

        mockMvc.perform(post("/api/accommodations").header(HttpHeaders.AUTHORIZATION, bearer(alice))
                        .contentType(MediaType.APPLICATION_JSON).content(json(with(accommodationBody(tripId), "type", "Castle"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid request body"));
    }

    @Test
    void springMvcErrorsKeepTheirStatusWithJsonBody() throws Exception {
        TestUser alice = registerAndLogin("alice");

        mockMvc.perform(get("/api/trips/not-a-number").header(HttpHeaders.AUTHORIZATION, bearer(alice)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Bad Request"));
        mockMvc.perform(post("/api/users/me").header(HttpHeaders.AUTHORIZATION, bearer(alice)))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.message").value("Method Not Allowed"));
    }

    // --- What the current frontend forms send must still be accepted ---

    @Test
    void currentFrontendPayloadsAreAccepted() throws Exception {
        TestUser alice = registerAndLogin("alice");

        // CreateTrip.tsx: form fields plus id: 0 and userId
        long tripId = postAndReturnId(alice, "/api/trips", Map.of(
                "description", "Summer", "destination", "Porto", "startDate", "2026-07-01", "endDate", "2026-07-01",
                "id", 0, "userId", alice.id()));

        // ActivityAccommodation.tsx: activity form (date inputs, cost defaults to 0)
        postAndReturnId(alice, "/api/activities", Map.of(
                "title", "Port tasting", "cost", 0, "category", "Dining", "description", "Cellars",
                "startDateTime", "2026-07-01", "endDateTime", "2026-07-01", "tripId", tripId));

        // ActivityAccommodation.tsx: accommodation form with an empty optional review
        postAndReturnId(alice, "/api/accommodations", Map.of(
                "name", "Riverside Hostel", "cost", 0, "address", "Porto", "type", "Hostel",
                "checkInDate", "2026-07-01", "checkOutDate", "2026-07-01", "review", "", "tripId", tripId));

        // EditTrip.tsx: sends back the whole trip it fetched, with userId
        MvcResult fetched = mockMvc.perform(get("/api/trips/{id}", tripId).header(HttpHeaders.AUTHORIZATION, bearer(alice)))
                .andExpect(status().isOk())
                .andReturn();
        @SuppressWarnings("unchecked")
        Map<String, Object> editPayload = objectMapper.readValue(fetched.getResponse().getContentAsString(), Map.class);
        editPayload.put("description", "Updated summer");
        mockMvc.perform(put("/api/trips/{id}", tripId).header(HttpHeaders.AUTHORIZATION, bearer(alice))
                        .contentType(MediaType.APPLICATION_JSON).content(json(editPayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Updated summer"));
    }
}
