package com.hotel.management.controller;

import com.hotel.management.dto.request.RoomSearchRequest;
import com.hotel.management.dto.response.RoomResponse;
import com.hotel.management.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
@Tag(name = "Rooms", description = "Room management endpoints")
public class RoomController {

    private final RoomService roomService;

    @GetMapping
    @Operation(summary = "Get all rooms")
    public ResponseEntity<List<RoomResponse>> getAllRooms() {
        List<RoomResponse> rooms = roomService.getAllRooms();
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get room by ID")
    public ResponseEntity<RoomResponse> getRoomById(@PathVariable Long id) {
        RoomResponse room = roomService.getRoomById(id);
        return ResponseEntity.ok(room);
    }

    @GetMapping("/search")
    @Operation(summary = "Search available rooms")
    public ResponseEntity<List<RoomResponse>> searchAvailableRooms(
            @ModelAttribute RoomSearchRequest searchRequest) {
        List<RoomResponse> rooms = roomService.searchAvailableRooms(searchRequest);
        return ResponseEntity.ok(rooms);
    }
}
