package com.hotel.management.service;

import com.hotel.management.dto.request.GuestRegistrationRequest;
import com.hotel.management.dto.response.AuthResponse;
import com.hotel.management.dto.response.GuestProfileResponse;
import com.hotel.management.entity.User;

public interface GuestProfileService {
    AuthResponse registerGuest(GuestRegistrationRequest request);
    GuestProfileResponse getGuestProfile(User user);
    GuestProfileResponse updateGuestProfile(User user, GuestProfileResponse profileData);
    void completeProfile(User user);
}
