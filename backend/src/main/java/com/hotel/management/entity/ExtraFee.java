package com.hotel.management.entity;

import com.hotel.management.entity.enums.FeeType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "extra_fees", indexes = {
    @Index(name = "idx_extra_fee_type", columnList = "fee_type"),
    @Index(name = "idx_extra_fee_active", columnList = "is_active")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExtraFee extends BaseEntity {

    @Column(name = "fee_code", nullable = false, unique = true, length = 50)
    private String feeCode;

    @Column(name = "fee_name", nullable = false, length = 200)
    private String feeName;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "fee_type", nullable = false, length = 50)
    private FeeType feeType;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "calculation_basis", nullable = false, length = 20)
    private String calculationBasis = "PER_STAY"; // PER_STAY, PER_NIGHT, PER_PERSON, PERCENTAGE

    @Column(name = "is_mandatory", nullable = false)
    private Boolean isMandatory = false;

    @Column(name = "is_refundable", nullable = false)
    private Boolean isRefundable = false;

    @Column(name = "applies_to_room_types", length = 500)
    private String appliesToRoomTypes; // Comma-separated room type IDs

    @Column(name = "min_stay_nights")
    private Integer minStayNights;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "display_order")
    private Integer displayOrder = 0;
}
