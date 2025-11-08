package com.hotel.management.controller;

import com.hotel.management.dto.request.GuestRegistrationRequest;
import com.hotel.management.dto.response.AuthResponse;
import com.hotel.management.dto.response.GuestProfileResponse;
import com.hotel.management.entity.User;
import com.hotel.management.service.GuestProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/guest-profile")
@RequiredArgsConstructor
@Tag(name = "Guest Profile", description = "Guest profile management endpoints")
public class GuestProfileController {

    private final GuestProfileService guestProfileService;

    @PostMapping("/register")
    @Operation(summary = "Register new guest with complete profile")
    public ResponseEntity<AuthResponse> registerGuest(@Valid @RequestBody GuestRegistrationRequest request) {
        AuthResponse response = guestProfileService.registerGuest(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/me")
    @Operation(summary = "Get my guest profile")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<GuestProfileResponse> getMyProfile(@AuthenticationPrincipal User user) {
        GuestProfileResponse profile = guestProfileService.getGuestProfile(user);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/me")
    @Operation(summary = "Update my guest profile")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<GuestProfileResponse> updateMyProfile(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody GuestProfileResponse profileData) {
        GuestProfileResponse updated = guestProfileService.updateGuestProfile(user, profileData);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/me/complete")
    @Operation(summary = "Mark profile as completed")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> completeProfile(@AuthenticationPrincipal User user) {
        guestProfileService.completeProfile(user);
        return ResponseEntity.ok().build();
    }
}
