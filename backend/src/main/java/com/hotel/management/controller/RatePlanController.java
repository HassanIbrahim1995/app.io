package com.hotel.management.controller;

import com.hotel.management.dto.request.RatePlanRequest;
import com.hotel.management.dto.response.RatePlanResponse;
import com.hotel.management.entity.enums.RatePlanType;
import com.hotel.management.service.RatePlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/rate-plans")
@RequiredArgsConstructor
@Tag(name = "Rate Plans", description = "Rate plan management endpoints")
public class RatePlanController {

    private final RatePlanService ratePlanService;

    @PostMapping
    @Operation(summary = "Create rate plan")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<RatePlanResponse> createRatePlan(@Valid @RequestBody RatePlanRequest request) {
        RatePlanResponse response = ratePlanService.createRatePlan(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update rate plan")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<RatePlanResponse> updateRatePlan(
            @PathVariable Long id,
            @Valid @RequestBody RatePlanRequest request) {
        RatePlanResponse response = ratePlanService.updateRatePlan(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get rate plan by ID")
    public ResponseEntity<RatePlanResponse> getRatePlanById(@PathVariable Long id) {
        RatePlanResponse response = ratePlanService.getRatePlanById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/code/{planCode}")
    @Operation(summary = "Get rate plan by code")
    public ResponseEntity<RatePlanResponse> getRatePlanByCode(@PathVariable String planCode) {
        RatePlanResponse response = ratePlanService.getRatePlanByCode(planCode);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all rate plans")
    public ResponseEntity<List<RatePlanResponse>> getAllRatePlans() {
        List<RatePlanResponse> ratePlans = ratePlanService.getActivePublicRatePlans();
        return ResponseEntity.ok(ratePlans);
    }

    @GetMapping("/valid-for-date")
    @Operation(summary = "Get valid rate plans for a specific date")
    public ResponseEntity<List<RatePlanResponse>> getValidRatePlansForDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<RatePlanResponse> ratePlans = ratePlanService.getValidRatePlansForDate(date);
        return ResponseEntity.ok(ratePlans);
    }

    @GetMapping("/room-type/{roomTypeId}")
    @Operation(summary = "Get rate plans by room type")
    public ResponseEntity<List<RatePlanResponse>> getRatePlansByRoomType(@PathVariable Long roomTypeId) {
        List<RatePlanResponse> ratePlans = ratePlanService.getRatePlansByRoomType(roomTypeId);
        return ResponseEntity.ok(ratePlans);
    }

    @GetMapping("/type/{planType}")
    @Operation(summary = "Get rate plans by type")
    public ResponseEntity<List<RatePlanResponse>> getRatePlansByType(@PathVariable RatePlanType planType) {
        List<RatePlanResponse> ratePlans = ratePlanService.getRatePlansByType(planType);
        return ResponseEntity.ok(ratePlans);
    }

    @GetMapping("/refundable")
    @Operation(summary = "Get refundable rate plans")
    public ResponseEntity<List<RatePlanResponse>> getRefundableRatePlans() {
        List<RatePlanResponse> ratePlans = ratePlanService.getRefundableRatePlans();
        return ResponseEntity.ok(ratePlans);
    }

    @GetMapping("/non-refundable")
    @Operation(summary = "Get non-refundable rate plans")
    public ResponseEntity<List<RatePlanResponse>> getNonRefundableRatePlans() {
        List<RatePlanResponse> ratePlans = ratePlanService.getNonRefundableRatePlans();
        return ResponseEntity.ok(ratePlans);
    }

    @GetMapping("/{id}/calculate-price")
    @Operation(summary = "Calculate price with rate plan")
    public ResponseEntity<BigDecimal> calculatePriceWithRatePlan(
            @PathVariable Long id,
            @RequestParam BigDecimal basePrice,
            @RequestParam Integer numberOfNights) {
        BigDecimal price = ratePlanService.calculatePriceWithRatePlan(basePrice, id, numberOfNights);
        return ResponseEntity.ok(price);
    }

    @GetMapping("/{id}/validate")
    @Operation(summary = "Validate rate plan for dates")
    public ResponseEntity<Boolean> isRatePlanValidForDates(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut) {
        boolean isValid = ratePlanService.isRatePlanValidForDates(id, checkIn, checkOut);
        return ResponseEntity.ok(isValid);
    }

    @GetMapping("/{id}/can-cancel")
    @Operation(summary = "Check if can cancel with refund")
    public ResponseEntity<Boolean> canCancelWithRefund(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate) {
        boolean canCancel = ratePlanService.canCancelWithRefund(id, checkInDate);
        return ResponseEntity.ok(canCancel);
    }

    @PostMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate rate plan")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> deactivateRatePlan(@PathVariable Long id) {
        ratePlanService.deactivateRatePlan(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete rate plan")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRatePlan(@PathVariable Long id) {
        ratePlanService.deleteRatePlan(id);
        return ResponseEntity.noContent().build();
    }
}
