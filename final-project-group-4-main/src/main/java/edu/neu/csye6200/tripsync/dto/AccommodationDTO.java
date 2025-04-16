package edu.neu.csye6200.tripsync.dto;

import edu.neu.csye6200.tripsync.model.Type;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
public class AccommodationDTO {
    private Long id;
    private String name;
    private String address;
    private Type type;
    private String checkInDate;
    private String checkOutDate;
    private String review;
    private BigDecimal cost;
    private Long tripId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
