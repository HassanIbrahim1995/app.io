package com.hotel.management.controller;

import com.hotel.management.dto.request.RoomMoveRequest;
import com.hotel.management.dto.response.ReservationResponse;
import com.hotel.management.entity.User;
import com.hotel.management.service.RoomMoveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/room-moves")
@RequiredArgsConstructor
@Tag(name = "Room Moves", description = "Room move management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class RoomMoveController {

    private final RoomMoveService roomMoveService;

    @PostMapping
    @Operation(summary = "Move guest to different room")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'FRONT_DESK')")
    public ResponseEntity<ReservationResponse> moveRoom(
            @Valid @RequestBody RoomMoveRequest request,
            @AuthenticationPrincipal User user) {
        ReservationResponse response = roomMoveService.moveRoom(request, user);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history/{reservationId}")
    @Operation(summary = "Get room move history for reservation")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'FRONT_DESK')")
    public ResponseEntity<List<Object>> getRoomMoveHistory(@PathVariable Long reservationId) {
        List<Object> history = roomMoveService.getRoomMoveHistory(reservationId);
        return ResponseEntity.ok(history);
    }
}
