package com.hotel.management.service.impl;

import com.hotel.management.dto.request.GroupReservationRequest;
import com.hotel.management.entity.*;
import com.hotel.management.entity.enums.ReservationStatus;
import com.hotel.management.entity.enums.RoomStatus;
import com.hotel.management.exception.BadRequestException;
import com.hotel.management.exception.ResourceNotFoundException;
import com.hotel.management.repository.*;
import com.hotel.management.service.GroupReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupReservationServiceImpl implements GroupReservationService {

    private final GroupReservationRepository groupReservationRepository;
    private final ReservationRepository reservationRepository;
    private final GuestRepository guestRepository;
    private final RoomRepository roomRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final PromotionRepository promotionRepository;

    @Override
    @Transactional
    public Map<String, Object> createGroupReservation(GroupReservationRequest request, User user) {
        // Validate dates
        if (request.getCheckInDate().isBefore(java.time.LocalDate.now())) {
            throw new BadRequestException("Check-in date cannot be in the past");
        }
        if (request.getCheckOutDate().isBefore(request.getCheckInDate())) {
            throw new BadRequestException("Check-out date must be after check-in date");
        }

        // Create group reservation
        GroupReservation groupReservation = GroupReservation.builder()
                .groupNumber("GRP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .groupName(request.getGroupName())
                .contactPersonName(request.getContactPersonName())
                .contactEmail(request.getContactEmail())
                .contactPhone(request.getContactPhone())
                .status(ReservationStatus.PENDING)
                .totalRooms(request.getRooms().size())
                .totalAmount(BigDecimal.ZERO)
                .paidAmount(BigDecimal.ZERO)
                .discountAmount(BigDecimal.ZERO)
                .specialRequests(request.getSpecialRequests())
                .promoCode(request.getPromoCode())
                .build();

        groupReservation = groupReservationRepository.save(groupReservation);

        // Calculate total amount and create individual reservations
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;
        List<Reservation> reservations = new ArrayList<>();

        long numberOfNights = ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate());

        for (GroupReservationRequest.GroupRoomRequest roomRequest : request.getRooms()) {
            // Find available room of requested type
            RoomType roomType = roomTypeRepository.findById(roomRequest.getRoomTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("RoomType", "id", roomRequest.getRoomTypeId()));

            List<Room> availableRooms = roomRepository.findAvailableRooms(
                    request.getCheckInDate(),
                    request.getCheckOutDate()
            ).stream()
             .filter(r -> r.getRoomType().getId().equals(roomRequest.getRoomTypeId()))
             .limit(1)
             .collect(Collectors.toList());

            if (availableRooms.isEmpty()) {
                throw new BadRequestException("No available rooms of type: " + roomType.getName());
            }

            Room room = availableRooms.get(0);

            // Get or create guest for the room
            Guest guest = guestRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Guest profile not found"));

            // Calculate room amount
            BigDecimal roomAmount = room.getBasePrice().multiply(BigDecimal.valueOf(numberOfNights));
            BigDecimal taxAmount = roomAmount.multiply(BigDecimal.valueOf(0.10));
            roomAmount = roomAmount.add(taxAmount);

            // Create reservation
            Reservation reservation = Reservation.builder()
                    .reservationNumber("RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                    .guest(guest)
                    .room(room)
                    .checkInDate(request.getCheckInDate())
                    .checkOutDate(request.getCheckOutDate())
                    .numberOfGuests(roomRequest.getNumberOfGuests())
                    .numberOfAdults(roomRequest.getNumberOfAdults())
                    .numberOfChildren(roomRequest.getNumberOfChildren())
                    .status(ReservationStatus.PENDING)
                    .totalAmount(roomAmount)
                    .paidAmount(BigDecimal.ZERO)
                    .discountAmount(BigDecimal.ZERO)
                    .taxAmount(taxAmount)
                    .specialRequests(request.getSpecialRequests())
                    .promoCode(request.getPromoCode())
                    .source("GROUP")
                    .groupReservation(groupReservation)
                    .build();

            reservation = reservationRepository.save(reservation);
            reservations.add(reservation);

            // Update room status
            room.setStatus(RoomStatus.RESERVED);
            roomRepository.save(room);

            totalAmount = totalAmount.add(roomAmount);
        }

        // Apply group discount if promo code provided
        if (request.getPromoCode() != null && !request.getPromoCode().isEmpty()) {
            Promotion promotion = promotionRepository
                    .findValidPromotionByCode(request.getPromoCode(), java.time.LocalDate.now())
                    .orElse(null);

            if (promotion != null) {
                totalDiscount = calculateGroupDiscount(totalAmount, promotion);
                totalAmount = totalAmount.subtract(totalDiscount);
            }
        }

        // Update group reservation totals
        groupReservation.setTotalAmount(totalAmount);
        groupReservation.setDiscountAmount(totalDiscount);
        groupReservationRepository.save(groupReservation);

        // Prepare response
        Map<String, Object> response = new HashMap<>();
        response.put("groupReservation", mapGroupToResponse(groupReservation));
        response.put("reservations", reservations.stream()
                .map(this::mapReservationToMap)
                .collect(Collectors.toList()));
        response.put("totalRooms", reservations.size());
        response.put("totalAmount", totalAmount);
        response.put("message", "Group reservation created successfully");

        return response;
    }

    @Override
    public Map<String, Object> getGroupReservation(Long id) {
        GroupReservation groupReservation = groupReservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("GroupReservation", "id", id));

        Map<String, Object> response = new HashMap<>();
        response.put("groupReservation", mapGroupToResponse(groupReservation));
        response.put("reservations", groupReservation.getReservations().stream()
                .map(this::mapReservationToMap)
                .collect(Collectors.toList()));

        return response;
    }

    @Override
    public List<Map<String, Object>> getAllGroupReservations() {
        return groupReservationRepository.findAll().stream()
                .map(this::mapGroupToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Map<String, Object> cancelGroupReservation(Long id, User user) {
        GroupReservation groupReservation = groupReservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("GroupReservation", "id", id));

        if (groupReservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new BadRequestException("Group reservation is already cancelled");
        }

        // Cancel all individual reservations
        for (Reservation reservation : groupReservation.getReservations()) {
            if (reservation.getStatus() != ReservationStatus.CANCELLED) {
                reservation.setStatus(ReservationStatus.CANCELLED);
                reservation.setCancelledAt(LocalDateTime.now());
                reservation.setCancelledBy(user.getId());

                // Update room status
                Room room = reservation.getRoom();
                room.setStatus(RoomStatus.AVAILABLE);
                roomRepository.save(room);

                reservationRepository.save(reservation);
            }
        }

        // Update group reservation status
        groupReservation.setStatus(ReservationStatus.CANCELLED);
        groupReservationRepository.save(groupReservation);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Group reservation cancelled successfully");
        response.put("groupReservation", mapGroupToResponse(groupReservation));

        return response;
    }

    private BigDecimal calculateGroupDiscount(BigDecimal totalAmount, Promotion promotion) {
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

    private Map<String, Object> mapGroupToResponse(GroupReservation group) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", group.getId());
        map.put("groupNumber", group.getGroupNumber());
        map.put("groupName", group.getGroupName());
        map.put("contactPersonName", group.getContactPersonName());
        map.put("contactEmail", group.getContactEmail());
        map.put("contactPhone", group.getContactPhone());
        map.put("status", group.getStatus());
        map.put("totalRooms", group.getTotalRooms());
        map.put("totalAmount", group.getTotalAmount());
        map.put("paidAmount", group.getPaidAmount());
        map.put("discountAmount", group.getDiscountAmount());
        map.put("specialRequests", group.getSpecialRequests());
        map.put("promoCode", group.getPromoCode());
        map.put("createdAt", group.getCreatedAt());
        return map;
    }

    private Map<String, Object> mapReservationToMap(Reservation reservation) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", reservation.getId());
        map.put("reservationNumber", reservation.getReservationNumber());
        map.put("roomNumber", reservation.getRoom().getRoomNumber());
        map.put("roomType", reservation.getRoom().getRoomType().getName());
        map.put("checkInDate", reservation.getCheckInDate());
        map.put("checkOutDate", reservation.getCheckOutDate());
        map.put("numberOfGuests", reservation.getNumberOfGuests());
        map.put("status", reservation.getStatus());
        map.put("totalAmount", reservation.getTotalAmount());
        return map;
    }
}
