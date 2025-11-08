package com.hotel.management.service.impl;

import com.hotel.management.dto.request.RatePlanRequest;
import com.hotel.management.dto.response.RatePlanResponse;
import com.hotel.management.entity.RatePlan;
import com.hotel.management.entity.RoomType;
import com.hotel.management.entity.enums.RatePlanType;
import com.hotel.management.exception.BadRequestException;
import com.hotel.management.exception.ResourceNotFoundException;
import com.hotel.management.repository.RatePlanRepository;
import com.hotel.management.repository.RoomTypeRepository;
import com.hotel.management.service.RatePlanService;
import com.hotel.management.util.DateValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RatePlanServiceImpl implements RatePlanService {

    private final RatePlanRepository ratePlanRepository;
    private final RoomTypeRepository roomTypeRepository;

    @Override
    @Transactional
    public RatePlanResponse createRatePlan(RatePlanRequest request) {
        // Check if plan code already exists
        if (ratePlanRepository.findByPlanCode(request.getPlanCode()).isPresent()) {
            throw new BadRequestException("Rate plan code already exists");
        }

        // Validate dates
        if (request.getValidFrom() != null && request.getValidTo() != null) {
            if (request.getValidTo().isBefore(request.getValidFrom())) {
                throw new BadRequestException("Valid-to date must be after valid-from date");
            }
        }

        // Validate minimum/maximum nights
        if (request.getMaximumNights() != null && request.getMaximumNights() < request.getMinimumNights()) {
            throw new BadRequestException("Maximum nights must be greater than or equal to minimum nights");
        }

        RatePlan ratePlan = RatePlan.builder()
                .planCode(request.getPlanCode().toUpperCase())
                .name(request.getName())
                .description(request.getDescription())
                .planType(request.getPlanType())
                .cancellationPolicy(request.getCancellationPolicy())
                .basePriceModifier(request.getBasePriceModifier())
                .percentageDiscount(request.getPercentageDiscount())
                .fixedDiscount(request.getFixedDiscount())
                .minimumNights(request.getMinimumNights())
                .maximumNights(request.getMaximumNights())
                .advanceBookingDays(request.getAdvanceBookingDays())
                .cancellationDeadlineHours(request.getCancellationDeadlineHours())
                .cancellationPenaltyPercentage(request.getCancellationPenaltyPercentage())
                .isRefundable(request.getIsRefundable())
                .includesBreakfast(request.getIncludesBreakfast())
                .includesWifi(request.getIncludesWifi())
                .includesParking(request.getIncludesParking())
                .validFrom(request.getValidFrom())
                .validTo(request.getValidTo())
                .blackoutDates(request.getBlackoutDates())
                .isActive(request.getIsActive())
                .requiresMembership(request.getRequiresMembership())
                .minimumLoyaltyTier(request.getMinimumLoyaltyTier())
                .displayOrder(request.getDisplayOrder())
                .isVisibleToPublic(request.getIsVisibleToPublic())
                .maxOccupancy(request.getMaxOccupancy())
                .earlyCheckinAllowed(request.getEarlyCheckinAllowed())
                .lateCheckoutAllowed(request.getLateCheckoutAllowed())
                .cancellationPolicyDetails(request.getCancellationPolicyDetails())
                .termsAndConditions(request.getTermsAndConditions())
                .build();

        // Set room type if provided
        if (request.getRoomTypeId() != null) {
            RoomType roomType = roomTypeRepository.findById(request.getRoomTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("RoomType", "id", request.getRoomTypeId()));
            ratePlan.setRoomType(roomType);
        }

        ratePlan = ratePlanRepository.save(ratePlan);
        return mapToResponse(ratePlan);
    }

    @Override
    @Transactional
    public RatePlanResponse updateRatePlan(Long id, RatePlanRequest request) {
        RatePlan ratePlan = ratePlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RatePlan", "id", id));

        // Validate dates
        if (request.getValidFrom() != null && request.getValidTo() != null) {
            if (request.getValidTo().isBefore(request.getValidFrom())) {
                throw new BadRequestException("Valid-to date must be after valid-from date");
            }
        }

        ratePlan.setName(request.getName());
        ratePlan.setDescription(request.getDescription());
        ratePlan.setPlanType(request.getPlanType());
        ratePlan.setCancellationPolicy(request.getCancellationPolicy());
        ratePlan.setBasePriceModifier(request.getBasePriceModifier());
        ratePlan.setPercentageDiscount(request.getPercentageDiscount());
        ratePlan.setFixedDiscount(request.getFixedDiscount());
        ratePlan.setMinimumNights(request.getMinimumNights());
        ratePlan.setMaximumNights(request.getMaximumNights());
        ratePlan.setAdvanceBookingDays(request.getAdvanceBookingDays());
        ratePlan.setCancellationDeadlineHours(request.getCancellationDeadlineHours());
        ratePlan.setCancellationPenaltyPercentage(request.getCancellationPenaltyPercentage());
        ratePlan.setIsRefundable(request.getIsRefundable());
        ratePlan.setIncludesBreakfast(request.getIncludesBreakfast());
        ratePlan.setIncludesWifi(request.getIncludesWifi());
        ratePlan.setIncludesParking(request.getIncludesParking());
        ratePlan.setValidFrom(request.getValidFrom());
        ratePlan.setValidTo(request.getValidTo());
        ratePlan.setBlackoutDates(request.getBlackoutDates());
        ratePlan.setIsActive(request.getIsActive());
        ratePlan.setRequiresMembership(request.getRequiresMembership());
        ratePlan.setMinimumLoyaltyTier(request.getMinimumLoyaltyTier());
        ratePlan.setDisplayOrder(request.getDisplayOrder());
        ratePlan.setIsVisibleToPublic(request.getIsVisibleToPublic());
        ratePlan.setMaxOccupancy(request.getMaxOccupancy());
        ratePlan.setEarlyCheckinAllowed(request.getEarlyCheckinAllowed());
        ratePlan.setLateCheckoutAllowed(request.getLateCheckoutAllowed());
        ratePlan.setCancellationPolicyDetails(request.getCancellationPolicyDetails());
        ratePlan.setTermsAndConditions(request.getTermsAndConditions());

        // Update room type if provided
        if (request.getRoomTypeId() != null) {
            RoomType roomType = roomTypeRepository.findById(request.getRoomTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("RoomType", "id", request.getRoomTypeId()));
            ratePlan.setRoomType(roomType);
        }

        ratePlan = ratePlanRepository.save(ratePlan);
        return mapToResponse(ratePlan);
    }

    @Override
    public RatePlanResponse getRatePlanById(Long id) {
        RatePlan ratePlan = ratePlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RatePlan", "id", id));
        return mapToResponse(ratePlan);
    }

    @Override
    public RatePlanResponse getRatePlanByCode(String planCode) {
        RatePlan ratePlan = ratePlanRepository.findByPlanCode(planCode.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("RatePlan", "planCode", planCode));
        return mapToResponse(ratePlan);
    }

    @Override
    public List<RatePlanResponse> getAllRatePlans() {
        return ratePlanRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RatePlanResponse> getActivePublicRatePlans() {
        return ratePlanRepository.findAllActiveAndPublic().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RatePlanResponse> getValidRatePlansForDate(LocalDate date) {
        return ratePlanRepository.findValidRatePlansForDate(date).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RatePlanResponse> getRatePlansByRoomType(Long roomTypeId) {
        return ratePlanRepository.findByRoomTypeId(roomTypeId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RatePlanResponse> getRatePlansByType(RatePlanType planType) {
        return ratePlanRepository.findActiveByType(planType).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RatePlanResponse> getRefundableRatePlans() {
        return ratePlanRepository.findByRefundability(true).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RatePlanResponse> getNonRefundableRatePlans() {
        return ratePlanRepository.findByRefundability(false).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteRatePlan(Long id) {
        if (!ratePlanRepository.existsById(id)) {
            throw new ResourceNotFoundException("RatePlan", "id", id);
        }
        ratePlanRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deactivateRatePlan(Long id) {
        RatePlan ratePlan = ratePlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RatePlan", "id", id));
        ratePlan.setIsActive(false);
        ratePlanRepository.save(ratePlan);
    }

    @Override
    public BigDecimal calculatePriceWithRatePlan(BigDecimal basePrice, Long ratePlanId, Integer numberOfNights) {
        RatePlan ratePlan = ratePlanRepository.findById(ratePlanId)
                .orElseThrow(() -> new ResourceNotFoundException("RatePlan", "id", ratePlanId));

        // Start with base price for the total stay
        BigDecimal totalPrice = basePrice.multiply(BigDecimal.valueOf(numberOfNights));

        // Apply base price modifier (e.g., 0.9 for 10% off, 1.0 for standard)
        totalPrice = totalPrice.multiply(ratePlan.getBasePriceModifier());

        // Apply percentage discount
        if (ratePlan.getPercentageDiscount().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal discount = totalPrice.multiply(ratePlan.getPercentageDiscount())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            totalPrice = totalPrice.subtract(discount);
        }

        // Apply fixed discount
        if (ratePlan.getFixedDiscount().compareTo(BigDecimal.ZERO) > 0) {
            totalPrice = totalPrice.subtract(ratePlan.getFixedDiscount());
        }

        // Ensure price doesn't go negative
        if (totalPrice.compareTo(BigDecimal.ZERO) < 0) {
            totalPrice = BigDecimal.ZERO;
        }

        return totalPrice.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public boolean isRatePlanValidForDates(Long ratePlanId, LocalDate checkIn, LocalDate checkOut) {
        RatePlan ratePlan = ratePlanRepository.findById(ratePlanId)
                .orElseThrow(() -> new ResourceNotFoundException("RatePlan", "id", ratePlanId));

        // Check if active
        if (!ratePlan.getIsActive()) {
            return false;
        }

        // Check validity dates
        if (ratePlan.getValidFrom() != null && checkIn.isBefore(ratePlan.getValidFrom())) {
            return false;
        }
        if (ratePlan.getValidTo() != null && checkOut.isAfter(ratePlan.getValidTo())) {
            return false;
        }

        // Check minimum/maximum nights
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        if (nights < ratePlan.getMinimumNights()) {
            return false;
        }
        if (ratePlan.getMaximumNights() != null && nights > ratePlan.getMaximumNights()) {
            return false;
        }

        // Check advance booking requirement
        if (ratePlan.getAdvanceBookingDays() != null) {
            long daysInAdvance = ChronoUnit.DAYS.between(LocalDate.now(), checkIn);
            if (daysInAdvance < ratePlan.getAdvanceBookingDays()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean canCancelWithRefund(Long ratePlanId, LocalDate checkInDate) {
        RatePlan ratePlan = ratePlanRepository.findById(ratePlanId)
                .orElseThrow(() -> new ResourceNotFoundException("RatePlan", "id", ratePlanId));

        // Non-refundable plans
        if (!ratePlan.getIsRefundable()) {
            return false;
        }

        // Check cancellation deadline
        if (ratePlan.getCancellationDeadlineHours() != null) {
            LocalDateTime checkInDateTime = checkInDate.atStartOfDay();
            LocalDateTime now = LocalDateTime.now();
            long hoursUntilCheckIn = ChronoUnit.HOURS.between(now, checkInDateTime);
            
            return hoursUntilCheckIn >= ratePlan.getCancellationDeadlineHours();
        }

        return true;
    }

    private RatePlanResponse mapToResponse(RatePlan ratePlan) {
        return RatePlanResponse.builder()
                .id(ratePlan.getId())
                .planCode(ratePlan.getPlanCode())
                .name(ratePlan.getName())
                .description(ratePlan.getDescription())
                .planType(ratePlan.getPlanType())
                .cancellationPolicy(ratePlan.getCancellationPolicy())
                .roomTypeId(ratePlan.getRoomType() != null ? ratePlan.getRoomType().getId() : null)
                .roomTypeName(ratePlan.getRoomType() != null ? ratePlan.getRoomType().getName() : null)
                .basePriceModifier(ratePlan.getBasePriceModifier())
                .percentageDiscount(ratePlan.getPercentageDiscount())
                .fixedDiscount(ratePlan.getFixedDiscount())
                .minimumNights(ratePlan.getMinimumNights())
                .maximumNights(ratePlan.getMaximumNights())
                .advanceBookingDays(ratePlan.getAdvanceBookingDays())
                .cancellationDeadlineHours(ratePlan.getCancellationDeadlineHours())
                .cancellationPenaltyPercentage(ratePlan.getCancellationPenaltyPercentage())
                .isRefundable(ratePlan.getIsRefundable())
                .includesBreakfast(ratePlan.getIncludesBreakfast())
                .includesWifi(ratePlan.getIncludesWifi())
                .includesParking(ratePlan.getIncludesParking())
                .validFrom(ratePlan.getValidFrom())
                .validTo(ratePlan.getValidTo())
                .blackoutDates(ratePlan.getBlackoutDates())
                .isActive(ratePlan.getIsActive())
                .requiresMembership(ratePlan.getRequiresMembership())
                .minimumLoyaltyTier(ratePlan.getMinimumLoyaltyTier())
                .displayOrder(ratePlan.getDisplayOrder())
                .isVisibleToPublic(ratePlan.getIsVisibleToPublic())
                .maxOccupancy(ratePlan.getMaxOccupancy())
                .earlyCheckinAllowed(ratePlan.getEarlyCheckinAllowed())
                .lateCheckoutAllowed(ratePlan.getLateCheckoutAllowed())
                .cancellationPolicyDetails(ratePlan.getCancellationPolicyDetails())
                .termsAndConditions(ratePlan.getTermsAndConditions())
                .build();
    }
}
