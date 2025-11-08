package com.hotel.management.controller;

import com.hotel.management.dto.request.HousekeepingTaskRequest;
import com.hotel.management.dto.response.HousekeepingTaskResponse;
import com.hotel.management.entity.enums.TaskStatus;
import com.hotel.management.service.HousekeepingService;
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
@RequestMapping("/api/v1/housekeeping")
@RequiredArgsConstructor
@Tag(name = "Housekeeping", description = "Housekeeping task management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class HousekeepingController {

    private final HousekeepingService housekeepingService;

    @PostMapping("/tasks")
    @Operation(summary = "Create housekeeping task")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'HOUSEKEEPING')")
    public ResponseEntity<HousekeepingTaskResponse> createTask(
            @Valid @RequestBody HousekeepingTaskRequest request) {
        HousekeepingTaskResponse response = housekeepingService.createTask(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/tasks")
    @Operation(summary = "Get all housekeeping tasks")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'HOUSEKEEPING')")
    public ResponseEntity<List<HousekeepingTaskResponse>> getAllTasks() {
        List<HousekeepingTaskResponse> tasks = housekeepingService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/tasks/{id}")
    @Operation(summary = "Get task by ID")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'HOUSEKEEPING')")
    public ResponseEntity<HousekeepingTaskResponse> getTaskById(@PathVariable Long id) {
        HousekeepingTaskResponse task = housekeepingService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    @GetMapping("/tasks/status/{status}")
    @Operation(summary = "Get tasks by status")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'HOUSEKEEPING')")
    public ResponseEntity<List<HousekeepingTaskResponse>> getTasksByStatus(
            @PathVariable TaskStatus status) {
        List<HousekeepingTaskResponse> tasks = housekeepingService.getTasksByStatus(status);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/tasks/room/{roomId}")
    @Operation(summary = "Get tasks by room")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'HOUSEKEEPING')")
    public ResponseEntity<List<HousekeepingTaskResponse>> getTasksByRoom(
            @PathVariable Long roomId) {
        List<HousekeepingTaskResponse> tasks = housekeepingService.getTasksByRoomId(roomId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/tasks/assigned/{userId}")
    @Operation(summary = "Get tasks assigned to user")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'HOUSEKEEPING')")
    public ResponseEntity<List<HousekeepingTaskResponse>> getTasksByUser(
            @PathVariable Long userId) {
        List<HousekeepingTaskResponse> tasks = housekeepingService.getTasksByAssignedUser(userId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/tasks/date/{date}")
    @Operation(summary = "Get tasks for specific date")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'HOUSEKEEPING')")
    public ResponseEntity<List<HousekeepingTaskResponse>> getTasksForDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<HousekeepingTaskResponse> tasks = housekeepingService.getTasksForDate(date);
        return ResponseEntity.ok(tasks);
    }

    @PostMapping("/tasks/{taskId}/assign/{userId}")
    @Operation(summary = "Assign task to user")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<HousekeepingTaskResponse> assignTask(
            @PathVariable Long taskId,
            @PathVariable Long userId) {
        HousekeepingTaskResponse task = housekeepingService.assignTask(taskId, userId);
        return ResponseEntity.ok(task);
    }

    @PostMapping("/tasks/{taskId}/start")
    @Operation(summary = "Start task")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'HOUSEKEEPING')")
    public ResponseEntity<HousekeepingTaskResponse> startTask(@PathVariable Long taskId) {
        HousekeepingTaskResponse task = housekeepingService.startTask(taskId);
        return ResponseEntity.ok(task);
    }

    @PostMapping("/tasks/{taskId}/complete")
    @Operation(summary = "Complete task")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'HOUSEKEEPING')")
    public ResponseEntity<HousekeepingTaskResponse> completeTask(
            @PathVariable Long taskId,
            @RequestParam(required = false) String completionNotes) {
        HousekeepingTaskResponse task = housekeepingService.completeTask(taskId, completionNotes);
        return ResponseEntity.ok(task);
    }

    @PostMapping("/tasks/{taskId}/cancel")
    @Operation(summary = "Cancel task")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<HousekeepingTaskResponse> cancelTask(
            @PathVariable Long taskId,
            @RequestParam String reason) {
        HousekeepingTaskResponse task = housekeepingService.cancelTask(taskId, reason);
        return ResponseEntity.ok(task);
    }

    @DeleteMapping("/tasks/{id}")
    @Operation(summary = "Delete task")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        housekeepingService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
