package edu.neu.csye6200.tripsync.controller;

import edu.neu.csye6200.tripsync.dto.TripDTO;
import edu.neu.csye6200.tripsync.model.Accommodation;
import edu.neu.csye6200.tripsync.model.Activity;
import edu.neu.csye6200.tripsync.model.Trip;
import edu.neu.csye6200.tripsync.model.User;
import edu.neu.csye6200.tripsync.service.AccommodationService;
import edu.neu.csye6200.tripsync.service.ActivityService;
import edu.neu.csye6200.tripsync.service.TripService;
import edu.neu.csye6200.tripsync.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trips")
public class TripController {

    private final TripService tripService;
    private final UserService userService;
    private final ActivityService activityService;
    private final AccommodationService accommodationService;

    @Autowired
    public TripController(TripService tripService, UserService userService, ActivityService activityService, AccommodationService accommodationService) {
        this.tripService = tripService;
        this.userService = userService;
        this.activityService = activityService;
        this.accommodationService = accommodationService;
    }

    // Helper method to convert Trip to TripDTO
    private TripDTO convertToDTO(Trip trip) {
        TripDTO tripDTO = new TripDTO();
        ActivityController activityController = new ActivityController(activityService,tripService);
        AccommodationController accommodationController = new AccommodationController(accommodationService,tripService);
        tripDTO.setId(trip.getId());
        tripDTO.setDestination(trip.getDestination());
        tripDTO.setDescription(trip.getDescription());
        tripDTO.setStartDate(trip.getStartDate());
        tripDTO.setEndDate(trip.getEndDate());
        tripDTO.setCreatedAt(trip.getCreatedAt().toString());
        tripDTO.setUpdatedAt(trip.getUpdatedAt().toString());
        tripDTO.setUserId(trip.getUser().getId());
        tripDTO.setActivities(trip.getActivities().stream()
                .map(activityController::convertToDTO)  // Assuming you have an instance of ActivityController
                .toList());
        tripDTO.setAccommodations(trip.getAccommodations().stream()
                .map(accommodationController::convertToDTO)  // Assuming you have an instance of AccommodationController
                .toList());
        return tripDTO;
    }

    // Create a new trip with userId
    @PostMapping
    public ResponseEntity<TripDTO> createTrip(@RequestBody TripDTO tripDTO) {
        User user = userService.getUserById(tripDTO.getUserId());
        List<Activity> activities = activityService.getActivitiesByTripId(tripDTO.getId());
        List<Accommodation> accommodations = accommodationService.getAccommodationsByTripId(tripDTO.getId());
        if (user == null) {
            return ResponseEntity.status(404).body(null);  // User not found
        }

        Trip trip = new Trip();
        trip.setDestination(tripDTO.getDestination());
        trip.setDescription(tripDTO.getDescription());
        trip.setStartDate(tripDTO.getStartDate());
        trip.setEndDate(tripDTO.getEndDate());
        trip.setUser(user);
        trip.setActivities(activities);
        trip.setAccommodations(accommodations);

        Trip createdTrip = tripService.createTrip(trip);
        return ResponseEntity.status(201).body(convertToDTO(createdTrip));
    }

    // Get all trips
    @GetMapping
    public ResponseEntity<List<TripDTO>> getAllTrips() {
        List<Trip> trips = tripService.getAllTrips();

        List<TripDTO> tripDTOList = trips.stream()
                .map(this::convertToDTO)
                .toList();

        return ResponseEntity.ok(tripDTOList);
    }

    // Get a trip by its ID
    @GetMapping("/{id}")
    public ResponseEntity<TripDTO> getTripById(@PathVariable Long id) {
        Trip trip = tripService.getTripById(id);
        return trip == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(convertToDTO(trip));
    }

    // Update an existing trip
    @PutMapping("/{id}")
    public ResponseEntity<TripDTO> updateTrip(@PathVariable Long id, @RequestBody TripDTO tripDTO) {
        Trip trip = tripService.getTripById(id);
        if (trip == null) {
            return ResponseEntity.notFound().build();
        }

        User user = userService.getUserById(tripDTO.getUserId());
        if (user == null) {
            return ResponseEntity.status(404).body(null);  // User not found
        }

        trip.setDestination(tripDTO.getDestination());
        trip.setDescription(tripDTO.getDescription());
        trip.setStartDate(tripDTO.getStartDate());
        trip.setEndDate(tripDTO.getEndDate());
        trip.setUser(user);

        Trip updatedTrip = tripService.updateTrip(id, trip);
        return ResponseEntity.ok(convertToDTO(updatedTrip));
    }

    // Delete a trip by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrip(@PathVariable Long id) {
        boolean isDeleted = tripService.deleteTrip(id);
        return isDeleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // Get trips by user ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TripDTO>> getTripsByUserId(@PathVariable Long userId) {
        List<Trip> trips = tripService.getTripsByUserId(userId);

        List<TripDTO> tripDTOList = trips.stream()
                .map(this::convertToDTO)
                .toList();

        return ResponseEntity.ok(tripDTOList);
    }
}
