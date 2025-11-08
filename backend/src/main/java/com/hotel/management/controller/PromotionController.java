package com.hotel.management.controller;

import com.hotel.management.dto.request.PromotionRequest;
import com.hotel.management.dto.response.PromotionResponse;
import com.hotel.management.service.PromotionService;
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

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/promotions")
@RequiredArgsConstructor
@Tag(name = "Promotions", description = "Promotion management endpoints")
public class PromotionController {

    private final PromotionService promotionService;

    @PostMapping
    @Operation(summary = "Create promotion")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<PromotionResponse> createPromotion(@Valid @RequestBody PromotionRequest request) {
        PromotionResponse response = promotionService.createPromotion(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update promotion")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<PromotionResponse> updatePromotion(
            @PathVariable Long id,
            @Valid @RequestBody PromotionRequest request) {
        PromotionResponse response = promotionService.updatePromotion(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get promotion by ID")
    public ResponseEntity<PromotionResponse> getPromotionById(@PathVariable Long id) {
        PromotionResponse response = promotionService.getPromotionById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get promotion by code")
    public ResponseEntity<PromotionResponse> getPromotionByCode(@PathVariable String code) {
        PromotionResponse response = promotionService.getPromotionByCode(code);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all promotions")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<PromotionResponse>> getAllPromotions() {
        List<PromotionResponse> promotions = promotionService.getAllPromotions();
        return ResponseEntity.ok(promotions);
    }

    @GetMapping("/active")
    @Operation(summary = "Get active promotions")
    public ResponseEntity<List<PromotionResponse>> getActivePromotions() {
        List<PromotionResponse> promotions = promotionService.getActivePromotions();
        return ResponseEntity.ok(promotions);
    }

    @GetMapping("/date-range")
    @Operation(summary = "Get promotions by date range")
    public ResponseEntity<List<PromotionResponse>> getPromotionsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<PromotionResponse> promotions = promotionService.getPromotionsByDateRange(startDate, endDate);
        return ResponseEntity.ok(promotions);
    }

    @GetMapping("/validate/{code}")
    @Operation(summary = "Validate promotion code")
    public ResponseEntity<Boolean> validatePromotionCode(
            @PathVariable String code,
            @RequestParam(required = false) Long userId) {
        boolean isValid = promotionService.validatePromotionCode(code, userId);
        return ResponseEntity.ok(isValid);
    }

    @PostMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate promotion")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> deactivatePromotion(@PathVariable Long id) {
        promotionService.deactivatePromotion(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete promotion")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePromotion(@PathVariable Long id) {
        promotionService.deletePromotion(id);
        return ResponseEntity.noContent().build();
    }
}
