package edu.neu.csye6200.tripsync.controller;

import edu.neu.csye6200.tripsync.dto.ActivityDTO;
import edu.neu.csye6200.tripsync.model.Activity;
import edu.neu.csye6200.tripsync.model.Trip;
import edu.neu.csye6200.tripsync.service.ActivityService;
import edu.neu.csye6200.tripsync.service.TripService;
import edu.neu.csye6200.tripsync.validation.OnCreate;
import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
public class ActivityController {

    private final ActivityService activityService;
    private final TripService tripService;

    @Autowired
    public ActivityController(ActivityService activityService, TripService tripService) {
        this.activityService = activityService;
        this.tripService = tripService;
    }

    //Helper method to convert Activity to ActivityDTO
    static ActivityDTO convertToDTO(Activity activity){
        ActivityDTO activityDTO = new ActivityDTO();
        activityDTO.setId(activity.getId());
        activityDTO.setTitle(activity.getTitle());
        activityDTO.setCategory(activity.getCategory());
        activityDTO.setDescription(activity.getDescription());
        activityDTO.setStartDateTime(activity.getStartDateTime());
        activityDTO.setEndDateTime(activity.getEndDateTime());
        activityDTO.setCost(activity.getCost());
        activityDTO.setCreatedAt(activity.getCreatedAt());
        activityDTO.setUpdatedAt(activity.getUpdatedAt());
        activityDTO.setTripId(activity.getTrip().getId());
        return activityDTO;
    }

    // Create a new activity on one of the current user's trips
    @PostMapping
    public ResponseEntity<ActivityDTO> createActivity(@AuthenticationPrincipal Long userId,
                                                      @Validated({Default.class, OnCreate.class}) @RequestBody ActivityDTO activityDTO) {
        Trip trip = tripService.getTripForUser(activityDTO.getTripId(), userId);

        Activity activity = new Activity();
        activity.setTitle(activityDTO.getTitle());
        activity.setCategory(activityDTO.getCategory());
        activity.setDescription(activityDTO.getDescription());
        activity.setStartDateTime(activityDTO.getStartDateTime());
        activity.setEndDateTime(activityDTO.getEndDateTime());
        activity.setCost(activityDTO.getCost());
        activity.setTrip(trip);

        Activity createdActivity = activityService.createActivity(activity);
        return ResponseEntity.status(201).body(convertToDTO(createdActivity));
    }

    // Get an activity by its ID
    @GetMapping("/{id}")
    public ResponseEntity<ActivityDTO> getActivityById(@AuthenticationPrincipal Long userId, @PathVariable Long id) {
        return ResponseEntity.ok(convertToDTO(activityService.getActivityForUser(id, userId)));
    }

    // Get all activities by trip ID
    @GetMapping("/trip/{tripId}")
    public ResponseEntity<List<ActivityDTO>> getActivitiesByTripId(@AuthenticationPrincipal Long userId, @PathVariable Long tripId) {
        List<ActivityDTO> activityDTOList = activityService.getActivitiesForTrip(tripId, userId).stream()
                .map(ActivityController::convertToDTO)
                .toList();
        return ResponseEntity.ok(activityDTOList);
    }

    // Update an existing activity (it stays on its current trip)
    @PutMapping("/{id}")
    public ResponseEntity<ActivityDTO> updateActivity(@AuthenticationPrincipal Long userId, @PathVariable Long id,
                                                      @Valid @RequestBody ActivityDTO activityDTO) {
        Activity activity = new Activity();
        activity.setTitle(activityDTO.getTitle());
        activity.setCategory(activityDTO.getCategory());
        activity.setDescription(activityDTO.getDescription());
        activity.setStartDateTime(activityDTO.getStartDateTime());
        activity.setEndDateTime(activityDTO.getEndDateTime());
        activity.setCost(activityDTO.getCost());

        Activity updatedActivity = activityService.updateActivity(id, userId, activity);
        return ResponseEntity.ok(convertToDTO(updatedActivity));
    }

    // Delete an activity by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActivity(@AuthenticationPrincipal Long userId, @PathVariable Long id) {
        activityService.deleteActivity(id, userId);
        return ResponseEntity.ok().build();
    }
}
