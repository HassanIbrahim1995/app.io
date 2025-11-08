package com.hotel.management.service.impl;

import com.hotel.management.dto.request.ReservationRequest;
import com.hotel.management.dto.response.ReservationResponse;
import com.hotel.management.entity.*;
import com.hotel.management.entity.enums.ReservationStatus;
import com.hotel.management.entity.enums.RoomStatus;
import com.hotel.management.exception.BadRequestException;
import com.hotel.management.exception.ResourceNotFoundException;
import com.hotel.management.repository.GuestRepository;
import com.hotel.management.repository.PromotionRepository;
import com.hotel.management.repository.ReservationRepository;
import com.hotel.management.repository.RoomRepository;
import com.hotel.management.service.ReservationService;
import com.hotel.management.util.DateValidationUtil;
import com.hotel.management.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final GuestRepository guestRepository;
    private final RoomRepository roomRepository;
    private final PromotionRepository promotionRepository;

    @Override
    @Transactional
    public ReservationResponse createReservation(ReservationRequest request, User user) {
        // Comprehensive date validation
        DateValidationUtil.validateCheckInNotInPast(request.getCheckInDate());
        DateValidationUtil.validateCheckInCheckOutDates(request.getCheckInDate(), request.getCheckOutDate());
        DateValidationUtil.validateMinimumStay(request.getCheckInDate(), request.getCheckOutDate(), 1);
        DateValidationUtil.validateMaximumStay(request.getCheckInDate(), request.getCheckOutDate(), 365);
        DateValidationUtil.validateAdvancedBooking(request.getCheckInDate(), 730); // 2 years max

        // Get guest
        Guest guest = guestRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Guest profile not found"));

        // Get room
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room", "id", request.getRoomId()));

        // Validate guest count
        ValidationUtil.validateGuestCount(request.getNumberOfGuests(), room.getMaxOccupancy());

        // Check room availability with date overlap validation
        if (!isRoomAvailableWithOverlapCheck(room.getId(), request.getCheckInDate(), request.getCheckOutDate(), null)) {
            throw new BadRequestException("Room is not available for the selected dates - overlaps with existing reservation");
        }

        // Calculate total amount
        long numberOfNights = ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate());
        BigDecimal totalAmount = room.getBasePrice().multiply(BigDecimal.valueOf(numberOfNights));

        // Apply promotion if provided
        BigDecimal discountAmount = BigDecimal.ZERO;
        if (request.getPromoCode() != null && !request.getPromoCode().isEmpty()) {
            Promotion promotion = promotionRepository
                    .findValidPromotionByCode(request.getPromoCode(), LocalDate.now())
                    .orElseThrow(() -> new BadRequestException("Invalid or expired promotion code"));

            discountAmount = calculateDiscount(totalAmount, promotion);
            totalAmount = totalAmount.subtract(discountAmount);
        }

        // Calculate tax (10%)
        BigDecimal taxAmount = totalAmount.multiply(BigDecimal.valueOf(0.10));
        totalAmount = totalAmount.add(taxAmount);

        // Create reservation
        Reservation reservation = Reservation.builder()
                .reservationNumber("RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .guest(guest)
                .room(room)
                .checkInDate(request.getCheckInDate())
                .checkOutDate(request.getCheckOutDate())
                .numberOfGuests(request.getNumberOfGuests())
                .numberOfAdults(request.getNumberOfAdults())
                .numberOfChildren(request.getNumberOfChildren())
                .status(ReservationStatus.PENDING)
                .totalAmount(totalAmount)
                .paidAmount(BigDecimal.ZERO)
                .discountAmount(discountAmount)
                .taxAmount(taxAmount)
                .specialRequests(request.getSpecialRequests())
                .promoCode(request.getPromoCode())
                .source("DIRECT")
                .build();

        reservation = reservationRepository.save(reservation);

        // Update room status
        room.setStatus(RoomStatus.RESERVED);
        roomRepository.save(room);

        return mapToReservationResponse(reservation);
    }

    @Override
    public ReservationResponse getReservationById(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", "id", id));
        return mapToReservationResponse(reservation);
    }

    @Override
    public List<ReservationResponse> getMyReservations(User user) {
        Guest guest = guestRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Guest profile not found"));

        return reservationRepository.findByGuestId(guest.getId()).stream()
                .map(this::mapToReservationResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ReservationResponse cancelReservation(Long id, User user) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", "id", id));

        if (!reservation.getGuest().getUser().getId().equals(user.getId())) {
            throw new BadRequestException("You can only cancel your own reservations");
        }

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new BadRequestException("Reservation is already cancelled");
        }

        // Calculate refund based on cancellation policy
        BigDecimal refundAmount = calculateRefundAmount(reservation);
        
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.setCancelledAt(LocalDateTime.now());
        reservation.setCancelledBy(user.getId());
        reservation.setRefundAmount(refundAmount);
        reservation.setRefundProcessed(false);
        
        // Set cancellation reason based on timing
        long daysUntilCheckIn = ChronoUnit.DAYS.between(LocalDateTime.now().toLocalDate(), reservation.getCheckInDate());
        String cancellationReason = daysUntilCheckIn >= 7 ? 
            "Cancelled with full refund (7+ days notice)" : 
            daysUntilCheckIn >= 3 ? 
                "Cancelled with 50% refund (3-7 days notice)" : 
                "Cancelled with no refund (less than 3 days notice)";
        reservation.setCancellationReason(cancellationReason);

        // Update room status
        Room room = reservation.getRoom();
        room.setStatus(RoomStatus.AVAILABLE);
        roomRepository.save(room);

        reservationRepository.save(reservation);
        return mapToReservationResponse(reservation);
    }
    
    private BigDecimal calculateRefundAmount(Reservation reservation) {
        // Calculate days until check-in
        long daysUntilCheckIn = ChronoUnit.DAYS.between(
            LocalDateTime.now().toLocalDate(), 
            reservation.getCheckInDate()
        );
        
        BigDecimal paidAmount = reservation.getPaidAmount();
        
        // Cancellation policy:
        // 7+ days: Full refund (100%)
        // 3-7 days: 50% refund
        // < 3 days: No refund
        if (daysUntilCheckIn >= 7) {
            return paidAmount;
        } else if (daysUntilCheckIn >= 3) {
            return paidAmount.multiply(BigDecimal.valueOf(0.5));
        } else {
            return BigDecimal.ZERO;
        }
    }

    @Override
    @Transactional
    public ReservationResponse checkIn(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", "id", id));

        if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
            throw new BadRequestException("Only confirmed reservations can be checked in");
        }

        reservation.setStatus(ReservationStatus.CHECKED_IN);
        reservation.setActualCheckIn(LocalDateTime.now());

        Room room = reservation.getRoom();
        room.setStatus(RoomStatus.OCCUPIED);
        roomRepository.save(room);

        reservationRepository.save(reservation);
        return mapToReservationResponse(reservation);
    }

    @Override
    @Transactional
    public ReservationResponse checkOut(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", "id", id));

        if (reservation.getStatus() != ReservationStatus.CHECKED_IN) {
            throw new BadRequestException("Only checked-in reservations can be checked out");
        }

        reservation.setStatus(ReservationStatus.CHECKED_OUT);
        reservation.setActualCheckOut(LocalDateTime.now());

        Room room = reservation.getRoom();
        room.setStatus(RoomStatus.CLEANING);
        roomRepository.save(room);

        reservationRepository.save(reservation);
        return mapToReservationResponse(reservation);
    }

    private boolean isRoomAvailable(Long roomId, LocalDate checkIn, LocalDate checkOut) {
        List<Reservation> conflictingReservations = reservationRepository.findByDateRange(checkIn, checkOut);
        return conflictingReservations.stream()
                .noneMatch(r -> r.getRoom().getId().equals(roomId) &&
                        r.getStatus() != ReservationStatus.CANCELLED);
    }

    /**
     * Enhanced room availability check with explicit date overlap validation
     * @param roomId The room to check
     * @param checkIn Check-in date
     * @param checkOut Check-out date
     * @param excludeReservationId Reservation ID to exclude from check (for updates)
     * @return true if room is available, false otherwise
     */
    private boolean isRoomAvailableWithOverlapCheck(Long roomId, LocalDate checkIn, LocalDate checkOut, Long excludeReservationId) {
        // Get all active reservations for this room
        List<Reservation> conflictingReservations = reservationRepository.findByDateRange(
                checkIn.minusDays(365), // Look back to catch any long-term reservations
                checkOut.plusDays(365)  // Look forward
        );

        // Check for date overlaps
        for (Reservation reservation : conflictingReservations) {
            // Skip if this is the reservation being updated
            if (excludeReservationId != null && reservation.getId().equals(excludeReservationId)) {
                continue;
            }

            // Only check for this specific room
            if (!reservation.getRoom().getId().equals(roomId)) {
                continue;
            }

            // Skip cancelled reservations
            if (reservation.getStatus() == ReservationStatus.CANCELLED) {
                continue;
            }

            // Use date validation utility to check for overlaps
            if (DateValidationUtil.doDateRangesOverlap(
                    checkIn, checkOut,
                    reservation.getCheckInDate(), reservation.getCheckOutDate())) {
                return false; // Overlap detected
            }
        }

        return true; // No overlaps found
    }

    private BigDecimal calculateDiscount(BigDecimal totalAmount, Promotion promotion) {
        BigDecimal discount;
        switch (promotion.getDiscountType()) {
            case PERCENTAGE:
                discount = totalAmount.multiply(promotion.getDiscountValue().divide(BigDecimal.valueOf(100)));
                break;
            case FIXED_AMOUNT:
                discount = promotion.getDiscountValue();
                break;
            default:
                discount = BigDecimal.ZERO;
        }

        if (promotion.getMaxDiscountAmount() != null && discount.compareTo(promotion.getMaxDiscountAmount()) > 0) {
            discount = promotion.getMaxDiscountAmount();
        }

        return discount;
    }

    private ReservationResponse mapToReservationResponse(Reservation reservation) {
        return ReservationResponse.builder()
                .id(reservation.getId())
                .reservationNumber(reservation.getReservationNumber())
                .guestId(reservation.getGuest().getId())
                .guestName(reservation.getGuest().getUser().getFullName())
                .roomId(reservation.getRoom().getId())
                .roomNumber(reservation.getRoom().getRoomNumber())
                .checkInDate(reservation.getCheckInDate())
                .checkOutDate(reservation.getCheckOutDate())
                .actualCheckIn(reservation.getActualCheckIn())
                .actualCheckOut(reservation.getActualCheckOut())
                .numberOfGuests(reservation.getNumberOfGuests())
                .numberOfAdults(reservation.getNumberOfAdults())
                .numberOfChildren(reservation.getNumberOfChildren())
                .status(reservation.getStatus())
                .totalAmount(reservation.getTotalAmount())
                .paidAmount(reservation.getPaidAmount())
                .discountAmount(reservation.getDiscountAmount())
                .taxAmount(reservation.getTaxAmount())
                .specialRequests(reservation.getSpecialRequests())
                .promoCode(reservation.getPromoCode())
                .source(reservation.getSource())
                .createdAt(reservation.getCreatedAt())
                .build();
    }
}
