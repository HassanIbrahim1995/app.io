package com.hotel.management.dto.response;

import com.hotel.management.entity.enums.DiscountType;
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
public class PromotionResponse {
    private Long id;
    private String code;
    private String name;
    private String description;
    private DiscountType discountType;
    private BigDecimal discountValue;
    private BigDecimal maxDiscount;
    private BigDecimal minBookingAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer usageLimit;
    private Integer usedCount;
    private Integer perUserLimit;
    private Boolean isActive;
    private Boolean requiresAuthentication;
    private Integer minNights;
    private Long applicableRoomTypeId;
    private String applicableDays;
}
