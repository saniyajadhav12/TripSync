package edu.neu.csye6200.tripsync.repository;

import edu.neu.csye6200.tripsync.model.Accommodation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {
    public List<Accommodation> findByTripId(Long tripId);
}
