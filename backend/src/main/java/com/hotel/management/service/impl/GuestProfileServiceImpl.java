package com.hotel.management.service.impl;

import com.hotel.management.dto.request.GuestRegistrationRequest;
import com.hotel.management.dto.response.AuthResponse;
import com.hotel.management.dto.response.GuestProfileResponse;
import com.hotel.management.dto.response.UserResponse;
import com.hotel.management.entity.Guest;
import com.hotel.management.entity.GuestPreference;
import com.hotel.management.entity.User;
import com.hotel.management.entity.enums.UserRole;
import com.hotel.management.exception.BadRequestException;
import com.hotel.management.exception.ResourceNotFoundException;
import com.hotel.management.repository.GuestRepository;
import com.hotel.management.repository.UserRepository;
import com.hotel.management.security.JwtTokenProvider;
import com.hotel.management.service.GuestProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GuestProfileServiceImpl implements GuestProfileService {

    private final UserRepository userRepository;
    private final GuestRepository guestRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    @Value("${app.jwt.expiration}")
    private long jwtExpirationMs;

    @Override
    @Transactional
    public AuthResponse registerGuest(GuestRegistrationRequest request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }

        // Create user account
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .address(request.getAddress())
                .city(request.getCity())
                .country(request.getCountry())
                .postalCode(request.getPostalCode())
                .role(UserRole.GUEST)
                .isActive(true)
                .isEmailVerified(false)
                .build();

        user = userRepository.save(user);

        // Create guest profile
        Guest guest = Guest.builder()
                .user(user)
                .dateOfBirth(request.getDateOfBirth())
                .passportNumber(request.getPassportNumber())
                .idNumber(request.getIdNumber())
                .nationality(request.getNationality())
                .company(request.getCompany())
                .jobTitle(request.getJobTitle())
                .emergencyContactName(request.getEmergencyContactName())
                .emergencyContactPhone(request.getEmergencyContactPhone())
                .dietaryPreferences(request.getDietaryPreferences())
                .specialRequests(request.getSpecialRequests())
                .allergies(request.getAllergies())
                .accessibilityNeeds(request.getAccessibilityNeeds())
                .languagePreference(request.getLanguagePreference())
                .marketingConsent(request.getMarketingConsent())
                .emailNotifications(request.getEmailNotifications())
                .smsNotifications(request.getSmsNotifications())
                .documentType(request.getDocumentType())
                .documentNumber(request.getDocumentType() != null ? 
                    (request.getPassportNumber() != null ? request.getPassportNumber() : request.getIdNumber()) : null)
                .documentExpiry(request.getDocumentExpiry())
                .issuingCountry(request.getIssuingCountry())
                .loyaltyPoints(0)
                .loyaltyTier("BRONZE")
                .profileCompleted(isProfileComplete(request))
                .build();

        guestRepository.save(guest);

        // Generate tokens
        String accessToken = tokenProvider.generateToken(user.getEmail());
        String refreshToken = tokenProvider.generateRefreshToken(user.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtExpirationMs / 1000)
                .user(mapToUserResponse(user))
                .build();
    }

    @Override
    public GuestProfileResponse getGuestProfile(User user) {
        Guest guest = guestRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Guest profile not found"));

        return mapToGuestProfileResponse(guest, user);
    }

    @Override
    @Transactional
    public GuestProfileResponse updateGuestProfile(User user, GuestProfileResponse profileData) {
        Guest guest = guestRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Guest profile not found"));

        // Update user information
        user.setFirstName(profileData.getFirstName());
        user.setLastName(profileData.getLastName());
        user.setPhone(profileData.getPhone());
        user.setAddress(profileData.getAddress());
        user.setCity(profileData.getCity());
        user.setCountry(profileData.getCountry());
        user.setPostalCode(profileData.getPostalCode());
        userRepository.save(user);

        // Update guest information
        guest.setDateOfBirth(profileData.getDateOfBirth());
        guest.setNationality(profileData.getNationality());
        guest.setCompany(profileData.getCompany());
        guest.setJobTitle(profileData.getJobTitle());
        guest.setDietaryPreferences(profileData.getDietaryPreferences());
        guest.setSpecialRequests(profileData.getSpecialRequests());
        guest.setAllergies(profileData.getAllergies());
        guest.setAccessibilityNeeds(profileData.getAccessibilityNeeds());
        guest.setLanguagePreference(profileData.getLanguagePreference());
        guest.setCurrencyPreference(profileData.getCurrencyPreference());
        guest.setEmergencyContactName(profileData.getEmergencyContactName());
        guest.setEmergencyContactPhone(profileData.getEmergencyContactPhone());
        guest.setMarketingConsent(profileData.getMarketingConsent());
        guest.setEmailNotifications(profileData.getEmailNotifications());
        guest.setSmsNotifications(profileData.getSmsNotifications());
        guest.setDocumentType(profileData.getDocumentType());
        guest.setDocumentNumber(profileData.getDocumentNumber());
        guest.setDocumentExpiry(profileData.getDocumentExpiry());
        guest.setIssuingCountry(profileData.getIssuingCountry());

        guestRepository.save(guest);

        return mapToGuestProfileResponse(guest, user);
    }

    @Override
    @Transactional
    public void completeProfile(User user) {
        Guest guest = guestRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Guest profile not found"));
        
        guest.setProfileCompleted(true);
        guestRepository.save(guest);
    }

    private boolean isProfileComplete(GuestRegistrationRequest request) {
        return request.getFirstName() != null &&
               request.getLastName() != null &&
               request.getEmail() != null &&
               request.getPhone() != null &&
               request.getDateOfBirth() != null &&
               request.getAddress() != null &&
               request.getCity() != null &&
               request.getCountry() != null;
    }

    private GuestProfileResponse mapToGuestProfileResponse(Guest guest, User user) {
        return GuestProfileResponse.builder()
                .id(guest.getId())
                .userId(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .dateOfBirth(guest.getDateOfBirth())
                .nationality(guest.getNationality())
                .address(user.getAddress())
                .city(user.getCity())
                .country(user.getCountry())
                .postalCode(user.getPostalCode())
                .documentType(guest.getDocumentType())
                .documentNumber(guest.getDocumentNumber())
                .documentExpiry(guest.getDocumentExpiry())
                .issuingCountry(guest.getIssuingCountry())
                .company(guest.getCompany())
                .jobTitle(guest.getJobTitle())
                .loyaltyPoints(guest.getLoyaltyPoints())
                .loyaltyTier(guest.getLoyaltyTier())
                .vipStatus(guest.getVipStatus())
                .dietaryPreferences(guest.getDietaryPreferences())
                .specialRequests(guest.getSpecialRequests())
                .allergies(guest.getAllergies())
                .accessibilityNeeds(guest.getAccessibilityNeeds())
                .languagePreference(guest.getLanguagePreference())
                .currencyPreference(guest.getCurrencyPreference())
                .emergencyContactName(guest.getEmergencyContactName())
                .emergencyContactPhone(guest.getEmergencyContactPhone())
                .marketingConsent(guest.getMarketingConsent())
                .emailNotifications(guest.getEmailNotifications())
                .smsNotifications(guest.getSmsNotifications())
                .totalStays(guest.getTotalStays())
                .totalSpent(guest.getTotalSpent())
                .lastStayDate(guest.getLastStayDate())
                .profileCompleted(guest.getProfileCompleted())
                .preferences(guest.getPreferences().stream()
                        .map(p -> GuestProfileResponse.GuestPreferenceDto.builder()
                                .preferenceType(p.getPreferenceType())
                                .preferenceValue(p.getPreferenceValue())
                                .notes(p.getNotes())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .isEmailVerified(user.getIsEmailVerified())
                .build();
    }
}
