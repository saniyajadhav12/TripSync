package edu.neu.csye6200.tripsync.service;

import edu.neu.csye6200.tripsync.model.Accommodation;
import java.util.List;

// All lookups are scoped to the user who owns the parent trip
public interface AccommodationService {
    Accommodation getAccommodationForUser(Long id, Long userId);
    List<Accommodation> getAccommodationsForTrip(Long tripId, Long userId);
    Accommodation createAccommodation(Accommodation accommodation);
    Accommodation updateAccommodation(Long id, Long userId, Accommodation accommodation);
    void deleteAccommodation(Long id, Long userId);
}
