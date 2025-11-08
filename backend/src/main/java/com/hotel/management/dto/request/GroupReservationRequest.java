package com.hotel.management.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class GroupReservationRequest {
    
    @NotBlank(message = "Group name is required")
    private String groupName;
    
    @NotNull(message = "Check-in date is required")
    private LocalDate checkInDate;
    
    @NotNull(message = "Check-out date is required")
    private LocalDate checkOutDate;
    
    @NotEmpty(message = "At least one room is required")
    private List<GroupRoomRequest> rooms;
    
    @NotBlank(message = "Contact person name is required")
    private String contactPersonName;
    
    @NotBlank(message = "Contact email is required")
    private String contactEmail;
    
    private String contactPhone;
    
    private String specialRequests;
    
    private String promoCode;
    
    @Data
    public static class GroupRoomRequest {
        @NotNull(message = "Room type ID is required")
        private Long roomTypeId;
        
        @NotNull(message = "Number of guests is required")
        @Min(value = 1, message = "At least one guest is required")
        private Integer numberOfGuests;
        
        @NotNull(message = "Number of adults is required")
        @Min(value = 1, message = "At least one adult is required")
        private Integer numberOfAdults;
        
        private Integer numberOfChildren = 0;
        
        private String guestName;
        
        private String guestEmail;
    }
}
