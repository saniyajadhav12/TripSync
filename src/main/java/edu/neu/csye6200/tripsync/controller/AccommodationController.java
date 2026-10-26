package edu.neu.csye6200.tripsync.controller;

import edu.neu.csye6200.tripsync.dto.AccommodationDTO;
import edu.neu.csye6200.tripsync.model.Accommodation;
import edu.neu.csye6200.tripsync.model.Trip;
import edu.neu.csye6200.tripsync.service.AccommodationService;
import edu.neu.csye6200.tripsync.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    AccommodationDTO convertToDTO(Accommodation accommodation) {
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


    // Get all accommodations
    @GetMapping
    public ResponseEntity<List<AccommodationDTO>> getAllAccommodations() {
        List<Accommodation> accommodations = accommodationService.getAllAccommodations();
        List<AccommodationDTO> accommodationDTOList = accommodations.stream().map(this::convertToDTO).toList();
        return ResponseEntity.ok(accommodationDTOList);
    }

    // Get accommodation by its ID
    @GetMapping("/{id}")
    public ResponseEntity<AccommodationDTO> getAccommodationById(@PathVariable Long id) {
        Accommodation accommodation = accommodationService.getAccommodationById(id);
        return accommodation==null
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(convertToDTO(accommodation));
    }

    // Get accommodations by trip ID
    @GetMapping("/trip/{tripId}")
    public ResponseEntity<List<AccommodationDTO>> getAccommodationsByTripId(@PathVariable Long tripId) {
        Trip trip = tripService.getTripById(tripId);
        if (trip == null) {
            return ResponseEntity.notFound().build(); // Trip not found
        }

        List<Accommodation> accommodations = accommodationService.getAccommodationsByTripId(tripId);
        List<AccommodationDTO> accommodationDTOList = accommodations.stream()
                .map(this::convertToDTO)
                .toList();

        return ResponseEntity.ok(accommodationDTOList);
    }

    // Create a new accommodation
    @PostMapping
    public ResponseEntity<AccommodationDTO> createAccommodation(@RequestBody AccommodationDTO accommodationDTO) {
        Trip trip = tripService.getTripById(accommodationDTO.getTripId());
        if (trip == null) {
            return ResponseEntity.status(404).body(null); // Trip not found
        }

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

    // Update an existing accommodation
    @PutMapping("/{id}")
    public ResponseEntity<AccommodationDTO> updateAccommodation(@PathVariable Long id, @RequestBody AccommodationDTO accommodationDTO) {
        Accommodation accommodation = accommodationService.getAccommodationById(id);
        if (accommodation == null) {
            return ResponseEntity.notFound().build(); // Accommodation not found
        }

        Trip trip = tripService.getTripById(accommodationDTO.getTripId());
        if (trip == null) {
            return ResponseEntity.status(404).body(null); // Trip not found
        }

        accommodation.setName(accommodationDTO.getName());
        accommodation.setAddress(accommodationDTO.getAddress());
        accommodation.setType(accommodationDTO.getType());
        accommodation.setCheckInDate(accommodationDTO.getCheckInDate());
        accommodation.setCheckOutDate(accommodationDTO.getCheckOutDate());
        accommodation.setReview(accommodationDTO.getReview());
        accommodation.setCost(accommodationDTO.getCost());
        accommodation.setTrip(trip);

        Accommodation updatedAccommodation = accommodationService.updateAccommodation(id, accommodation);
        return ResponseEntity.ok(convertToDTO(updatedAccommodation));
    }


    // Delete accommodation by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccommodation(@PathVariable Long id) {
        return accommodationService.deleteAccommodation(id)
                ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }
}
