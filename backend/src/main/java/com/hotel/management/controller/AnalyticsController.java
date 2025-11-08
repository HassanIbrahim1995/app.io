package com.hotel.management.controller;

import com.hotel.management.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics", description = "Analytics and reporting endpoints")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/revenue/total")
    @Operation(summary = "Get total revenue for date range")
    public ResponseEntity<BigDecimal> getTotalRevenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        BigDecimal revenue = analyticsService.getTotalRevenue(startDate, endDate);
        return ResponseEntity.ok(revenue);
    }

    @GetMapping("/revenue/month")
    @Operation(summary = "Get revenue by month")
    public ResponseEntity<BigDecimal> getRevenueByMonth(
            @RequestParam int year,
            @RequestParam int month) {
        BigDecimal revenue = analyticsService.getRevenueByMonth(year, month);
        return ResponseEntity.ok(revenue);
    }

    @GetMapping("/revenue/by-room-type")
    @Operation(summary = "Get revenue by room type")
    public ResponseEntity<Map<String, BigDecimal>> getRevenueByRoomType(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Map<String, BigDecimal> revenue = analyticsService.getRevenueByRoomType(startDate, endDate);
        return ResponseEntity.ok(revenue);
    }

    @GetMapping("/occupancy/rate")
    @Operation(summary = "Get occupancy rate")
    public ResponseEntity<Double> getOccupancyRate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Double rate = analyticsService.getOccupancyRate(startDate, endDate);
        return ResponseEntity.ok(rate);
    }

    @GetMapping("/occupancy/by-month")
    @Operation(summary = "Get occupancy by month")
    public ResponseEntity<Map<String, Double>> getOccupancyByMonth(@RequestParam int year) {
        Map<String, Double> occupancy = analyticsService.getOccupancyByMonth(year);
        return ResponseEntity.ok(occupancy);
    }

    @GetMapping("/reservations/total")
    @Operation(summary = "Get total reservations")
    public ResponseEntity<Long> getTotalReservations(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Long total = analyticsService.getTotalReservations(startDate, endDate);
        return ResponseEntity.ok(total);
    }

    @GetMapping("/reservations/by-status")
    @Operation(summary = "Get reservations by status")
    public ResponseEntity<Map<String, Long>> getReservationsByStatus(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Map<String, Long> reservations = analyticsService.getReservationsByStatus(startDate, endDate);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/reservations/average-stay")
    @Operation(summary = "Get average length of stay")
    public ResponseEntity<Double> getAverageLengthOfStay(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Double average = analyticsService.getAverageLengthOfStay(startDate, endDate);
        return ResponseEntity.ok(average);
    }

    @GetMapping("/reservations/cancellation-rate")
    @Operation(summary = "Get cancellation rate")
    public ResponseEntity<Double> getCancellationRate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Double rate = analyticsService.getCancellationRate(startDate, endDate);
        return ResponseEntity.ok(rate);
    }

    @GetMapping("/guests/total")
    @Operation(summary = "Get total guests")
    public ResponseEntity<Long> getTotalGuests() {
        Long total = analyticsService.getTotalGuests();
        return ResponseEntity.ok(total);
    }

    @GetMapping("/guests/by-loyalty")
    @Operation(summary = "Get guests by loyalty tier")
    public ResponseEntity<Map<String, Long>> getGuestsByLoyaltyTier() {
        Map<String, Long> guests = analyticsService.getGuestsByLoyaltyTier();
        return ResponseEntity.ok(guests);
    }

    @GetMapping("/reviews/average-rating")
    @Operation(summary = "Get average rating")
    public ResponseEntity<Double> getAverageRating() {
        Double rating = analyticsService.getAverageRating();
        return ResponseEntity.ok(rating);
    }

    @GetMapping("/financial/revpar")
    @Operation(summary = "Get Revenue Per Available Room (RevPAR)")
    public ResponseEntity<BigDecimal> getRevPAR(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        BigDecimal revpar = analyticsService.getAverageRevenuePerAvailableRoom(startDate, endDate);
        return ResponseEntity.ok(revpar);
    }

    @GetMapping("/financial/adr")
    @Operation(summary = "Get Average Daily Rate (ADR)")
    public ResponseEntity<BigDecimal> getADR(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        BigDecimal adr = analyticsService.getAverageRevenuePerOccupiedRoom(startDate, endDate);
        return ResponseEntity.ok(adr);
    }
}
