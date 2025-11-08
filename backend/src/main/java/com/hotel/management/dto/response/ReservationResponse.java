package com.hotel.management.dto.response;

import com.hotel.management.entity.enums.ReservationStatus;
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
public class ReservationResponse {
    private Long id;
    private String reservationNumber;
    private Long guestId;
    private String guestName;
    private Long roomId;
    private String roomNumber;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private LocalDateTime actualCheckIn;
    private LocalDateTime actualCheckOut;
    private Integer numberOfGuests;
    private Integer numberOfAdults;
    private Integer numberOfChildren;
    private ReservationStatus status;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal discountAmount;
    private BigDecimal taxAmount;
    private String specialRequests;
    private String promoCode;
    private String source;
    private LocalDateTime createdAt;
}
