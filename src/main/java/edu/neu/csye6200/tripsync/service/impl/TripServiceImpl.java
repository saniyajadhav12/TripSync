package edu.neu.csye6200.tripsync.service.impl;

import edu.neu.csye6200.tripsync.model.Trip;
import edu.neu.csye6200.tripsync.repository.TripRepository;
import edu.neu.csye6200.tripsync.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TripServiceImpl implements TripService {

    private final TripRepository tripRepository;

    @Autowired
    public TripServiceImpl(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    @Override
    public Trip getTripById(Long id) {
        Optional<Trip> tripOptional = tripRepository.findById(id);
        if (tripOptional.isEmpty()) {
            throw new IllegalArgumentException("Trip not found with ID: " + id);
        }
        return tripOptional.get();
    }

    @Override
    public List<Trip> getAllTrips() {
        return tripRepository.findAll();
    }

    @Override
    public Trip createTrip(Trip trip) {
        return tripRepository.save(trip);
    }

    public List<Trip> getTripsByUserId(Long userId) {
        return tripRepository.findByUserId(userId);  // Custom query to fetch trips by user ID
    }

    @Override
    public Trip updateTrip(Long id, Trip trip) {
        Optional<Trip> existingTripOptional = tripRepository.findById(id);

        if (existingTripOptional.isEmpty()) {
            throw new IllegalArgumentException("Trip not found with ID: " + id);
        }

        Trip existingTrip = existingTripOptional.get();

        existingTrip.setDestination(trip.getDestination());
        existingTrip.setDescription(trip.getDescription());
        existingTrip.setStartDate(trip.getStartDate());
        existingTrip.setEndDate(trip.getEndDate());

        return tripRepository.save(existingTrip);
    }

    @Override
    public boolean deleteTrip(Long id) {
        try {
            Optional<Trip> tripOptional = tripRepository.findById(id);

            if (tripOptional.isEmpty()) {
                return false;  // Trip not found, return false
            }

            tripRepository.deleteById(id);  // Attempt deletion
            return true;  // Deletion successful
        } catch (Exception e) {
            // Handle any potential errors (e.g., database issues)
            return false;  // Return false if an exception occurs during deletion
        }
    }


}
