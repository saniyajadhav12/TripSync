package edu.neu.csye6200.tripsync.dto;

import edu.neu.csye6200.tripsync.validation.DateRange;
import edu.neu.csye6200.tripsync.validation.IsoDate;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

// Used for both requests and responses. Server-set fields (id, userId, activities, accommodations,
// createdAt, updatedAt) are ignored on input.
@Data
@DateRange(start = "startDate", end = "endDate", message = "must not be before startDate")
public class TripDTO {
    private Long id;
    @NotBlank
    private String destination;
    private String description;
    @NotBlank
    @IsoDate
    private String startDate;
    @NotBlank
    @IsoDate
    private String endDate;
    private Long userId;
    private List<ActivityDTO> activities;
    private List<AccommodationDTO> accommodations;
    private String createdAt;
    private String updatedAt;
}
