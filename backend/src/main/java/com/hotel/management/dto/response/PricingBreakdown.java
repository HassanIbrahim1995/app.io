package com.hotel.management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PricingBreakdown {
    private BigDecimal roomBasePrice;
    private Integer numberOfNights;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private String discountDescription;
    private BigDecimal taxAmount;
    private List<TaxDetail> taxes;
    private BigDecimal cityTaxAmount;
    private List<FeeDetail> extraFees;
    private BigDecimal totalFees;
    private BigDecimal grandTotal;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TaxDetail {
        private String taxName;
        private BigDecimal rate;
        private BigDecimal amount;
        private String calculationType;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FeeDetail {
        private String feeName;
        private String feeType;
        private BigDecimal amount;
        private Boolean isMandatory;
        private Boolean isRefundable;
        private String description;
    }
}
