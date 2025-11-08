package com.hotel.management.repository;

import com.hotel.management.entity.Revenue;
import com.hotel.management.entity.enums.RevenueStatus;
import com.hotel.management.entity.enums.RevenueType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface RevenueRepository extends JpaRepository<Revenue, Long> {
    
    // Basic queries
    Page<Revenue> findByRevenueType(RevenueType revenueType, Pageable pageable);
    
    Page<Revenue> findByStatus(RevenueStatus status, Pageable pageable);
    
    Page<Revenue> findByDepartment(String department, Pageable pageable);
    
    List<Revenue> findByReservationId(Long reservationId);
    
    List<Revenue> findByGuestId(Long guestId);
    
    // Date range queries
    @Query("SELECT r FROM Revenue r WHERE r.revenueDate BETWEEN :startDate AND :endDate")
    List<Revenue> findByDateRange(LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT r FROM Revenue r WHERE r.revenueDate BETWEEN :startDate AND :endDate AND r.status = :status")
    List<Revenue> findByDateRangeAndStatus(LocalDate startDate, LocalDate endDate, RevenueStatus status);
    
    // Sum queries
    @Query("SELECT SUM(r.amount) FROM Revenue r WHERE r.revenueDate BETWEEN :startDate AND :endDate AND r.status = 'COLLECTED'")
    BigDecimal sumCollectedRevenueBetweenDates(LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT SUM(r.netAmount) FROM Revenue r WHERE r.revenueDate BETWEEN :startDate AND :endDate AND r.status = 'COLLECTED'")
    BigDecimal sumNetRevenueBetweenDates(LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT SUM(r.amount) FROM Revenue r WHERE r.revenueType = :type AND r.revenueDate BETWEEN :startDate AND :endDate AND r.status = 'COLLECTED'")
    BigDecimal sumRevenueByTypeBetweenDates(RevenueType type, LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT SUM(r.amount) FROM Revenue r WHERE r.department = :department AND r.revenueDate BETWEEN :startDate AND :endDate AND r.status = 'COLLECTED'")
    BigDecimal sumRevenueByDepartmentBetweenDates(String department, LocalDate startDate, LocalDate endDate);
    
    // Group by queries
    @Query("SELECT r.revenueType, SUM(r.amount) FROM Revenue r WHERE r.revenueDate BETWEEN :startDate AND :endDate AND r.status = 'COLLECTED' GROUP BY r.revenueType")
    List<Object[]> sumRevenueByTypeGrouped(LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT r.department, SUM(r.amount) FROM Revenue r WHERE r.revenueDate BETWEEN :startDate AND :endDate AND r.status = 'COLLECTED' GROUP BY r.department")
    List<Object[]> sumRevenueByDepartmentGrouped(LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT DATE(r.revenueDate), SUM(r.amount) FROM Revenue r WHERE r.revenueDate BETWEEN :startDate AND :endDate AND r.status = 'COLLECTED' GROUP BY DATE(r.revenueDate) ORDER BY DATE(r.revenueDate)")
    List<Object[]> sumRevenueDailyGrouped(LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT YEAR(r.revenueDate), MONTH(r.revenueDate), SUM(r.amount) FROM Revenue r WHERE r.revenueDate BETWEEN :startDate AND :endDate AND r.status = 'COLLECTED' GROUP BY YEAR(r.revenueDate), MONTH(r.revenueDate) ORDER BY YEAR(r.revenueDate), MONTH(r.revenueDate)")
    List<Object[]> sumRevenueMonthlyGrouped(LocalDate startDate, LocalDate endDate);
    
    // Average queries
    @Query("SELECT AVG(r.amount) FROM Revenue r WHERE r.revenueType = :type AND r.revenueDate BETWEEN :startDate AND :endDate AND r.status = 'COLLECTED'")
    BigDecimal averageRevenueByType(RevenueType type, LocalDate startDate, LocalDate endDate);
    
    // Count queries
    @Query("SELECT COUNT(r) FROM Revenue r WHERE r.revenueDate BETWEEN :startDate AND :endDate")
    Long countRevenueTransactions(LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT r.status, COUNT(r) FROM Revenue r WHERE r.revenueDate BETWEEN :startDate AND :endDate GROUP BY r.status")
    List<Object[]> countByStatusGrouped(LocalDate startDate, LocalDate endDate);
    
    // Top revenue queries
    @Query("SELECT r.guest, SUM(r.amount) as total FROM Revenue r WHERE r.revenueDate BETWEEN :startDate AND :endDate AND r.status = 'COLLECTED' GROUP BY r.guest ORDER BY total DESC")
    List<Object[]> findTopRevenueGuests(LocalDate startDate, LocalDate endDate, Pageable pageable);
    
    @Query("SELECT r.room, SUM(r.amount) as total FROM Revenue r WHERE r.revenueDate BETWEEN :startDate AND :endDate AND r.status = 'COLLECTED' GROUP BY r.room ORDER BY total DESC")
    List<Object[]> findTopRevenueRooms(LocalDate startDate, LocalDate endDate, Pageable pageable);
    
    // Pending/Outstanding revenue
    @Query("SELECT SUM(r.amount) FROM Revenue r WHERE r.status = 'PENDING'")
    BigDecimal sumPendingRevenue();
    
    @Query("SELECT r FROM Revenue r WHERE r.status = 'PENDING' AND r.revenueDate < :date")
    List<Revenue> findOverduePendingRevenue(LocalDate date);
}
