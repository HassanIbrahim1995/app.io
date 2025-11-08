package com.hotel.management.dto.request;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class RoomSearchRequest {
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate checkInDate;
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate checkOutDate;
    
    private Long roomTypeId;
    private Integer guests;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Boolean isAccessible;
    private Boolean isSmokingAllowed;
}
