package com.hotel.management.controller;

import com.hotel.management.dto.response.RevenueResponse;
import com.hotel.management.entity.enums.RevenueStatus;
import com.hotel.management.entity.enums.RevenueType;
import com.hotel.management.service.RevenueService;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/revenues")
@RequiredArgsConstructor
@Tag(name = "Revenue", description = "Revenue management and analytics endpoints")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'FRONT_DESK')")
public class RevenueController {

    private final RevenueService revenueService;

    @GetMapping
    @Operation(summary = "Get all revenues")
    public ResponseEntity<Page<RevenueResponse>> getAllRevenues(
            @PageableDefault(size = 50, sort = "revenueDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<RevenueResponse> revenues = revenueService.getAllRevenues(pageable);
        return ResponseEntity.ok(revenues);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get revenue by ID")
    public ResponseEntity<RevenueResponse> getRevenueById(@PathVariable Long id) {
        RevenueResponse revenue = revenueService.getRevenueById(id);
        return ResponseEntity.ok(revenue);
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Get revenues by type")
    public ResponseEntity<Page<RevenueResponse>> getRevenuesByType(
            @PathVariable RevenueType type,
            @PageableDefault(size = 50) Pageable pageable) {
        Page<RevenueResponse> revenues = revenueService.getRevenuesByType(type, pageable);
        return ResponseEntity.ok(revenues);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get revenues by status")
    public ResponseEntity<Page<RevenueResponse>> getRevenuesByStatus(
            @PathVariable RevenueStatus status,
            @PageableDefault(size = 50) Pageable pageable) {
        Page<RevenueResponse> revenues = revenueService.getRevenuesByStatus(status, pageable);
        return ResponseEntity.ok(revenues);
    }

    @GetMapping("/department/{department}")
    @Operation(summary = "Get revenues by department")
    public ResponseEntity<Page<RevenueResponse>> getRevenuesByDepartment(
            @PathVariable String department,
            @PageableDefault(size = 50) Pageable pageable) {
        Page<RevenueResponse> revenues = revenueService.getRevenuesByDepartment(department, pageable);
        return ResponseEntity.ok(revenues);
    }

    @GetMapping("/reservation/{reservationId}")
    @Operation(summary = "Get revenues by reservation")
    public ResponseEntity<List<RevenueResponse>> getRevenuesByReservation(@PathVariable Long reservationId) {
        List<RevenueResponse> revenues = revenueService.getRevenuesByReservation(reservationId);
        return ResponseEntity.ok(revenues);
    }

    @GetMapping("/guest/{guestId}")
    @Operation(summary = "Get revenues by guest")
    public ResponseEntity<List<RevenueResponse>> getRevenuesByGuest(@PathVariable Long guestId) {
        List<RevenueResponse> revenues = revenueService.getRevenuesByGuest(guestId);
        return ResponseEntity.ok(revenues);
    }

    @GetMapping("/date-range")
    @Operation(summary = "Get revenues by date range")
    public ResponseEntity<List<RevenueResponse>> getRevenuesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<RevenueResponse> revenues = revenueService.getRevenuesByDateRange(startDate, endDate);
        return ResponseEntity.ok(revenues);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update revenue status")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> updateRevenueStatus(
            @PathVariable Long id,
            @RequestParam RevenueStatus status) {
        revenueService.updateRevenueStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/post-to-gl")
    @Operation(summary = "Post revenue to General Ledger")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> postRevenueToGL(@PathVariable Long id) {
        revenueService.postRevenueToGL(id);
        return ResponseEntity.ok().build();
    }

    // Analytics endpoints
    @GetMapping("/analytics/total")
    @Operation(summary = "Get total revenue")
    public ResponseEntity<BigDecimal> getTotalRevenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        BigDecimal total = revenueService.getTotalRevenue(startDate, endDate);
        return ResponseEntity.ok(total);
    }

    @GetMapping("/analytics/net")
    @Operation(summary = "Get net revenue")
    public ResponseEntity<BigDecimal> getNetRevenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        BigDecimal net = revenueService.getNetRevenue(startDate, endDate);
        return ResponseEntity.ok(net);
    }

    @GetMapping("/analytics/by-type")
    @Operation(summary = "Get revenue breakdown by type")
    public ResponseEntity<Map<RevenueType, BigDecimal>> getRevenueBreakdownByType(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Map<RevenueType, BigDecimal> breakdown = revenueService.getRevenueBreakdownByType(startDate, endDate);
        return ResponseEntity.ok(breakdown);
    }

    @GetMapping("/analytics/by-department")
    @Operation(summary = "Get revenue breakdown by department")
    public ResponseEntity<Map<String, BigDecimal>> getRevenueBreakdownByDepartment(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Map<String, BigDecimal> breakdown = revenueService.getRevenueBreakdownByDepartment(startDate, endDate);
        return ResponseEntity.ok(breakdown);
    }

    @GetMapping("/analytics/daily")
    @Operation(summary = "Get daily revenue")
    public ResponseEntity<Map<LocalDate, BigDecimal>> getDailyRevenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Map<LocalDate, BigDecimal> daily = revenueService.getDailyRevenue(startDate, endDate);
        return ResponseEntity.ok(daily);
    }

    @GetMapping("/analytics/monthly")
    @Operation(summary = "Get monthly revenue")
    public ResponseEntity<Map<String, BigDecimal>> getMonthlyRevenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Map<String, BigDecimal> monthly = revenueService.getMonthlyRevenue(startDate, endDate);
        return ResponseEntity.ok(monthly);
    }

    @GetMapping("/analytics/pending")
    @Operation(summary = "Get pending revenue")
    public ResponseEntity<BigDecimal> getPendingRevenue() {
        BigDecimal pending = revenueService.getPendingRevenue();
        return ResponseEntity.ok(pending);
    }

    @GetMapping("/analytics/overdue")
    @Operation(summary = "Get overdue pending revenue")
    public ResponseEntity<List<RevenueResponse>> getOverduePendingRevenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOfDate) {
        List<RevenueResponse> overdue = revenueService.getOverduePendingRevenue(asOfDate);
        return ResponseEntity.ok(overdue);
    }
}
