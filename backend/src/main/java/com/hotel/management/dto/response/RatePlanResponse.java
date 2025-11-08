package com.hotel.management.dto.response;

import com.hotel.management.entity.enums.CancellationPolicyType;
import com.hotel.management.entity.enums.RatePlanType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatePlanResponse {
    private Long id;
    private String planCode;
    private String name;
    private String description;
    private RatePlanType planType;
    private CancellationPolicyType cancellationPolicy;
    private Long roomTypeId;
    private String roomTypeName;
    private BigDecimal basePriceModifier;
    private BigDecimal percentageDiscount;
    private BigDecimal fixedDiscount;
    private Integer minimumNights;
    private Integer maximumNights;
    private Integer advanceBookingDays;
    private Integer cancellationDeadlineHours;
    private BigDecimal cancellationPenaltyPercentage;
    private Boolean isRefundable;
    private Boolean includesBreakfast;
    private Boolean includesWifi;
    private Boolean includesParking;
    private LocalDate validFrom;
    private LocalDate validTo;
    private String blackoutDates;
    private Boolean isActive;
    private Boolean requiresMembership;
    private String minimumLoyaltyTier;
    private Integer displayOrder;
    private Boolean isVisibleToPublic;
    private Integer maxOccupancy;
    private Boolean earlyCheckinAllowed;
    private Boolean lateCheckoutAllowed;
    private String cancellationPolicyDetails;
    private String termsAndConditions;
    
    // Calculated price for display
    private BigDecimal estimatedPrice;
    private BigDecimal savingsAmount;
    private BigDecimal savingsPercentage;
}
