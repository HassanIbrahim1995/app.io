package com.hotel.management.dto.response;

import com.hotel.management.entity.enums.RevenueStatus;
import com.hotel.management.entity.enums.RevenueType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevenueResponse {
    private Long id;
    private String revenueNumber;
    private RevenueType revenueType;
    private RevenueStatus status;
    private BigDecimal amount;
    private BigDecimal taxAmount;
    private BigDecimal netAmount;
    private LocalDate revenueDate;
    private Long reservationId;
    private String reservationNumber;
    private Long guestId;
    private String guestName;
    private Long roomId;
    private String roomNumber;
    private Long paymentId;
    private String department;
    private String costCenter;
    private String glCode;
    private String description;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal discountAmount;
    private String currency;
    private String sourceReference;
    private Boolean postedToGl;
    private LocalDate postingDate;
    private LocalDate recognizedDate;
    private String notes;
    private LocalDateTime createdAt;
}
