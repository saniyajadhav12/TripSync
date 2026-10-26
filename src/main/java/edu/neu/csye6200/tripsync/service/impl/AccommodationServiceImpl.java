package edu.neu.csye6200.tripsync.service.impl;

import edu.neu.csye6200.tripsync.exception.ResourceNotFoundException;
import edu.neu.csye6200.tripsync.model.Accommodation;
import edu.neu.csye6200.tripsync.repository.AccommodationRepository;
import edu.neu.csye6200.tripsync.service.AccommodationService;
import edu.neu.csye6200.tripsync.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccommodationServiceImpl implements AccommodationService {

    private final AccommodationRepository accommodationRepository;
    private final TripService tripService;

    @Autowired
    public AccommodationServiceImpl(AccommodationRepository accommodationRepository, TripService tripService) {
        this.accommodationRepository = accommodationRepository;
        this.tripService = tripService;
    }

    @Override
    public Accommodation getAccommodationForUser(Long id, Long userId) {
        return accommodationRepository.findByIdAndTripUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Accommodation not found"));
    }

    @Override
    public List<Accommodation> getAccommodationsForTrip(Long tripId, Long userId) {
        tripService.getTripForUser(tripId, userId); // 404 unless the trip belongs to the user
        return accommodationRepository.findByTripId(tripId);
    }

    @Override
    public Accommodation createAccommodation(Accommodation accommodation) {
        return accommodationRepository.save(accommodation);
    }

    @Override
    public Accommodation updateAccommodation(Long id, Long userId, Accommodation accommodation) {
        Accommodation existingAccommodation = getAccommodationForUser(id, userId);

        existingAccommodation.setName(accommodation.getName());
        existingAccommodation.setAddress(accommodation.getAddress());
        existingAccommodation.setType(accommodation.getType());
        existingAccommodation.setCheckInDate(accommodation.getCheckInDate());
        existingAccommodation.setCheckOutDate(accommodation.getCheckOutDate());
        existingAccommodation.setReview(accommodation.getReview());
        existingAccommodation.setCost(accommodation.getCost());

        return accommodationRepository.save(existingAccommodation);
    }

    @Override
    public void deleteAccommodation(Long id, Long userId) {
        accommodationRepository.delete(getAccommodationForUser(id, userId));
    }
}
