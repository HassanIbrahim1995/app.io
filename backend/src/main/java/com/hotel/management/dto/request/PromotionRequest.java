package com.hotel.management.dto.request;

import com.hotel.management.entity.enums.DiscountType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PromotionRequest {
    
    @NotBlank(message = "Promo code is required")
    @Size(min = 3, max = 20, message = "Promo code must be between 3 and 20 characters")
    private String code;
    
    @NotBlank(message = "Promotion name is required")
    private String name;
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
    
    @NotNull(message = "Discount type is required")
    private DiscountType discountType;
    
    @NotNull(message = "Discount value is required")
    @DecimalMin(value = "0.0", message = "Discount value must be positive")
    private BigDecimal discountValue;
    
    @DecimalMin(value = "0.0", message = "Maximum discount must be positive")
    private BigDecimal maxDiscount;
    
    @DecimalMin(value = "0.0", message = "Minimum booking amount must be positive")
    private BigDecimal minBookingAmount;
    
    @NotNull(message = "Start date is required")
    private LocalDate startDate;
    
    @NotNull(message = "End date is required")
    private LocalDate endDate;
    
    @Min(value = 0, message = "Usage limit must be non-negative")
    private Integer usageLimit;
    
    @Min(value = 0, message = "Per user limit must be non-negative")
    private Integer perUserLimit;
    
    private Boolean isActive = true;
    
    private Boolean requiresAuthentication = false;
    
    @Min(value = 1, message = "Minimum nights must be at least 1")
    private Integer minNights;
    
    private Long applicableRoomTypeId;
    
    private String applicableDays; // Comma-separated days: MON,TUE,WED
}
