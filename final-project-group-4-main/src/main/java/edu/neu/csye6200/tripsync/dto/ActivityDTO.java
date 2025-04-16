package edu.neu.csye6200.tripsync.dto;

import edu.neu.csye6200.tripsync.model.Category;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ActivityDTO {
    private Long id;
    private String title;
    private Category category;
    private String description;
    private String startDateTime;
    private String endDateTime;
    private BigDecimal cost;
    private Long tripId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
