package edu.neu.csye6200.tripsync.config;

import edu.neu.csye6200.tripsync.model.Trip;
import edu.neu.csye6200.tripsync.model.User;
import edu.neu.csye6200.tripsync.repository.AccommodationRepository;
import edu.neu.csye6200.tripsync.repository.ActivityRepository;
import edu.neu.csye6200.tripsync.repository.TripRepository;
import edu.neu.csye6200.tripsync.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// Runs with the demo profile on its own in-memory database, so the seeded data doesn't mix with other tests
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:tripsync-demo;DB_CLOSE_DELAY=-1;MODE=MySQL")
@ActiveProfiles({"test", "demo"})
class DemoDataSeederTest {

    @Autowired
    private DemoDataSeeder seeder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private AccommodationRepository accommodationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void seedsDemoUserWithTripsOnStartup() {
        User demo = userRepository.findByUsername(DemoDataSeeder.DEMO_USERNAME);

        assertThat(demo).isNotNull();
        assertThat(demo.getFirstName()).isEqualTo("Demo");
        assertThat(demo.getLastName()).isEqualTo("Traveler");
        assertThat(demo.getEmail()).isEqualTo("demo@example.com");
        assertThat(demo.getPassword()).isNotEqualTo(DemoDataSeeder.DEMO_PASSWORD);
        assertThat(passwordEncoder.matches(DemoDataSeeder.DEMO_PASSWORD, demo.getPassword())).isTrue();

        List<Trip> trips = tripRepository.findByUserId(demo.getId());
        assertThat(trips).extracting(Trip::getDestination)
                .containsExactlyInAnyOrder("Kyoto, Japan", "Moab, Utah", "Lisbon, Portugal");
        for (Trip trip : trips) {
            assertThat(LocalDate.parse(trip.getStartDate())).isAfter(LocalDate.now());
            assertThat(activityRepository.findByTripId(trip.getId())).hasSizeBetween(2, 3);
            assertThat(accommodationRepository.findByTripId(trip.getId())).hasSizeBetween(1, 2);
        }
    }

    @Test
    void runningAgainDoesNotDuplicateData() {
        long users = userRepository.count();
        long trips = tripRepository.count();
        long activities = activityRepository.count();
        long accommodations = accommodationRepository.count();

        seeder.run(); // the demo user already exists from startup, so this must be a no-op

        assertThat(userRepository.count()).isEqualTo(users);
        assertThat(tripRepository.count()).isEqualTo(trips);
        assertThat(activityRepository.count()).isEqualTo(activities);
        assertThat(accommodationRepository.count()).isEqualTo(accommodations);
    }
}
