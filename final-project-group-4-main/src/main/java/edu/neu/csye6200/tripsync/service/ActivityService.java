package edu.neu.csye6200.tripsync.service;

import edu.neu.csye6200.tripsync.model.Activity;
import java.util.List;

public interface ActivityService {
    Activity getActivityById(Long id);
    List<Activity> getAllActivities();
    List<Activity> getActivitiesByTripId(Long tripID);
    Activity createActivity(Activity activity);
    Activity updateActivity(Long id, Activity activity);
    boolean deleteActivity(Long id);
}