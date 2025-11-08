package com.hotel.management.controller;

import com.hotel.management.entity.UpsellOffer;
import com.hotel.management.service.UpsellService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/upsell")
@RequiredArgsConstructor
@Tag(name = "Upselling", description = "Upsell and recommendation endpoints")
public class UpsellController {

    private final UpsellService upsellService;

    @GetMapping("/offers")
    @Operation(summary = "Get all active upsell offers")
    public ResponseEntity<List<UpsellOffer>> getActiveOffers() {
        List<UpsellOffer> offers = upsellService.getActiveOffers();
        return ResponseEntity.ok(offers);
    }

    @GetMapping("/room-upgrades/{roomTypeId}")
    @Operation(summary = "Get room upgrade offers")
    public ResponseEntity<List<UpsellOffer>> getRoomUpgradeOffers(@PathVariable Long roomTypeId) {
        List<UpsellOffer> offers = upsellService.getRoomUpgradeOffers(roomTypeId);
        return ResponseEntity.ok(offers);
    }

    @GetMapping("/recommendations/{reservationId}")
    @Operation(summary = "Get personalized recommendations for reservation")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<UpsellOffer>> getRecommendations(@PathVariable Long reservationId) {
        List<UpsellOffer> recommendations = upsellService.getRecommendationsForReservation(reservationId);
        return ResponseEntity.ok(recommendations);
    }

    @PostMapping("/offers/{offerId}/accept")
    @Operation(summary = "Accept an upsell offer")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UpsellOffer> acceptOffer(
            @PathVariable Long offerId,
            @RequestParam Long reservationId) {
        UpsellOffer offer = upsellService.acceptOffer(offerId, reservationId);
        return ResponseEntity.ok(offer);
    }
}
