package com.hotel.management.dto.response;

import com.hotel.management.entity.enums.RoomStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponse {
    private Long id;
    private String roomNumber;
    private Long roomTypeId;
    private String roomTypeName;
    private Integer floor;
    private BigDecimal basePrice;
    private RoomStatus status;
    private Integer maxOccupancy;
    private Boolean isAccessible;
    private Boolean isSmokingAllowed;
    private String description;
    private List<String> imageUrls;
    private List<AmenityResponse> amenities;
    private Double averageRating;
}
