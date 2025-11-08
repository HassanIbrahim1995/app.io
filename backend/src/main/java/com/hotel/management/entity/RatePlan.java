package com.hotel.management.entity;

import com.hotel.management.entity.enums.CancellationPolicyType;
import com.hotel.management.entity.enums.RatePlanType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "rate_plans", indexes = {
    @Index(name = "idx_rate_plan_code", columnList = "plan_code"),
    @Index(name = "idx_rate_plan_type", columnList = "plan_type"),
    @Index(name = "idx_rate_plan_active", columnList = "is_active")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RatePlan extends BaseEntity {

    @Column(name = "plan_code", nullable = false, unique = true, length = 50)
    private String planCode;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "plan_type", nullable = false, length = 30)
    private RatePlanType planType;

    @Enumerated(EnumType.STRING)
    @Column(name = "cancellation_policy", nullable = false, length = 30)
    private CancellationPolicyType cancellationPolicy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_type_id")
    private RoomType roomType;

    @Column(name = "base_price_modifier", precision = 5, scale = 2)
    private BigDecimal basePriceModifier = BigDecimal.valueOf(1.0); // Multiplier for base price

    @Column(name = "percentage_discount", precision = 5, scale = 2)
    private BigDecimal percentageDiscount = BigDecimal.ZERO; // Percentage off base price

    @Column(name = "fixed_discount", precision = 10, scale = 2)
    private BigDecimal fixedDiscount = BigDecimal.ZERO; // Fixed amount off

    @Column(name = "minimum_nights")
    private Integer minimumNights = 1;

    @Column(name = "maximum_nights")
    private Integer maximumNights;

    @Column(name = "advance_booking_days")
    private Integer advanceBookingDays; // Must book X days in advance

    @Column(name = "cancellation_deadline_hours")
    private Integer cancellationDeadlineHours; // Hours before check-in for free cancellation

    @Column(name = "cancellation_penalty_percentage", precision = 5, scale = 2)
    private BigDecimal cancellationPenaltyPercentage = BigDecimal.ZERO;

    @Column(name = "is_refundable", nullable = false)
    private Boolean isRefundable = true;

    @Column(name = "includes_breakfast", nullable = false)
    private Boolean includesBreakfast = false;

    @Column(name = "includes_wifi", nullable = false)
    private Boolean includesWifi = true;

    @Column(name = "includes_parking", nullable = false)
    private Boolean includesParking = false;

    @Column(name = "valid_from")
    private LocalDate validFrom;

    @Column(name = "valid_to")
    private LocalDate validTo;

    @Column(name = "blackout_dates", length = 1000)
    private String blackoutDates; // Comma-separated dates or date ranges

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "requires_membership", nullable = false)
    private Boolean requiresMembership = false;

    @Column(name = "minimum_loyalty_tier", length = 20)
    private String minimumLoyaltyTier; // BRONZE, SILVER, GOLD, PLATINUM

    @Column(name = "display_order")
    private Integer displayOrder = 0;

    @Column(name = "is_visible_to_public", nullable = false)
    private Boolean isVisibleToPublic = true;

    @Column(name = "max_occupancy")
    private Integer maxOccupancy;

    @Column(name = "early_checkin_allowed", nullable = false)
    private Boolean earlyCheckinAllowed = false;

    @Column(name = "late_checkout_allowed", nullable = false)
    private Boolean lateCheckoutAllowed = false;

    @Column(name = "cancellation_policy_details", columnDefinition = "TEXT")
    private String cancellationPolicyDetails;

    @Column(name = "terms_and_conditions", columnDefinition = "TEXT")
    private String termsAndConditions;
}
