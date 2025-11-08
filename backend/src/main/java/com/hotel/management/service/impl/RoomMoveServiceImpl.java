package com.hotel.management.service.impl;

import com.hotel.management.dto.request.RoomMoveRequest;
import com.hotel.management.dto.response.ReservationResponse;
import com.hotel.management.entity.*;
import com.hotel.management.entity.enums.ReservationStatus;
import com.hotel.management.entity.enums.RoomStatus;
import com.hotel.management.exception.BadRequestException;
import com.hotel.management.exception.ResourceNotFoundException;
import com.hotel.management.repository.ReservationRepository;
import com.hotel.management.repository.RoomMoveHistoryRepository;
import com.hotel.management.repository.RoomRepository;
import com.hotel.management.service.RoomMoveService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomMoveServiceImpl implements RoomMoveService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final RoomMoveHistoryRepository moveHistoryRepository;

    @Override
    @Transactional
    public ReservationResponse moveRoom(RoomMoveRequest request, User user) {
        // Get reservation
        Reservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", "id", request.getReservationId()));

        // Validate reservation status
        if (reservation.getStatus() != ReservationStatus.CONFIRMED && 
            reservation.getStatus() != ReservationStatus.CHECKED_IN) {
            throw new BadRequestException("Only confirmed or checked-in reservations can be moved");
        }

        // Get old and new rooms
        Room oldRoom = reservation.getRoom();
        Room newRoom = roomRepository.findById(request.getNewRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room", "id", request.getNewRoomId()));

        // Validate new room availability
        if (newRoom.getStatus() != RoomStatus.AVAILABLE && newRoom.getStatus() != RoomStatus.RESERVED) {
            throw new BadRequestException("New room is not available");
        }

        // Check if rooms are the same
        if (oldRoom.getId().equals(newRoom.getId())) {
            throw new BadRequestException("Cannot move to the same room");
        }

        // Calculate price difference if not waiving charges
        if (!request.getWaiveCharges()) {
            BigDecimal priceDifference = newRoom.getBasePrice().subtract(oldRoom.getBasePrice());
            if (priceDifference.compareTo(BigDecimal.ZERO) > 0) {
                long remainingNights = ChronoUnit.DAYS.between(
                    LocalDateTime.now().toLocalDate(),
                    reservation.getCheckOutDate()
                );
                BigDecimal additionalCharge = priceDifference.multiply(BigDecimal.valueOf(remainingNights));
                reservation.setTotalAmount(reservation.getTotalAmount().add(additionalCharge));
            }
        }

        // Record move history
        RoomMoveHistory moveHistory = RoomMoveHistory.builder()
                .reservation(reservation)
                .fromRoom(oldRoom)
                .toRoom(newRoom)
                .movedAt(LocalDateTime.now())
                .movedBy(user.getId())
                .reason(request.getReason())
                .chargesWaived(request.getWaiveCharges())
                .build();
        moveHistoryRepository.save(moveHistory);

        // Update room statuses
        oldRoom.setStatus(RoomStatus.AVAILABLE);
        newRoom.setStatus(reservation.getStatus() == ReservationStatus.CHECKED_IN ? 
                          RoomStatus.OCCUPIED : RoomStatus.RESERVED);
        roomRepository.save(oldRoom);
        roomRepository.save(newRoom);

        // Update reservation
        reservation.setRoom(newRoom);
        String note = String.format("Room moved from %s to %s. Reason: %s", 
                                   oldRoom.getRoomNumber(), 
                                   newRoom.getRoomNumber(), 
                                   request.getReason());
        reservation.setNotes(reservation.getNotes() != null ? 
                            reservation.getNotes() + "\n" + note : note);
        reservationRepository.save(reservation);

        return mapToResponse(reservation);
    }

    @Override
    public List<Object> getRoomMoveHistory(Long reservationId) {
        List<RoomMoveHistory> history = moveHistoryRepository.findByReservationId(reservationId);
        
        return history.stream()
                .map(move -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", move.getId());
                    map.put("fromRoom", move.getFromRoom().getRoomNumber());
                    map.put("toRoom", move.getToRoom().getRoomNumber());
                    map.put("movedAt", move.getMovedAt());
                    map.put("movedBy", move.getMovedBy());
                    map.put("reason", move.getReason());
                    map.put("chargesWaived", move.getChargesWaived());
                    return map;
                })
                .collect(Collectors.toList());
    }

    private ReservationResponse mapToResponse(Reservation reservation) {
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
