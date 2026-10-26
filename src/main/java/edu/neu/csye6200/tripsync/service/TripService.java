package edu.neu.csye6200.tripsync.service;

import edu.neu.csye6200.tripsync.model.Trip;

import java.util.List;

// All lookups are scoped to the owning user; another user's trip is reported as not found
public interface TripService {
    Trip getTripForUser(Long id, Long userId);
    List<Trip> getTripsForUser(Long userId);
    Trip createTrip(Trip trip);
    Trip updateTrip(Long id, Long userId, Trip trip);
    void deleteTrip(Long id, Long userId);
}
