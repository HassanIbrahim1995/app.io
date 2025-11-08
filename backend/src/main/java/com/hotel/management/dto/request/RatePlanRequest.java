package com.hotel.management.dto.request;

import com.hotel.management.entity.enums.CancellationPolicyType;
import com.hotel.management.entity.enums.RatePlanType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class RatePlanRequest {
    
    @NotBlank(message = "Plan code is required")
    @Size(min = 3, max = 50, message = "Plan code must be between 3 and 50 characters")
    private String planCode;
    
    @NotBlank(message = "Plan name is required")
    @Size(max = 200, message = "Plan name must not exceed 200 characters")
    private String name;
    
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
    
    @NotNull(message = "Plan type is required")
    private RatePlanType planType;
    
    @NotNull(message = "Cancellation policy is required")
    private CancellationPolicyType cancellationPolicy;
    
    private Long roomTypeId;
    
    @DecimalMin(value = "0.0", message = "Base price modifier must be positive")
    private BigDecimal basePriceModifier = BigDecimal.valueOf(1.0);
    
    @DecimalMin(value = "0.0", message = "Percentage discount must be non-negative")
    @DecimalMax(value = "100.0", message = "Percentage discount cannot exceed 100%")
    private BigDecimal percentageDiscount = BigDecimal.ZERO;
    
    @DecimalMin(value = "0.0", message = "Fixed discount must be non-negative")
    private BigDecimal fixedDiscount = BigDecimal.ZERO;
    
    @Min(value = 1, message = "Minimum nights must be at least 1")
    private Integer minimumNights = 1;
    
    @Min(value = 1, message = "Maximum nights must be at least 1")
    private Integer maximumNights;
    
    @Min(value = 0, message = "Advance booking days must be non-negative")
    private Integer advanceBookingDays;
    
    @Min(value = 0, message = "Cancellation deadline hours must be non-negative")
    private Integer cancellationDeadlineHours;
    
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "100.0")
    private BigDecimal cancellationPenaltyPercentage = BigDecimal.ZERO;
    
    @NotNull(message = "Refundability must be specified")
    private Boolean isRefundable = true;
    
    private Boolean includesBreakfast = false;
    private Boolean includesWifi = true;
    private Boolean includesParking = false;
    
    private LocalDate validFrom;
    private LocalDate validTo;
    
    private String blackoutDates;
    
    private Boolean isActive = true;
    private Boolean requiresMembership = false;
    
    private String minimumLoyaltyTier;
    
    @Min(value = 0)
    private Integer displayOrder = 0;
    
    private Boolean isVisibleToPublic = true;
    
    @Min(value = 1)
    private Integer maxOccupancy;
    
    private Boolean earlyCheckinAllowed = false;
    private Boolean lateCheckoutAllowed = false;
    
    private String cancellationPolicyDetails;
    private String termsAndConditions;
}
