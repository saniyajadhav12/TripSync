package edu.neu.csye6200.tripsync.dto;

import edu.neu.csye6200.tripsync.model.Category;

import edu.neu.csye6200.tripsync.validation.OnCreate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Used for both requests and responses. tripId is required only when creating an activity.
@Data
public class ActivityDTO {
    private Long id;
    @NotBlank
    private String title;
    @NotNull
    private Category category;
    private String description;
    @NotBlank
    private String startDateTime;
    private String endDateTime;
    @NotNull
    @PositiveOrZero
    private BigDecimal cost;
    @NotNull(groups = OnCreate.class)
    private Long tripId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
