package edu.neu.csye6200.tripsync.service;

import edu.neu.csye6200.tripsync.model.Accommodation;
import java.util.List;

public interface AccommodationService {
    Accommodation getAccommodationById(Long id);
    List<Accommodation> getAccommodationsByTripId(Long id);
    List<Accommodation> getAllAccommodations();
    Accommodation createAccommodation(Accommodation accommodation);
    Accommodation updateAccommodation(Long id, Accommodation accommodation);
    boolean deleteAccommodation(Long id);
}
