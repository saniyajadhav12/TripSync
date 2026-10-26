package edu.neu.csye6200.tripsync;

import edu.neu.csye6200.tripsync.config.DemoDataSeeder;
import edu.neu.csye6200.tripsync.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class TripSyncApplicationTests {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    @Test
    void contextLoads() {
    }

    @Test
    void demoSeederIsNotActiveWithoutDemoProfile() {
        assertThat(context.getBeanNamesForType(DemoDataSeeder.class)).isEmpty();
        assertThat(userRepository.findByUsername("demo")).isNull();
    }
}
