package com.hotel.management.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ReservationRequest {
    
    @NotNull(message = "Room ID is required")
    private Long roomId;
    
    @NotNull(message = "Check-in date is required")
    @Future(message = "Check-in date must be in the future")
    private LocalDate checkInDate;
    
    @NotNull(message = "Check-out date is required")
    @Future(message = "Check-out date must be in the future")
    private LocalDate checkOutDate;
    
    @NotNull(message = "Number of guests is required")
    @Min(value = 1, message = "At least one guest is required")
    private Integer numberOfGuests;
    
    @NotNull(message = "Number of adults is required")
    @Min(value = 1, message = "At least one adult is required")
    private Integer numberOfAdults;
    
    private Integer numberOfChildren = 0;
    
    private String specialRequests;
    private String promoCode;
    
    private List<AddonRequest> addons;
}
