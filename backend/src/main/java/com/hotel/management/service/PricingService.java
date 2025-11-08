package com.hotel.management.service;

import com.hotel.management.dto.response.PricingBreakdown;
import com.hotel.management.entity.Reservation;

import java.time.LocalDate;

public interface PricingService {
    PricingBreakdown calculateReservationPricing(
        Long roomId,
        LocalDate checkIn,
        LocalDate checkOut,
        Integer numberOfGuests,
        String city,
        String promoCode
    );
    
    PricingBreakdown recalculateReservationPricing(Reservation reservation);
}
