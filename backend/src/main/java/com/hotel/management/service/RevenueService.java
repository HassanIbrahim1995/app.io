package com.hotel.management.service;

import com.hotel.management.dto.response.RevenueResponse;
import com.hotel.management.entity.Revenue;
import com.hotel.management.entity.enums.RevenueStatus;
import com.hotel.management.entity.enums.RevenueType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface RevenueService {
    Revenue createRevenue(Revenue revenue);
    
    RevenueResponse getRevenueById(Long id);
    
    Page<RevenueResponse> getAllRevenues(Pageable pageable);
    
    Page<RevenueResponse> getRevenuesByType(RevenueType type, Pageable pageable);
    
    Page<RevenueResponse> getRevenuesByStatus(RevenueStatus status, Pageable pageable);
    
    Page<RevenueResponse> getRevenuesByDepartment(String department, Pageable pageable);
    
    List<RevenueResponse> getRevenuesByReservation(Long reservationId);
    
    List<RevenueResponse> getRevenuesByGuest(Long guestId);
    
    List<RevenueResponse> getRevenuesByDateRange(LocalDate startDate, LocalDate endDate);
    
    void updateRevenueStatus(Long id, RevenueStatus status);
    
    void postRevenueToGL(Long id);
    
    // Analytics methods
    BigDecimal getTotalRevenue(LocalDate startDate, LocalDate endDate);
    
    BigDecimal getNetRevenue(LocalDate startDate, LocalDate endDate);
    
    BigDecimal getRevenueByType(RevenueType type, LocalDate startDate, LocalDate endDate);
    
    BigDecimal getRevenueByDepartment(String department, LocalDate startDate, LocalDate endDate);
    
    Map<RevenueType, BigDecimal> getRevenueBreakdownByType(LocalDate startDate, LocalDate endDate);
    
    Map<String, BigDecimal> getRevenueBreakdownByDepartment(LocalDate startDate, LocalDate endDate);
    
    Map<LocalDate, BigDecimal> getDailyRevenue(LocalDate startDate, LocalDate endDate);
    
    Map<String, BigDecimal> getMonthlyRevenue(LocalDate startDate, LocalDate endDate);
    
    BigDecimal getPendingRevenue();
    
    List<RevenueResponse> getOverduePendingRevenue(LocalDate asOfDate);
    
    // Revenue from specific sources
    void recordRoomRevenue(Long reservationId, Long roomId, Long guestId, BigDecimal amount, LocalDate date);
    
    void recordServiceRevenue(RevenueType serviceType, Long guestId, Long roomId, BigDecimal amount, String description);
    
    void recordProductRevenue(Long productId, Long guestId, BigDecimal amount, Integer quantity);
    
    void recordRefund(Long originalRevenueId, BigDecimal refundAmount, String reason);
}
