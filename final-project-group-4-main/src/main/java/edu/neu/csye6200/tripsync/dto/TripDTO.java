package edu.neu.csye6200.tripsync.dto;

import lombok.Data;

import java.util.List;

@Data
public class TripDTO {
    private Long id;
    private String destination;
    private String description;
    private String startDate;
    private String endDate;
    private Long userId;
    private List<ActivityDTO> activities;
    private List<AccommodationDTO> accommodations;
    private String createdAt;
    private String updatedAt;
}