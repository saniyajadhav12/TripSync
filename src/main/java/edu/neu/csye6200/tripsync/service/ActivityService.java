package edu.neu.csye6200.tripsync.service;

import edu.neu.csye6200.tripsync.model.Activity;
import java.util.List;

// All lookups are scoped to the user who owns the parent trip
public interface ActivityService {
    Activity getActivityForUser(Long id, Long userId);
    List<Activity> getActivitiesForTrip(Long tripId, Long userId);
    Activity createActivity(Activity activity);
    Activity updateActivity(Long id, Long userId, Activity activity);
    void deleteActivity(Long id, Long userId);
}
