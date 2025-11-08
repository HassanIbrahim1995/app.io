package com.hotel.management.util;

import com.hotel.management.exception.BadRequestException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Utility class for date validation and overlap detection
 */
public class DateValidationUtil {

    /**
     * Validate that check-in date is before check-out date
     */
    public static void validateCheckInCheckOutDates(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null) {
            throw new BadRequestException("Check-in and check-out dates are required");
        }

        if (checkOut.isBefore(checkIn) || checkOut.isEqual(checkIn)) {
            throw new BadRequestException("Check-out date must be after check-in date");
        }
    }

    /**
     * Validate that check-in date is not in the past
     */
    public static void validateCheckInNotInPast(LocalDate checkIn) {
        if (checkIn == null) {
            throw new BadRequestException("Check-in date is required");
        }

        LocalDate today = LocalDate.now();
        if (checkIn.isBefore(today)) {
            throw new BadRequestException("Check-in date cannot be in the past");
        }
    }

    /**
     * Validate minimum stay duration
     */
    public static void validateMinimumStay(LocalDate checkIn, LocalDate checkOut, int minNights) {
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        if (nights < minNights) {
            throw new BadRequestException("Minimum stay is " + minNights + " night(s)");
        }
    }

    /**
     * Validate maximum stay duration
     */
    public static void validateMaximumStay(LocalDate checkIn, LocalDate checkOut, int maxNights) {
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        if (nights > maxNights) {
            throw new BadRequestException("Maximum stay is " + maxNights + " night(s)");
        }
    }

    /**
     * Validate advanced booking window (e.g., cannot book more than 365 days in advance)
     */
    public static void validateAdvancedBooking(LocalDate checkIn, int maxDaysInAdvance) {
        LocalDate today = LocalDate.now();
        long daysInAdvance = ChronoUnit.DAYS.between(today, checkIn);
        
        if (daysInAdvance > maxDaysInAdvance) {
            throw new BadRequestException("Cannot book more than " + maxDaysInAdvance + " days in advance");
        }
    }

    /**
     * Check if two date ranges overlap
     * Returns true if the ranges overlap, false otherwise
     */
    public static boolean doDateRangesOverlap(
            LocalDate start1, LocalDate end1,
            LocalDate start2, LocalDate end2) {
        
        if (start1 == null || end1 == null || start2 == null || end2 == null) {
            return false;
        }

        // Ranges overlap if:
        // (start1 < end2) AND (end1 > start2)
        return start1.isBefore(end2) && end1.isAfter(start2);
    }

    /**
     * Check if a date is within a date range (inclusive)
     */
    public static boolean isDateInRange(LocalDate date, LocalDate start, LocalDate end) {
        if (date == null || start == null || end == null) {
            return false;
        }
        return !date.isBefore(start) && !date.isAfter(end);
    }

    /**
     * Calculate number of nights between check-in and check-out
     */
    public static long calculateNights(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(checkIn, checkOut);
    }

    /**
     * Validate cancellation timing
     */
    public static void validateCancellationTiming(LocalDate checkIn, int minimumNoticeDays) {
        LocalDate today = LocalDate.now();
        long daysUntilCheckIn = ChronoUnit.DAYS.between(today, checkIn);
        
        if (daysUntilCheckIn < minimumNoticeDays) {
            throw new BadRequestException(
                "Cancellation requires at least " + minimumNoticeDays + " days notice"
            );
        }
    }

    /**
     * Check if booking is within blackout dates
     */
    public static boolean isWithinBlackoutDates(
            LocalDate checkIn, LocalDate checkOut,
            LocalDate blackoutStart, LocalDate blackoutEnd) {
        
        return doDateRangesOverlap(checkIn, checkOut, blackoutStart, blackoutEnd);
    }
}
