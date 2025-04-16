package edu.neu.csye6200.tripsync.repository;

import edu.neu.csye6200.tripsync.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    public List<Activity> findByTripId(Long tripId);
}

