package com.hotel.management.controller;

import com.hotel.management.dto.request.ReservationRequest;
import com.hotel.management.dto.response.ReservationResponse;
import com.hotel.management.entity.User;
import com.hotel.management.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
@Tag(name = "Reservations", description = "Reservation management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    @Operation(summary = "Create a new reservation")
    public ResponseEntity<ReservationResponse> createReservation(
            @Valid @RequestBody ReservationRequest request,
            @AuthenticationPrincipal User user) {
        ReservationResponse response = reservationService.createReservation(request, user);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get reservation by ID")
    public ResponseEntity<ReservationResponse> getReservationById(@PathVariable Long id) {
        ReservationResponse response = reservationService.getReservationById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-reservations")
    @Operation(summary = "Get my reservations")
    public ResponseEntity<List<ReservationResponse>> getMyReservations(@AuthenticationPrincipal User user) {
        List<ReservationResponse> reservations = reservationService.getMyReservations(user);
        return ResponseEntity.ok(reservations);
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel reservation")
    public ResponseEntity<ReservationResponse> cancelReservation(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        ReservationResponse response = reservationService.cancelReservation(id, user);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/check-in")
    @Operation(summary = "Check-in reservation")
    public ResponseEntity<ReservationResponse> checkIn(@PathVariable Long id) {
        ReservationResponse response = reservationService.checkIn(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/check-out")
    @Operation(summary = "Check-out reservation")
    public ResponseEntity<ReservationResponse> checkOut(@PathVariable Long id) {
        ReservationResponse response = reservationService.checkOut(id);
        return ResponseEntity.ok(response);
    }
}
