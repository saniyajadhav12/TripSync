package edu.neu.csye6200.tripsync.service;

import edu.neu.csye6200.tripsync.model.Trip;

import java.util.List;

public interface TripService {
    Trip getTripById(Long id);
    List<Trip> getTripsByUserId(Long userId);
    List<Trip> getAllTrips();
    Trip createTrip(Trip trip);
    Trip updateTrip(Long id, Trip trip);
    boolean deleteTrip(Long id);
}
