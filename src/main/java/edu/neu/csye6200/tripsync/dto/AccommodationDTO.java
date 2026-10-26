package edu.neu.csye6200.tripsync.dto;

import edu.neu.csye6200.tripsync.model.Type;

import edu.neu.csye6200.tripsync.validation.DateRange;
import edu.neu.csye6200.tripsync.validation.IsoDate;
import edu.neu.csye6200.tripsync.validation.OnCreate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;


// Used for both requests and responses. tripId is required only when creating an accommodation.
@Data
@DateRange(start = "checkInDate", end = "checkOutDate", message = "must not be before checkInDate")
public class AccommodationDTO {
    private Long id;
    @NotBlank
    private String name;
    private String address;
    @NotNull
    private Type type;
    @NotBlank
    @IsoDate
    private String checkInDate;
    @NotBlank
    @IsoDate
    private String checkOutDate;
    private String review;
    @PositiveOrZero
    private BigDecimal cost;
    @NotNull(groups = OnCreate.class)
    private Long tripId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
