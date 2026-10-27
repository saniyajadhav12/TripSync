package edu.neu.csye6200.tripsync.service.impl;

import edu.neu.csye6200.tripsync.exception.ResourceNotFoundException;
import edu.neu.csye6200.tripsync.model.Trip;
import edu.neu.csye6200.tripsync.repository.TripRepository;
import edu.neu.csye6200.tripsync.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TripServiceImpl implements TripService {

    private final TripRepository tripRepository;

    @Autowired
    public TripServiceImpl(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    @Override
    public Trip getTripForUser(Long id, Long userId) {
        return tripRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));
    }

    @Override
    public List<Trip> getTripsForUser(Long userId) {
        return tripRepository.findByUserId(userId);
    }

    @Override
    public Trip createTrip(Trip trip) {
        return tripRepository.save(trip);
    }

    @Override
    public Trip updateTrip(Long id, Long userId, Trip trip) {
        Trip existingTrip = getTripForUser(id, userId);

        existingTrip.setDestination(trip.getDestination());
        existingTrip.setDescription(trip.getDescription());
        existingTrip.setStartDate(trip.getStartDate());
        existingTrip.setEndDate(trip.getEndDate());

        return tripRepository.save(existingTrip);
    }

    @Override
    public void deleteTrip(Long id, Long userId) {
        tripRepository.delete(getTripForUser(id, userId));
    }
}
