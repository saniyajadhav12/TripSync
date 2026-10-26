package edu.neu.csye6200.tripsync.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "users") // "user" is a reserved word in H2 and other databases
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    @Email
    @Column(unique = true)
    private String email;
    @Column(unique = true)
    private String username;
    @ToString.Exclude
    private String password;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public User(Long userId) {
        this.id = userId;
    }

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
