package com.hotel.management.controller;

import com.hotel.management.entity.AuditLog;
import com.hotel.management.entity.enums.AuditAction;
import com.hotel.management.entity.enums.EntityType;
import com.hotel.management.service.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/audit-logs")
@RequiredArgsConstructor
@Tag(name = "Audit Logs", description = "Audit log management and viewing endpoints")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    @Operation(summary = "Get all audit logs (paginated)")
    public ResponseEntity<Page<AuditLog>> getAuditLogs(
            @PageableDefault(size = 50, sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<AuditLog> logs = auditLogService.getAuditLogs(pageable);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get audit logs by user")
    public ResponseEntity<Page<AuditLog>> getAuditLogsByUser(
            @PathVariable Long userId,
            @PageableDefault(size = 50, sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<AuditLog> logs = auditLogService.getAuditLogsByUser(userId, pageable);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/action/{action}")
    @Operation(summary = "Get audit logs by action type")
    public ResponseEntity<Page<AuditLog>> getAuditLogsByAction(
            @PathVariable AuditAction action,
            @PageableDefault(size = 50, sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<AuditLog> logs = auditLogService.getAuditLogsByAction(action, pageable);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/entity/{entityType}/{entityId}")
    @Operation(summary = "Get audit logs by entity")
    public ResponseEntity<Page<AuditLog>> getAuditLogsByEntity(
            @PathVariable EntityType entityType,
            @PathVariable Long entityId,
            @PageableDefault(size = 50, sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<AuditLog> logs = auditLogService.getAuditLogsByEntity(entityType, entityId, pageable);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/date-range")
    @Operation(summary = "Get audit logs by date range")
    public ResponseEntity<Page<AuditLog>> getAuditLogsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @PageableDefault(size = 50, sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<AuditLog> logs = auditLogService.getAuditLogsByDateRange(start, end, pageable);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/failed")
    @Operation(summary = "Get failed actions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<AuditLog>> getFailedActions(
            @PageableDefault(size = 50, sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<AuditLog> logs = auditLogService.getFailedActions(pageable);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/statistics/actions")
    @Operation(summary = "Get action statistics")
    public ResponseEntity<Map<String, Long>> getActionStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        Map<String, Long> statistics = auditLogService.getActionStatistics(start, end);
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/statistics/user-roles")
    @Operation(summary = "Get user role statistics")
    public ResponseEntity<Map<String, Long>> getUserRoleStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        Map<String, Long> statistics = auditLogService.getUserRoleStatistics(start, end);
        return ResponseEntity.ok(statistics);
    }
}
