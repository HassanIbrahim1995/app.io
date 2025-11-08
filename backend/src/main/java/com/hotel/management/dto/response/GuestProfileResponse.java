package com.hotel.management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuestProfileResponse {
    private Long id;
    private Long userId;
    
    // Personal Information
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private LocalDate dateOfBirth;
    private String nationality;
    
    // Contact Information
    private String address;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    
    // Document Information
    private String documentType;
    private String documentNumber;
    private LocalDate documentExpiry;
    private String issuingCountry;
    
    // Company Information
    private String company;
    private String jobTitle;
    
    // Loyalty Information
    private Integer loyaltyPoints;
    private String loyaltyTier;
    private Boolean vipStatus;
    
    // Preferences
    private String dietaryPreferences;
    private String specialRequests;
    private String allergies;
    private String accessibilityNeeds;
    private String languagePreference;
    private String currencyPreference;
    
    // Emergency Contact
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String emergencyContactRelation;
    
    // Communication Preferences
    private Boolean marketingConsent;
    private Boolean emailNotifications;
    private Boolean smsNotifications;
    
    // Statistics
    private Integer totalStays;
    private BigDecimal totalSpent;
    private LocalDate lastStayDate;
    private Boolean profileCompleted;
    
    // Preferences List
    private List<GuestPreferenceDto> preferences;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GuestPreferenceDto {
        private String preferenceType;
        private String preferenceValue;
        private String notes;
    }
}
