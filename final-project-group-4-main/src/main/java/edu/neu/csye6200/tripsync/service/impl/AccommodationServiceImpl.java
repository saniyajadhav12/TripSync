package edu.neu.csye6200.tripsync.service.impl;

import edu.neu.csye6200.tripsync.model.Accommodation;
import edu.neu.csye6200.tripsync.repository.AccommodationRepository;
import edu.neu.csye6200.tripsync.service.AccommodationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccommodationServiceImpl implements AccommodationService {

    private final AccommodationRepository accommodationRepository;

    @Autowired
    public AccommodationServiceImpl(AccommodationRepository accommodationRepository) {
        this.accommodationRepository = accommodationRepository;
    }

    @Override
    public Accommodation getAccommodationById(Long id) {
        Optional<Accommodation> accommodationOptional = accommodationRepository.findById(id);

        if (accommodationOptional.isEmpty()) {
            throw new IllegalArgumentException("Accommodation not found with ID: " + id);
        }

        return accommodationOptional.get();
    }

    @Override
    public List<Accommodation> getAllAccommodations() {
        return accommodationRepository.findAll();
    }


    public List<Accommodation> getAccommodationsByTripId(Long tripId) {
        return accommodationRepository.findByTripId(tripId);
    }


    @Override
    public Accommodation createAccommodation(Accommodation accommodation) {
        return accommodationRepository.save(accommodation);
    }

    @Override
    public Accommodation updateAccommodation(Long id, Accommodation accommodation) {
        Optional<Accommodation> existingAccommodationOptional = accommodationRepository.findById(id);

        if (existingAccommodationOptional.isEmpty()) {
            throw new IllegalArgumentException("Accommodation not found with ID: " + id); // or custom exception
        }

        Accommodation existingAccommodation = existingAccommodationOptional.get();

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
    public boolean deleteAccommodation(Long id) {
        Optional<Accommodation> accommodationOptional = accommodationRepository.findById(id);

        if (accommodationOptional.isEmpty()) {
            return false;
        }

        accommodationRepository.deleteById(id);
        return true;
    }
}
