package edu.neu.csye6200.tripsync.service.impl;

import edu.neu.csye6200.tripsync.exception.ResourceNotFoundException;
import edu.neu.csye6200.tripsync.model.Activity;
import edu.neu.csye6200.tripsync.repository.ActivityRepository;
import edu.neu.csye6200.tripsync.service.ActivityService;
import edu.neu.csye6200.tripsync.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityServiceImpl implements ActivityService {

    private final ActivityRepository activityRepository;
    private final TripService tripService;

    @Autowired
    public ActivityServiceImpl(ActivityRepository activityRepository, TripService tripService) {
        this.activityRepository = activityRepository;
        this.tripService = tripService;
    }

    @Override
    public Activity getActivityForUser(Long id, Long userId) {
        return activityRepository.findByIdAndTripUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Activity not found"));
    }

    @Override
    public List<Activity> getActivitiesForTrip(Long tripId, Long userId) {
        tripService.getTripForUser(tripId, userId); // 404 unless the trip belongs to the user
        return activityRepository.findByTripId(tripId);
    }

    @Override
    public Activity createActivity(Activity activity) {
        return activityRepository.save(activity);
    }

    @Override
    public Activity updateActivity(Long id, Long userId, Activity activity) {
        Activity existingActivity = getActivityForUser(id, userId);

        existingActivity.setTitle(activity.getTitle());
        existingActivity.setCategory(activity.getCategory());
        existingActivity.setDescription(activity.getDescription());
        existingActivity.setStartDateTime(activity.getStartDateTime());
        existingActivity.setEndDateTime(activity.getEndDateTime());
        existingActivity.setCost(activity.getCost());

        return activityRepository.save(existingActivity);
    }

    @Override
    public void deleteActivity(Long id, Long userId) {
        activityRepository.delete(getActivityForUser(id, userId));
    }
}
