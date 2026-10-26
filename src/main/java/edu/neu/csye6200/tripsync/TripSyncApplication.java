package edu.neu.csye6200.tripsync;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

// Users are authenticated against our own table with JWTs, so skip Spring's default in-memory user
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class TripSyncApplication {

	public static void main(String[] args) {
		SpringApplication.run(TripSyncApplication.class, args);
	}

}
