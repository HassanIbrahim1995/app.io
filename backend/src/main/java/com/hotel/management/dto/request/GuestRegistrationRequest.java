package com.hotel.management.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class GuestRegistrationRequest {
    
    // Account Information
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$",
             message = "Password must contain at least one digit, one lowercase, one uppercase, and one special character")
    private String password;
    
    // Personal Information
    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;
    
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Invalid phone number format")
    private String phone;
    
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;
    
    private String nationality;
    
    // Contact Information
    private String address;
    private String city;
    private String state;
    private String country;
    
    @Pattern(regexp = "^[0-9]{5,10}$", message = "Invalid postal code")
    private String postalCode;
    
    // Document Information (Optional at registration)
    private String documentType; // PASSPORT, DRIVERS_LICENSE, ID_CARD
    private String passportNumber;
    private String idNumber;
    private LocalDate documentExpiry;
    private String issuingCountry;
    
    // Company Information (Optional)
    private String company;
    private String jobTitle;
    
    // Emergency Contact
    private String emergencyContactName;
    
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Invalid emergency contact phone")
    private String emergencyContactPhone;
    
    private String emergencyContactRelation;
    
    // Preferences
    private String dietaryPreferences;
    private String specialRequests;
    private String allergies;
    private String accessibilityNeeds;
    private String languagePreference;
    
    // Communication Preferences
    private Boolean marketingConsent = false;
    private Boolean emailNotifications = true;
    private Boolean smsNotifications = false;
    
    // Policy Acceptance
    @AssertTrue(message = "You must accept the Terms and Conditions")
    private Boolean acceptTerms;
    
    @AssertTrue(message = "You must accept the Privacy Policy")
    private Boolean acceptPrivacy;
}
