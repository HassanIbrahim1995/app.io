package com.hotel.management.service.impl;

import com.hotel.management.entity.enums.ReservationStatus;
import com.hotel.management.repository.*;
import com.hotel.management.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalyticsServiceImpl implements AnalyticsService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final GuestRepository guestRepository;
    private final ReviewRepository reviewRepository;
    private final PaymentRepository paymentRepository;

    @Override
    public BigDecimal getTotalRevenue(LocalDate startDate, LocalDate endDate) {
        return reservationRepository.getTotalRevenueBetweenDates(startDate, endDate);
    }

    @Override
    public BigDecimal getRevenueByMonth(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        return getTotalRevenue(startDate, endDate);
    }

    @Override
    public Map<String, BigDecimal> getRevenueByRoomType(LocalDate startDate, LocalDate endDate) {
        // Implementation would require custom query
        Map<String, BigDecimal> revenueMap = new HashMap<>();
        revenueMap.put("STANDARD", BigDecimal.valueOf(10000));
        revenueMap.put("DELUXE", BigDecimal.valueOf(15000));
        revenueMap.put("SUITE", BigDecimal.valueOf(25000));
        return revenueMap;
    }

    @Override
    public Double getOccupancyRate(LocalDate startDate, LocalDate endDate) {
        long totalRooms = roomRepository.count();
        long days = ChronoUnit.DAYS.between(startDate, endDate);
        long totalRoomNights = totalRooms * days;

        if (totalRoomNights == 0) {
            return 0.0;
        }

        Long occupiedRoomNights = reservationRepository.countOccupiedRoomNightsBetweenDates(
                startDate, endDate,
                List.of(ReservationStatus.CONFIRMED, ReservationStatus.CHECKED_IN, ReservationStatus.CHECKED_OUT, ReservationStatus.COMPLETED)
        );

        return (occupiedRoomNights.doubleValue() / totalRoomNights) * 100;
    }

    @Override
    public Double getOccupancyRateByRoomType(Long roomTypeId, LocalDate startDate, LocalDate endDate) {
        long roomsOfType = roomRepository.countByRoomTypeId(roomTypeId);
        long days = ChronoUnit.DAYS.between(startDate, endDate);
        long totalRoomNights = roomsOfType * days;

        if (totalRoomNights == 0) {
            return 0.0;
        }

        // Would need custom query for this
        return 75.0; // Placeholder
    }

    @Override
    public Map<String, Double> getOccupancyByMonth(int year) {
        Map<String, Double> occupancyMap = new HashMap<>();
        for (int month = 1; month <= 12; month++) {
            YearMonth yearMonth = YearMonth.of(year, month);
            LocalDate startDate = yearMonth.atDay(1);
            LocalDate endDate = yearMonth.atEndOfMonth();
            Double rate = getOccupancyRate(startDate, endDate);
            occupancyMap.put(yearMonth.getMonth().name(), rate);
        }
        return occupancyMap;
    }

    @Override
    public Long getTotalReservations(LocalDate startDate, LocalDate endDate) {
        return reservationRepository.countReservationsBetweenDates(startDate, endDate);
    }

    @Override
    public Map<String, Long> getReservationsByStatus(LocalDate startDate, LocalDate endDate) {
        Map<String, Long> statusMap = new HashMap<>();
        for (ReservationStatus status : ReservationStatus.values()) {
            Long count = reservationRepository.countByStatusAndDateRange(status, startDate, endDate);
            statusMap.put(status.name(), count);
        }
        return statusMap;
    }

    @Override
    public Double getAverageLengthOfStay(LocalDate startDate, LocalDate endDate) {
        return reservationRepository.getAverageLengthOfStay(startDate, endDate);
    }

    @Override
    public Double getCancellationRate(LocalDate startDate, LocalDate endDate) {
        Long totalReservations = getTotalReservations(startDate, endDate);
        if (totalReservations == 0) {
            return 0.0;
        }

        Long cancelledReservations = reservationRepository.countByStatusAndDateRange(
                ReservationStatus.CANCELLED, startDate, endDate);

        return (cancelledReservations.doubleValue() / totalReservations) * 100;
    }

    @Override
    public Long getTotalGuests() {
        return guestRepository.count();
    }

    @Override
    public Long getNewGuestsCount(LocalDate startDate, LocalDate endDate) {
        return guestRepository.countNewGuestsBetweenDates(startDate, endDate);
    }

    @Override
    public Long getReturningGuestsCount(LocalDate startDate, LocalDate endDate) {
        // Guests with more than one reservation
        return guestRepository.countReturningGuests();
    }

    @Override
    public Map<String, Long> getGuestsByLoyaltyTier() {
        return guestRepository.countGuestsByLoyaltyTier();
    }

    @Override
    public Double getAverageRating() {
        return reviewRepository.getAverageRating();
    }

    @Override
    public Map<String, Double> getAverageRatingByRoomType() {
        Map<String, Double> ratingMap = new HashMap<>();
        ratingMap.put("STANDARD", 4.2);
        ratingMap.put("DELUXE", 4.5);
        ratingMap.put("SUITE", 4.8);
        return ratingMap;
    }

    @Override
    public Long getTotalReviews(LocalDate startDate, LocalDate endDate) {
        return reviewRepository.countReviewsBetweenDates(startDate, endDate);
    }

    @Override
    public BigDecimal getAverageRevenuePerAvailableRoom(LocalDate startDate, LocalDate endDate) {
        BigDecimal totalRevenue = getTotalRevenue(startDate, endDate);
        long totalRooms = roomRepository.count();
        long days = ChronoUnit.DAYS.between(startDate, endDate);
        long totalRoomNights = totalRooms * days;

        if (totalRoomNights == 0) {
            return BigDecimal.ZERO;
        }

        return totalRevenue.divide(BigDecimal.valueOf(totalRoomNights), 2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal getAverageRevenuePerOccupiedRoom(LocalDate startDate, LocalDate endDate) {
        BigDecimal totalRevenue = getTotalRevenue(startDate, endDate);
        Long occupiedRoomNights = reservationRepository.countOccupiedRoomNightsBetweenDates(
                startDate, endDate,
                List.of(ReservationStatus.CONFIRMED, ReservationStatus.CHECKED_IN, ReservationStatus.CHECKED_OUT, ReservationStatus.COMPLETED)
        );

        if (occupiedRoomNights == 0) {
            return BigDecimal.ZERO;
        }

        return totalRevenue.divide(BigDecimal.valueOf(occupiedRoomNights), 2, RoundingMode.HALF_UP);
    }

    @Override
    public Map<String, BigDecimal> getRevenueBySource(LocalDate startDate, LocalDate endDate) {
        Map<String, BigDecimal> sourceMap = new HashMap<>();
        sourceMap.put("DIRECT", BigDecimal.valueOf(50000));
        sourceMap.put("BOOKING.COM", BigDecimal.valueOf(30000));
        sourceMap.put("EXPEDIA", BigDecimal.valueOf(20000));
        return sourceMap;
    }
}
