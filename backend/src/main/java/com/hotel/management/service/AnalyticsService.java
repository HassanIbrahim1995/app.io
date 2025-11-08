package com.hotel.management.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public interface AnalyticsService {
    // Revenue Analytics
    BigDecimal getTotalRevenue(LocalDate startDate, LocalDate endDate);
    BigDecimal getRevenueByMonth(int year, int month);
    Map<String, BigDecimal> getRevenueByRoomType(LocalDate startDate, LocalDate endDate);
    
    // Occupancy Analytics
    Double getOccupancyRate(LocalDate startDate, LocalDate endDate);
    Double getOccupancyRateByRoomType(Long roomTypeId, LocalDate startDate, LocalDate endDate);
    Map<String, Double> getOccupancyByMonth(int year);
    
    // Reservation Analytics
    Long getTotalReservations(LocalDate startDate, LocalDate endDate);
    Map<String, Long> getReservationsByStatus(LocalDate startDate, LocalDate endDate);
    Double getAverageLengthOfStay(LocalDate startDate, LocalDate endDate);
    Double getCancellationRate(LocalDate startDate, LocalDate endDate);
    
    // Guest Analytics
    Long getTotalGuests();
    Long getNewGuestsCount(LocalDate startDate, LocalDate endDate);
    Long getReturningGuestsCount(LocalDate startDate, LocalDate endDate);
    Map<String, Long> getGuestsByLoyaltyTier();
    
    // Review Analytics
    Double getAverageRating();
    Map<String, Double> getAverageRatingByRoomType();
    Long getTotalReviews(LocalDate startDate, LocalDate endDate);
    
    // Financial Analytics
    BigDecimal getAverageRevenuePerAvailableRoom(LocalDate startDate, LocalDate endDate);
    BigDecimal getAverageRevenuePerOccupiedRoom(LocalDate startDate, LocalDate endDate);
    Map<String, BigDecimal> getRevenueBySource(LocalDate startDate, LocalDate endDate);
}
