package edu.neu.csye6200.tripsync.controller;

import edu.neu.csye6200.tripsync.dto.AccommodationDTO;
import edu.neu.csye6200.tripsync.model.Accommodation;
import edu.neu.csye6200.tripsync.model.Trip;
import edu.neu.csye6200.tripsync.service.AccommodationService;
import edu.neu.csye6200.tripsync.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accommodations")
public class AccommodationController {

    private final AccommodationService accommodationService;
    private final TripService tripService;

    @Autowired
    public AccommodationController(AccommodationService accommodationService, TripService tripService) {
        this.accommodationService = accommodationService;
        this.tripService = tripService;
    }

    //Helper method to convert Accommodation to AccommodationDTO
    static AccommodationDTO convertToDTO(Accommodation accommodation) {
        AccommodationDTO accommodationDTO = new AccommodationDTO();
        accommodationDTO.setId(accommodation.getId());
        accommodationDTO.setName(accommodation.getName());
        accommodationDTO.setAddress(accommodation.getAddress());
        accommodationDTO.setType(accommodation.getType());
        accommodationDTO.setCheckInDate(accommodation.getCheckInDate());
        accommodationDTO.setCheckOutDate(accommodation.getCheckOutDate());
        accommodationDTO.setReview(accommodation.getReview());
        accommodationDTO.setCost(accommodation.getCost());
        accommodationDTO.setCreatedAt(accommodation.getCreatedAt());
        accommodationDTO.setUpdatedAt(accommodation.getUpdatedAt());
        accommodationDTO.setTripId(accommodation.getTrip().getId());
        return accommodationDTO;
    }

    // Get accommodation by its ID
    @GetMapping("/{id}")
    public ResponseEntity<AccommodationDTO> getAccommodationById(@AuthenticationPrincipal Long userId, @PathVariable Long id) {
        return ResponseEntity.ok(convertToDTO(accommodationService.getAccommodationForUser(id, userId)));
    }

    // Get accommodations by trip ID
    @GetMapping("/trip/{tripId}")
    public ResponseEntity<List<AccommodationDTO>> getAccommodationsByTripId(@AuthenticationPrincipal Long userId,
                                                                            @PathVariable Long tripId) {
        List<AccommodationDTO> accommodationDTOList = accommodationService.getAccommodationsForTrip(tripId, userId).stream()
                .map(AccommodationController::convertToDTO)
                .toList();
        return ResponseEntity.ok(accommodationDTOList);
    }

    // Create a new accommodation on one of the current user's trips
    @PostMapping
    public ResponseEntity<AccommodationDTO> createAccommodation(@AuthenticationPrincipal Long userId,
                                                                @RequestBody AccommodationDTO accommodationDTO) {
        Trip trip = tripService.getTripForUser(accommodationDTO.getTripId(), userId);

        Accommodation accommodation = new Accommodation();
        accommodation.setName(accommodationDTO.getName());
        accommodation.setAddress(accommodationDTO.getAddress());
        accommodation.setType(accommodationDTO.getType());
        accommodation.setCheckInDate(accommodationDTO.getCheckInDate());
        accommodation.setCheckOutDate(accommodationDTO.getCheckOutDate());
        accommodation.setReview(accommodationDTO.getReview());
        accommodation.setCost(accommodationDTO.getCost());
        accommodation.setTrip(trip);

        Accommodation createdAccommodation = accommodationService.createAccommodation(accommodation);
        return ResponseEntity.status(201).body(convertToDTO(createdAccommodation));
    }

    // Update an existing accommodation (it stays on its current trip)
    @PutMapping("/{id}")
    public ResponseEntity<AccommodationDTO> updateAccommodation(@AuthenticationPrincipal Long userId, @PathVariable Long id,
                                                                @RequestBody AccommodationDTO accommodationDTO) {
        Accommodation accommodation = new Accommodation();
        accommodation.setName(accommodationDTO.getName());
        accommodation.setAddress(accommodationDTO.getAddress());
        accommodation.setType(accommodationDTO.getType());
        accommodation.setCheckInDate(accommodationDTO.getCheckInDate());
        accommodation.setCheckOutDate(accommodationDTO.getCheckOutDate());
        accommodation.setReview(accommodationDTO.getReview());
        accommodation.setCost(accommodationDTO.getCost());

        Accommodation updatedAccommodation = accommodationService.updateAccommodation(id, userId, accommodation);
        return ResponseEntity.ok(convertToDTO(updatedAccommodation));
    }

    // Delete accommodation by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccommodation(@AuthenticationPrincipal Long userId, @PathVariable Long id) {
        accommodationService.deleteAccommodation(id, userId);
        return ResponseEntity.ok().build();
    }
}
