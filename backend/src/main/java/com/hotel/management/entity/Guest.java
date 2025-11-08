package com.hotel.management.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "guests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Guest extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "passport_number", length = 50)
    private String passportNumber;

    @Column(name = "id_number", length = 50)
    private String idNumber;

    @Column(length = 50)
    private String nationality;

    @Column(name = "loyalty_points", nullable = false)
    private Integer loyaltyPoints = 0;

    @Column(name = "loyalty_tier", length = 20)
    private String loyaltyTier = "BRONZE";

    @Column(name = "special_requests", length = 1000)
    private String specialRequests;

    @Column(name = "dietary_preferences", length = 500)
    private String dietaryPreferences;

    @Column(name = "emergency_contact_name", length = 100)
    private String emergencyContactName;

    @Column(name = "emergency_contact_phone", length = 20)
    private String emergencyContactPhone;

    @OneToMany(mappedBy = "guest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reservation> reservations = new ArrayList<>();

    @OneToMany(mappedBy = "guest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "guest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GuestPreference> preferences = new ArrayList<>();

    @Column(length = 100)
    private String company;

    @Column(name = "job_title", length = 100)
    private String jobTitle;

    @Column(name = "vip_status", nullable = false)
    private Boolean vipStatus = false;

    @Column(name = "marketing_consent", nullable = false)
    private Boolean marketingConsent = false;

    @Column(name = "email_notifications", nullable = false)
    private Boolean emailNotifications = true;

    @Column(name = "sms_notifications", nullable = false)
    private Boolean smsNotifications = false;

    @Column(name = "language_preference", length = 10)
    private String languagePreference = "en";

    @Column(name = "currency_preference", length = 3)
    private String currencyPreference = "USD";

    @Column(name = "allergies", length = 500)
    private String allergies;

    @Column(name = "accessibility_needs", length = 500)
    private String accessibilityNeeds;

    @Column(name = "document_type", length = 50)
    private String documentType; // PASSPORT, DRIVERS_LICENSE, ID_CARD

    @Column(name = "document_number", length = 50)
    private String documentNumber;

    @Column(name = "document_expiry")
    private LocalDate documentExpiry;

    @Column(name = "issuing_country", length = 50)
    private String issuingCountry;

    @Column(name = "profile_completed", nullable = false)
    private Boolean profileCompleted = false;

    @Column(name = "last_stay_date")
    private LocalDate lastStayDate;

    @Column(name = "total_stays")
    private Integer totalStays = 0;

    @Column(name = "total_spent", precision = 10, scale = 2)
    private BigDecimal totalSpent = BigDecimal.ZERO;
}
