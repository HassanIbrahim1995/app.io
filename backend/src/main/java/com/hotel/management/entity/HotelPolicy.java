package com.hotel.management.entity;

import com.hotel.management.entity.enums.PolicyCategory;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "hotel_policies", indexes = {
    @Index(name = "idx_policy_category", columnList = "category"),
    @Index(name = "idx_policy_active", columnList = "is_active")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelPolicy extends BaseEntity {

    @Column(name = "policy_code", nullable = false, unique = true, length = 50)
    private String policyCode;

    @Column(nullable = false, length = 200)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private PolicyCategory category;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "short_description", length = 500)
    private String shortDescription;

    @Column(name = "effective_date")
    private LocalDate effectiveDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "is_mandatory", nullable = false)
    private Boolean isMandatory = false;

    @Column(name = "requires_acceptance", nullable = false)
    private Boolean requiresAcceptance = false;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "display_order")
    private Integer displayOrder = 0;

    @Column(name = "version", length = 20)
    private String version;

    @Column(name = "applies_to", length = 50)
    private String appliesTo = "ALL"; // ALL, GUESTS, EMPLOYEES

    @Column(name = "language_code", length = 5)
    private String languageCode = "en";
}
