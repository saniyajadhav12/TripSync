package edu.neu.csye6200.tripsync.config;

import edu.neu.csye6200.tripsync.model.Accommodation;
import edu.neu.csye6200.tripsync.model.Activity;
import edu.neu.csye6200.tripsync.model.Category;
import edu.neu.csye6200.tripsync.model.Trip;
import edu.neu.csye6200.tripsync.model.Type;
import edu.neu.csye6200.tripsync.model.User;
import edu.neu.csye6200.tripsync.repository.AccommodationRepository;
import edu.neu.csye6200.tripsync.repository.ActivityRepository;
import edu.neu.csye6200.tripsync.repository.TripRepository;
import edu.neu.csye6200.tripsync.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

// Seeds a demo user with sample trips when the "demo" profile is active (see docs/DEMO.md).
// Never runs in the default profile or in tests, and skips seeding if the demo user already exists.
@Component
@Profile("demo")
public class DemoDataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoDataSeeder.class);

    // Public, local-only demo credentials. They are intentionally documented in docs/DEMO.md and are
    // not a secret, which is why the demo profile must never be enabled on a shared or real deployment.
    static final String DEMO_USERNAME = "demo";
    static final String DEMO_PASSWORD = "tripsync-demo";
    static final String DEMO_EMAIL = "demo@example.com";

    private final UserRepository userRepository;
    private final TripRepository tripRepository;
    private final ActivityRepository activityRepository;
    private final AccommodationRepository accommodationRepository;
    private final PasswordEncoder passwordEncoder;

    public DemoDataSeeder(UserRepository userRepository, TripRepository tripRepository,
                          ActivityRepository activityRepository, AccommodationRepository accommodationRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tripRepository = tripRepository;
        this.activityRepository = activityRepository;
        this.accommodationRepository = accommodationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.findByUsername(DEMO_USERNAME) != null) {
            log.info("Demo user '{}' already exists; skipping demo data", DEMO_USERNAME);
            return;
        }
        if (userRepository.findByEmail(DEMO_EMAIL) != null) {
            log.warn("Email {} is already used by another account; skipping demo data", DEMO_EMAIL);
            return;
        }

        User demo = new User();
        demo.setFirstName("Demo");
        demo.setLastName("Traveler");
        demo.setEmail(DEMO_EMAIL);
        demo.setUsername(DEMO_USERNAME);
        demo.setPassword(passwordEncoder.encode(DEMO_PASSWORD));
        userRepository.save(demo);

        // Dates are relative to today so the trips are always in the future
        LocalDate today = LocalDate.now();

        // Trips are created in this order on purpose: the Home page assigns its sample photos by
        // position (a pagoda, a red-rock arch, then a city skyline), so each card gets a fitting photo.
        LocalDate kyotoStart = today.plusDays(75);
        Trip kyoto = trip(demo, "Kyoto, Japan",
                "Temples, gardens and food markets in Japan's old capital.",
                kyotoStart, kyotoStart.plusDays(7));
        activity(kyoto, "Fushimi Inari sunrise hike", Category.Adventure,
                "Walk through thousands of torii gates before the crowds arrive.",
                kyotoStart.plusDays(1), "0.00");
        activity(kyoto, "Tea ceremony in Gion", Category.Cultural_Experience,
                "A traditional matcha ceremony in a wooden teahouse.",
                kyotoStart.plusDays(3), "35.00");
        activity(kyoto, "Nishiki Market food walk", Category.Shopping,
                "Pickles, tamagoyaki and knives along 'Kyoto's kitchen'.",
                kyotoStart.plusDays(5), "25.00");
        accommodation(kyoto, "Hotel Gion Garden", "Higashiyama Ward, Kyoto", Type.Hotel,
                kyotoStart, kyotoStart.plusDays(4), "720.00");
        accommodation(kyoto, "Arashiyama Riverside Hostel", "Ukyo Ward, Kyoto", Type.Hostel,
                kyotoStart.plusDays(4), kyotoStart.plusDays(7), "135.00");

        LocalDate moabStart = today.plusDays(120);
        Trip moab = trip(demo, "Moab, Utah",
                "Red-rock arches, canyon viewpoints and dark desert skies.",
                moabStart, moabStart.plusDays(5));
        activity(moab, "Delicate Arch sunset hike", Category.Adventure,
                "A 5 km round trip across slickrock to Utah's most famous arch.",
                moabStart.plusDays(1), "0.00");
        activity(moab, "Canyonlands scenic drive", Category.Sightseeing,
                "Island in the Sky viewpoints, from Mesa Arch to Grand View Point.",
                moabStart.plusDays(2), "30.00");
        activity(moab, "Stargazing at Dead Horse Point", Category.Leisure,
                "A ranger-led night sky program in an International Dark Sky Park.",
                moabStart.plusDays(3), "15.00");
        accommodation(moab, "Devils Garden Campground", "Arches National Park, Moab", Type.Camping,
                moabStart, moabStart.plusDays(2), "50.00");
        accommodation(moab, "Red Cliffs Lodge", "Highway 128, Moab", Type.Resort,
                moabStart.plusDays(2), moabStart.plusDays(5), "585.00");

        LocalDate lisbonStart = today.plusDays(30);
        Trip lisbon = trip(demo, "Lisbon, Portugal",
                "A week of trams, tiled facades, pastries and sunsets over the Tagus.",
                lisbonStart, lisbonStart.plusDays(6));
        activity(lisbon, "Tram 28 through Alfama", Category.Sightseeing,
                "Ride the classic yellow tram past the cathedral and up to the castle viewpoints.",
                lisbonStart.plusDays(1), "3.00");
        activity(lisbon, "Pastéis de Belém tasting", Category.Dining,
                "Custard tarts at the original bakery, then a walk to the Belém Tower.",
                lisbonStart.plusDays(2), "12.50");
        activity(lisbon, "Fado night in Alfama", Category.Cultural_Experience,
                "Dinner and live fado in a small family-run tavern.",
                lisbonStart.plusDays(4), "45.00");
        accommodation(lisbon, "Alfama Guesthouse", "Rua dos Remédios 12, Lisbon", Type.Guesthouse,
                lisbonStart, lisbonStart.plusDays(6), "540.00");

        log.info("Seeded demo user '{}' with 3 trips", DEMO_USERNAME);
    }

    private Trip trip(User owner, String destination, String description, LocalDate start, LocalDate end) {
        Trip trip = new Trip();
        trip.setUser(owner);
        trip.setDestination(destination);
        trip.setDescription(description);
        trip.setStartDate(start.toString());
        trip.setEndDate(end.toString());
        return tripRepository.save(trip);
    }

    private void activity(Trip trip, String title, Category category, String description, LocalDate day, String cost) {
        Activity activity = new Activity();
        activity.setTrip(trip);
        activity.setTitle(title);
        activity.setCategory(category);
        activity.setDescription(description);
        activity.setStartDateTime(day.toString());
        activity.setEndDateTime(day.toString());
        activity.setCost(new BigDecimal(cost));
        activityRepository.save(activity);
    }

    private void accommodation(Trip trip, String name, String address, Type type,
                               LocalDate checkIn, LocalDate checkOut, String cost) {
        Accommodation accommodation = new Accommodation();
        accommodation.setTrip(trip);
        accommodation.setName(name);
        accommodation.setAddress(address);
        accommodation.setType(type);
        accommodation.setCheckInDate(checkIn.toString());
        accommodation.setCheckOutDate(checkOut.toString());
        accommodation.setCost(new BigDecimal(cost));
        accommodationRepository.save(accommodation);
    }
}
