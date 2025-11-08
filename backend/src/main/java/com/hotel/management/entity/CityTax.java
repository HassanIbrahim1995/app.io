package com.hotel.management.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "city_taxes", indexes = {
    @Index(name = "idx_city_tax_city", columnList = "city"),
    @Index(name = "idx_city_tax_active", columnList = "is_active")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CityTax extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String city;

    @Column(length = 100)
    private String state;

    @Column(nullable = false, length = 100)
    private String country;

    @Column(name = "tax_name", nullable = false, length = 200)
    private String taxName;

    @Column(name = "tax_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal taxRate; // Percentage

    @Column(name = "fixed_amount", precision = 10, scale = 2)
    private BigDecimal fixedAmount; // Per night per room

    @Column(name = "calculation_type", nullable = false, length = 20)
    private String calculationType = "PERCENTAGE"; // PERCENTAGE, FIXED, PER_NIGHT, PER_PERSON

    @Column(name = "applies_to", length = 50)
    private String appliesTo = "ROOM"; // ROOM, SERVICES, ALL

    @Column(name = "effective_from")
    private LocalDate effectiveFrom;

    @Column(name = "effective_to")
    private LocalDate effectiveTo;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(length = 1000)
    private String description;
}
