package com.hotel.management.service;

import com.hotel.management.dto.request.RatePlanRequest;
import com.hotel.management.dto.response.RatePlanResponse;
import com.hotel.management.entity.enums.RatePlanType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface RatePlanService {
    RatePlanResponse createRatePlan(RatePlanRequest request);
    RatePlanResponse updateRatePlan(Long id, RatePlanRequest request);
    RatePlanResponse getRatePlanById(Long id);
    RatePlanResponse getRatePlanByCode(String planCode);
    List<RatePlanResponse> getAllRatePlans();
    List<RatePlanResponse> getActivePublicRatePlans();
    List<RatePlanResponse> getValidRatePlansForDate(LocalDate date);
    List<RatePlanResponse> getRatePlansByRoomType(Long roomTypeId);
    List<RatePlanResponse> getRatePlansByType(RatePlanType planType);
    List<RatePlanResponse> getRefundableRatePlans();
    List<RatePlanResponse> getNonRefundableRatePlans();
    void deleteRatePlan(Long id);
    void deactivateRatePlan(Long id);
    BigDecimal calculatePriceWithRatePlan(BigDecimal basePrice, Long ratePlanId, Integer numberOfNights);
    boolean isRatePlanValidForDates(Long ratePlanId, LocalDate checkIn, LocalDate checkOut);
    boolean canCancelWithRefund(Long ratePlanId, LocalDate checkInDate);
}
