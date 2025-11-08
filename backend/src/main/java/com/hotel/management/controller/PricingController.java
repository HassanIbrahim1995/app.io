package com.hotel.management.controller;

import com.hotel.management.dto.response.PricingBreakdown;
import com.hotel.management.service.PricingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/pricing")
@RequiredArgsConstructor
@Tag(name = "Pricing", description = "Pricing calculation endpoints")
public class PricingController {

    private final PricingService pricingService;

    @GetMapping("/calculate")
    @Operation(summary = "Calculate reservation pricing with breakdown")
    public ResponseEntity<PricingBreakdown> calculatePricing(
            @RequestParam Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            @RequestParam Integer numberOfGuests,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String promoCode) {
        
        PricingBreakdown breakdown = pricingService.calculateReservationPricing(
                roomId, checkIn, checkOut, numberOfGuests, city, promoCode);
        
        return ResponseEntity.ok(breakdown);
    }
}
