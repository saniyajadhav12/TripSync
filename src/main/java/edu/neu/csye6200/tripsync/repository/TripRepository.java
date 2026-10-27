package edu.neu.csye6200.tripsync.repository;

import edu.neu.csye6200.tripsync.model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
    List<Trip> findByUserId(Long userId);
    Optional<Trip> findByIdAndUserId(Long id, Long userId);
}
