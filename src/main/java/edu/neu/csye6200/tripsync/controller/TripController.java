package edu.neu.csye6200.tripsync.controller;

import edu.neu.csye6200.tripsync.dto.TripDTO;
import edu.neu.csye6200.tripsync.exception.ResourceNotFoundException;
import edu.neu.csye6200.tripsync.model.Trip;
import edu.neu.csye6200.tripsync.service.TripService;
import edu.neu.csye6200.tripsync.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/trips")
public class TripController {

    private final TripService tripService;
    private final UserService userService;

    @Autowired
    public TripController(TripService tripService, UserService userService) {
        this.tripService = tripService;
        this.userService = userService;
    }

    // Helper method to convert Trip to TripDTO
    private TripDTO convertToDTO(Trip trip) {
        TripDTO tripDTO = new TripDTO();
        tripDTO.setId(trip.getId());
        tripDTO.setDestination(trip.getDestination());
        tripDTO.setDescription(trip.getDescription());
        tripDTO.setStartDate(trip.getStartDate());
        tripDTO.setEndDate(trip.getEndDate());
        tripDTO.setCreatedAt(trip.getCreatedAt().toString());
        tripDTO.setUpdatedAt(trip.getUpdatedAt().toString());
        tripDTO.setUserId(trip.getUser().getId());
        tripDTO.setActivities(trip.getActivities().stream()
                .map(ActivityController::convertToDTO)
                .toList());
        tripDTO.setAccommodations(trip.getAccommodations().stream()
                .map(AccommodationController::convertToDTO)
                .toList());
        return tripDTO;
    }

    // Create a new trip owned by the current user (any userId in the body is ignored)
    @PostMapping
    public ResponseEntity<TripDTO> createTrip(@AuthenticationPrincipal Long userId, @Valid @RequestBody TripDTO tripDTO) {
        Trip trip = new Trip();
        trip.setDestination(tripDTO.getDestination());
        trip.setDescription(tripDTO.getDescription());
        trip.setStartDate(tripDTO.getStartDate());
        trip.setEndDate(tripDTO.getEndDate());
        trip.setUser(userService.getUserById(userId));
        trip.setActivities(new ArrayList<>());
        trip.setAccommodations(new ArrayList<>());

        Trip createdTrip = tripService.createTrip(trip);
        return ResponseEntity.status(201).body(convertToDTO(createdTrip));
    }

    // Get the current user's trips
    @GetMapping
    public ResponseEntity<List<TripDTO>> getMyTrips(@AuthenticationPrincipal Long userId) {
        List<TripDTO> tripDTOList = tripService.getTripsForUser(userId).stream()
                .map(this::convertToDTO)
                .toList();
        return ResponseEntity.ok(tripDTOList);
    }

    // Get a trip by its ID
    @GetMapping("/{id}")
    public ResponseEntity<TripDTO> getTripById(@AuthenticationPrincipal Long userId, @PathVariable Long id) {
        return ResponseEntity.ok(convertToDTO(tripService.getTripForUser(id, userId)));
    }

    // Update an existing trip (the owner cannot be changed)
    @PutMapping("/{id}")
    public ResponseEntity<TripDTO> updateTrip(@AuthenticationPrincipal Long userId, @PathVariable Long id,
                                              @Valid @RequestBody TripDTO tripDTO) {
        Trip trip = new Trip();
        trip.setDestination(tripDTO.getDestination());
        trip.setDescription(tripDTO.getDescription());
        trip.setStartDate(tripDTO.getStartDate());
        trip.setEndDate(tripDTO.getEndDate());

        Trip updatedTrip = tripService.updateTrip(id, userId, trip);
        return ResponseEntity.ok(convertToDTO(updatedTrip));
    }

    // Delete a trip by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrip(@AuthenticationPrincipal Long userId, @PathVariable Long id) {
        tripService.deleteTrip(id, userId);
        return ResponseEntity.noContent().build();
    }

    // Get trips by user ID. Kept for the current frontend; only works for the current user's own ID.
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TripDTO>> getTripsByUserId(@AuthenticationPrincipal Long currentUserId,
                                                          @PathVariable Long userId) {
        if (!userId.equals(currentUserId)) {
            throw new ResourceNotFoundException("User not found");
        }
        return getMyTrips(currentUserId);
    }
}
