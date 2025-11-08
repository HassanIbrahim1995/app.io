package com.hotel.management.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RoomMoveRequest {
    
    @NotNull(message = "Reservation ID is required")
    private Long reservationId;
    
    @NotNull(message = "New room ID is required")
    private Long newRoomId;
    
    private String reason;
    
    private Boolean waiveCharges = false;
}
