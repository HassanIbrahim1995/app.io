package com.hotel.management.controller;

import com.hotel.management.dto.request.GroupReservationRequest;
import com.hotel.management.entity.User;
import com.hotel.management.service.GroupReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/group-reservations")
@RequiredArgsConstructor
@Tag(name = "Group Reservations", description = "Group reservation management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class GroupReservationController {

    private final GroupReservationService groupReservationService;

    @PostMapping
    @Operation(summary = "Create group reservation")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'FRONT_DESK')")
    public ResponseEntity<Map<String, Object>> createGroupReservation(
            @Valid @RequestBody GroupReservationRequest request,
            @AuthenticationPrincipal User user) {
        Map<String, Object> response = groupReservationService.createGroupReservation(request, user);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get group reservation by ID")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'FRONT_DESK')")
    public ResponseEntity<Map<String, Object>> getGroupReservation(@PathVariable Long id) {
        Map<String, Object> response = groupReservationService.getGroupReservation(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all group reservations")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'FRONT_DESK')")
    public ResponseEntity<List<Map<String, Object>>> getAllGroupReservations() {
        List<Map<String, Object>> groups = groupReservationService.getAllGroupReservations();
        return ResponseEntity.ok(groups);
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel group reservation")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'FRONT_DESK')")
    public ResponseEntity<Map<String, Object>> cancelGroupReservation(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        Map<String, Object> response = groupReservationService.cancelGroupReservation(id, user);
        return ResponseEntity.ok(response);
    }
}
