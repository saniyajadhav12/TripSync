package edu.neu.csye6200.tripsync.service.impl;

import edu.neu.csye6200.tripsync.model.Activity;
import edu.neu.csye6200.tripsync.repository.ActivityRepository;
import edu.neu.csye6200.tripsync.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ActivityServiceImpl implements ActivityService {

    private final ActivityRepository activityRepository;

    @Autowired
    public ActivityServiceImpl(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @Override
    public Activity getActivityById(Long id) {
        Optional<Activity> activityOptional = activityRepository.findById(id);

        if (activityOptional.isEmpty()) {
            throw new IllegalArgumentException("Activity not found with ID: " + id);
        }

        return activityOptional.get();
    }

    @Override
    public List<Activity> getAllActivities() {
        return activityRepository.findAll();
    }

    public List<Activity> getActivitiesByTripId(Long tripId) {
        return activityRepository.findByTripId(tripId);
    }

    @Override
    public Activity createActivity(Activity activity) {
        return activityRepository.save(activity);
    }

    @Override
    public Activity updateActivity(Long id, Activity activity) {
        Optional<Activity> existingActivityOptional = activityRepository.findById(id);

        if (existingActivityOptional.isEmpty()) {
            throw new IllegalArgumentException("Activity not found with ID: " + id); // or custom exception
        }

        Activity existingActivity = existingActivityOptional.get();

        existingActivity.setTitle(activity.getTitle());
        existingActivity.setCategory(activity.getCategory());
        existingActivity.setDescription(activity.getDescription());
        existingActivity.setStartDateTime(activity.getStartDateTime());
        existingActivity.setEndDateTime(activity.getEndDateTime());
        existingActivity.setCost(activity.getCost());

        return activityRepository.save(existingActivity);
    }

    @Override
    public boolean deleteActivity(Long id) {
        Optional<Activity> activityOptional = activityRepository.findById(id);

        if (activityOptional.isEmpty()) {
            return false;
        }
        activityRepository.deleteById(id);
        return true;
    }
}
