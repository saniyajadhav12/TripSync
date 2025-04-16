package edu.neu.csye6200.tripsync.controller;

import edu.neu.csye6200.tripsync.dto.ActivityDTO;
import edu.neu.csye6200.tripsync.model.Activity;
import edu.neu.csye6200.tripsync.model.Trip;
import edu.neu.csye6200.tripsync.service.ActivityService;
import edu.neu.csye6200.tripsync.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    ActivityDTO convertToDTO(Activity activity){
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

    // Create a new activity
    @PostMapping
    public ResponseEntity<ActivityDTO> createActivity(@RequestBody ActivityDTO activityDTO) {
        Trip trip = tripService.getTripById(activityDTO.getTripId());
        if (trip==null){
            return ResponseEntity.status(404).body(null); // User not found
        }

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

    // Get all activities
    @GetMapping
    public ResponseEntity<List<ActivityDTO>> getAllActivities() {
        List<Activity> activities = activityService.getAllActivities();
        List<ActivityDTO> activityDTOList = activities.stream().map(this::convertToDTO).toList();
        return ResponseEntity.ok(activityDTOList);
    }

    // Get an activity by its ID
    @GetMapping("/{id}")
    public ResponseEntity<ActivityDTO> getActivityById(@PathVariable Long id) {
        Activity activity = activityService.getActivityById(id);
        return activity==null ? ResponseEntity.notFound().build() : ResponseEntity.ok(convertToDTO(activity));
    }

    // Get all activities by trip ID
    @GetMapping("/trip/{tripId}")
    public ResponseEntity<List<ActivityDTO>> getActivitiesByTripId(@PathVariable Long tripId) {
        Trip trip = tripService.getTripById(tripId);
        if (trip == null) {
            return ResponseEntity.status(404).body(null); // Trip not found
        }

        List<Activity> activities = activityService.getActivitiesByTripId(tripId);
        List<ActivityDTO> activityDTOList = activities.stream().map(this::convertToDTO).toList();
        return ResponseEntity.ok(activityDTOList);
    }



    // Update an existing activity
    @PutMapping("/{id}")
    public ResponseEntity<ActivityDTO> updateActivity(@PathVariable Long id, @RequestBody ActivityDTO activityDTO) {
        Activity activity = activityService.getActivityById(id);
        if(activity==null){
            return ResponseEntity.notFound().build();
        }
        Trip trip = tripService.getTripById(activityDTO.getTripId());
        if(trip==null){
            return ResponseEntity.status(404).body(null);
        }

        activity.setTitle(activityDTO.getTitle());
        activity.setCategory(activityDTO.getCategory());
        activity.setDescription(activityDTO.getDescription());
        activity.setStartDateTime(activityDTO.getStartDateTime());
        activity.setEndDateTime(activityDTO.getEndDateTime());
        activity.setCost(activityDTO.getCost());
        activity.setTrip(trip);

        Activity updatedActivity = activityService.updateActivity(id, activity);
        return ResponseEntity.ok(convertToDTO(updatedActivity));

    }

    // Delete an activity by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActivity(@PathVariable Long id) {
        return activityService.deleteActivity(id)
                ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }
}
