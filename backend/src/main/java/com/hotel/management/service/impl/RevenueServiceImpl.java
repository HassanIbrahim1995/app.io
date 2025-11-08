package com.hotel.management.service.impl;

import com.hotel.management.dto.response.RevenueResponse;
import com.hotel.management.entity.*;
import com.hotel.management.entity.enums.RevenueStatus;
import com.hotel.management.entity.enums.RevenueType;
import com.hotel.management.exception.ResourceNotFoundException;
import com.hotel.management.repository.*;
import com.hotel.management.service.RevenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RevenueServiceImpl implements RevenueService {

    private final RevenueRepository revenueRepository;
    private final ReservationRepository reservationRepository;
    private final GuestRepository guestRepository;
    private final RoomRepository roomRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public Revenue createRevenue(Revenue revenue) {
        if (revenue.getRevenueNumber() == null) {
            revenue.setRevenueNumber(generateRevenueNumber());
        }
        
        // Calculate net amount if not provided
        if (revenue.getNetAmount() == null) {
            revenue.setNetAmount(revenue.getAmount().subtract(revenue.getTaxAmount()));
        }
        
        return revenueRepository.save(revenue);
    }

    @Override
    public RevenueResponse getRevenueById(Long id) {
        Revenue revenue = revenueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Revenue", "id", id));
        return mapToResponse(revenue);
    }

    @Override
    public Page<RevenueResponse> getAllRevenues(Pageable pageable) {
        return revenueRepository.findAll(pageable).map(this::mapToResponse);
    }

    @Override
    public Page<RevenueResponse> getRevenuesByType(RevenueType type, Pageable pageable) {
        return revenueRepository.findByRevenueType(type, pageable).map(this::mapToResponse);
    }

    @Override
    public Page<RevenueResponse> getRevenuesByStatus(RevenueStatus status, Pageable pageable) {
        return revenueRepository.findByStatus(status, pageable).map(this::mapToResponse);
    }

    @Override
    public Page<RevenueResponse> getRevenuesByDepartment(String department, Pageable pageable) {
        return revenueRepository.findByDepartment(department, pageable).map(this::mapToResponse);
    }

    @Override
    public List<RevenueResponse> getRevenuesByReservation(Long reservationId) {
        return revenueRepository.findByReservationId(reservationId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RevenueResponse> getRevenuesByGuest(Long guestId) {
        return revenueRepository.findByGuestId(guestId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RevenueResponse> getRevenuesByDateRange(LocalDate startDate, LocalDate endDate) {
        return revenueRepository.findByDateRange(startDate, endDate).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateRevenueStatus(Long id, RevenueStatus status) {
        Revenue revenue = revenueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Revenue", "id", id));
        revenue.setStatus(status);
        revenueRepository.save(revenue);
    }

    @Override
    @Transactional
    public void postRevenueToGL(Long id) {
        Revenue revenue = revenueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Revenue", "id", id));
        revenue.setPostedToGl(true);
        revenue.setPostingDate(LocalDate.now());
        revenueRepository.save(revenue);
    }

    @Override
    public BigDecimal getTotalRevenue(LocalDate startDate, LocalDate endDate) {
        BigDecimal total = revenueRepository.sumCollectedRevenueBetweenDates(startDate, endDate);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getNetRevenue(LocalDate startDate, LocalDate endDate) {
        BigDecimal net = revenueRepository.sumNetRevenueBetweenDates(startDate, endDate);
        return net != null ? net : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getRevenueByType(RevenueType type, LocalDate startDate, LocalDate endDate) {
        BigDecimal revenue = revenueRepository.sumRevenueByTypeBetweenDates(type, startDate, endDate);
        return revenue != null ? revenue : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getRevenueByDepartment(String department, LocalDate startDate, LocalDate endDate) {
        BigDecimal revenue = revenueRepository.sumRevenueByDepartmentBetweenDates(department, startDate, endDate);
        return revenue != null ? revenue : BigDecimal.ZERO;
    }

    @Override
    public Map<RevenueType, BigDecimal> getRevenueBreakdownByType(LocalDate startDate, LocalDate endDate) {
        List<Object[]> results = revenueRepository.sumRevenueByTypeGrouped(startDate, endDate);
        Map<RevenueType, BigDecimal> breakdown = new HashMap<>();
        
        for (Object[] result : results) {
            RevenueType type = (RevenueType) result[0];
            BigDecimal amount = (BigDecimal) result[1];
            breakdown.put(type, amount);
        }
        
        return breakdown;
    }

    @Override
    public Map<String, BigDecimal> getRevenueBreakdownByDepartment(LocalDate startDate, LocalDate endDate) {
        List<Object[]> results = revenueRepository.sumRevenueByDepartmentGrouped(startDate, endDate);
        Map<String, BigDecimal> breakdown = new HashMap<>();
        
        for (Object[]result : results) {
            String department = (String) result[0];
            BigDecimal amount = (BigDecimal) result[1];
            breakdown.put(department != null ? department : "UNCATEGORIZED", amount);
        }
        
        return breakdown;
    }

    @Override
    public Map<LocalDate, BigDecimal> getDailyRevenue(LocalDate startDate, LocalDate endDate) {
        List<Object[]> results = revenueRepository.sumRevenueDailyGrouped(startDate, endDate);
        Map<LocalDate, BigDecimal> daily = new LinkedHashMap<>();
        
        for (Object[] result : results) {
            LocalDate date = (LocalDate) result[0];
            BigDecimal amount = (BigDecimal) result[1];
            daily.put(date, amount);
        }
        
        return daily;
    }

    @Override
    public Map<String, BigDecimal> getMonthlyRevenue(LocalDate startDate, LocalDate endDate) {
        List<Object[]> results = revenueRepository.sumRevenueMonthlyGrouped(startDate, endDate);
        Map<String, BigDecimal> monthly = new LinkedHashMap<>();
        
        for (Object[] result : results) {
            Integer year = (Integer) result[0];
            Integer month = (Integer) result[1];
            BigDecimal amount = (BigDecimal) result[2];
            String key = year + "-" + String.format("%02d", month);
            monthly.put(key, amount);
        }
        
        return monthly;
    }

    @Override
    public BigDecimal getPendingRevenue() {
        BigDecimal pending = revenueRepository.sumPendingRevenue();
        return pending != null ? pending : BigDecimal.ZERO;
    }

    @Override
    public List<RevenueResponse> getOverduePendingRevenue(LocalDate asOfDate) {
        return revenueRepository.findOverduePendingRevenue(asOfDate).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void recordRoomRevenue(Long reservationId, Long roomId, Long guestId, BigDecimal amount, LocalDate date) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", "id", reservationId));
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room", "id", roomId));
        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new ResourceNotFoundException("Guest", "id", guestId));

        Revenue revenue = Revenue.builder()
                .revenueNumber(generateRevenueNumber())
                .revenueType(RevenueType.ROOM_BOOKING)
                .status(RevenueStatus.COLLECTED)
                .amount(amount)
                .netAmount(amount)
                .revenueDate(date)
                .reservation(reservation)
                .guest(guest)
                .room(room)
                .department("ROOMS")
                .description("Room booking revenue for " + room.getRoomNumber())
                .recognizedDate(date)
                .build();

        revenueRepository.save(revenue);
    }

    @Override
    @Transactional
    public void recordServiceRevenue(RevenueType serviceType, Long guestId, Long roomId, BigDecimal amount, String description) {
        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new ResourceNotFoundException("Guest", "id", guestId));
        Room room = roomId != null ? roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room", "id", roomId)) : null;

        String department = determineDepartment(serviceType);

        Revenue revenue = Revenue.builder()
                .revenueNumber(generateRevenueNumber())
                .revenueType(serviceType)
                .status(RevenueStatus.COLLECTED)
                .amount(amount)
                .netAmount(amount)
                .revenueDate(LocalDate.now())
                .guest(guest)
                .room(room)
                .department(department)
                .description(description)
                .recognizedDate(LocalDate.now())
                .build();

        revenueRepository.save(revenue);
    }

    @Override
    @Transactional
    public void recordProductRevenue(Long productId, Long guestId, BigDecimal amount, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new ResourceNotFoundException("Guest", "id", guestId));

        Revenue revenue = Revenue.builder()
                .revenueNumber(generateRevenueNumber())
                .revenueType(RevenueType.valueOf(product.getCategory().name()))
                .status(RevenueStatus.COLLECTED)
                .amount(amount)
                .netAmount(amount)
                .revenueDate(LocalDate.now())
                .guest(guest)
                .product(product)
                .department("PRODUCTS")
                .description("Product sale: " + product.getName())
                .quantity(BigDecimal.valueOf(quantity))
                .unitPrice(product.getPrice())
                .recognizedDate(LocalDate.now())
                .build();

        revenueRepository.save(revenue);
    }

    @Override
    @Transactional
    public void recordRefund(Long originalRevenueId, BigDecimal refundAmount, String reason) {
        Revenue originalRevenue = revenueRepository.findById(originalRevenueId)
                .orElseThrow(() -> new ResourceNotFoundException("Revenue", "id", originalRevenueId));

        Revenue refund = Revenue.builder()
                .revenueNumber(generateRevenueNumber())
                .revenueType(RevenueType.REFUND)
                .status(RevenueStatus.COLLECTED)
                .amount(refundAmount.negate()) // Negative amount for refund
                .netAmount(refundAmount.negate())
                .revenueDate(LocalDate.now())
                .reservation(originalRevenue.getReservation())
                .guest(originalRevenue.getGuest())
                .room(originalRevenue.getRoom())
                .department(originalRevenue.getDepartment())
                .description("Refund: " + reason)
                .sourceReference("REF-" + originalRevenue.getRevenueNumber())
                .recognizedDate(LocalDate.now())
                .build();

        revenueRepository.save(refund);
    }

    private String generateRevenueNumber() {
        return "REV-" + System.currentTimeMillis();
    }

    private String determineDepartment(RevenueType type) {
        return switch (type) {
            case ROOM_BOOKING, ROOM_UPGRADE, EARLY_CHECKIN, LATE_CHECKOUT -> "ROOMS";
            case MINIBAR, ROOM_SERVICE, RESTAURANT, BAR -> "F&B";
            case LAUNDRY, SPA -> "SPA";
            case CONFERENCE_ROOM, BANQUET, CATERING, EVENT_SERVICES -> "EVENTS";
            case PARKING, WIFI, GYM, POOL, BUSINESS_CENTER -> "AMENITIES";
            default -> "OTHER";
        };
    }

    private RevenueResponse mapToResponse(Revenue revenue) {
        return RevenueResponse.builder()
                .id(revenue.getId())
                .revenueNumber(revenue.getRevenueNumber())
                .revenueType(revenue.getRevenueType())
                .status(revenue.getStatus())
                .amount(revenue.getAmount())
                .taxAmount(revenue.getTaxAmount())
                .netAmount(revenue.getNetAmount())
                .revenueDate(revenue.getRevenueDate())
                .reservationId(revenue.getReservation() != null ? revenue.getReservation().getId() : null)
                .reservationNumber(revenue.getReservation() != null ? revenue.getReservation().getReservationNumber() : null)
                .guestId(revenue.getGuest() != null ? revenue.getGuest().getId() : null)
                .guestName(revenue.getGuest() != null ? 
                    revenue.getGuest().getUser().getFirstName() + " " + revenue.getGuest().getUser().getLastName() : null)
                .roomId(revenue.getRoom() != null ? revenue.getRoom().getId() : null)
                .roomNumber(revenue.getRoom() != null ? revenue.getRoom().getRoomNumber() : null)
                .paymentId(revenue.getPayment() != null ? revenue.getPayment().getId() : null)
                .department(revenue.getDepartment())
                .costCenter(revenue.getCostCenter())
                .glCode(revenue.getGlCode())
                .description(revenue.getDescription())
                .quantity(revenue.getQuantity())
                .unitPrice(revenue.getUnitPrice())
                .discountAmount(revenue.getDiscountAmount())
                .currency(revenue.getCurrency())
                .sourceReference(revenue.getSourceReference())
                .postedToGl(revenue.getPostedToGl())
                .postingDate(revenue.getPostingDate())
                .recognizedDate(revenue.getRecognizedDate())
                .notes(revenue.getNotes())
                .createdAt(revenue.getCreatedAt())
                .build();
    }
}
