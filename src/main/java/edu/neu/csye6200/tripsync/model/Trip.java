package edu.neu.csye6200.tripsync.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String destination;
    private String description;
    private String startDate;
    private String endDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Activity> activities;

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Accommodation> accommodations;

    @PrePersist
    public void prePersist() {
        if (user == null) {
            System.out.println("Warning: User is null while creating a trip.");
        }
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }


    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

}