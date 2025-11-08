package com.hotel.management.repository;

import com.hotel.management.entity.RatePlan;
import com.hotel.management.entity.enums.RatePlanType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RatePlanRepository extends JpaRepository<RatePlan, Long> {
    
    Optional<RatePlan> findByPlanCode(String planCode);
    
    List<RatePlan> findByPlanType(RatePlanType planType);
    
    @Query("SELECT r FROM RatePlan r WHERE r.isActive = true AND r.isVisibleToPublic = true " +
           "ORDER BY r.displayOrder, r.basePriceModifier")
    List<RatePlan> findAllActiveAndPublic();
    
    @Query("SELECT r FROM RatePlan r WHERE r.isActive = true AND " +
           "(r.validFrom IS NULL OR r.validFrom <= :date) AND " +
           "(r.validTo IS NULL OR r.validTo >= :date)")
    List<RatePlan> findValidRatePlansForDate(LocalDate date);
    
    @Query("SELECT r FROM RatePlan r WHERE r.roomType.id = :roomTypeId AND r.isActive = true " +
           "ORDER BY r.displayOrder, r.basePriceModifier")
    List<RatePlan> findByRoomTypeId(Long roomTypeId);
    
    @Query("SELECT r FROM RatePlan r WHERE r.isRefundable = :isRefundable AND r.isActive = true")
    List<RatePlan> findByRefundability(Boolean isRefundable);
    
    @Query("SELECT r FROM RatePlan r WHERE r.planType = :planType AND " +
           "r.isActive = true AND r.isVisibleToPublic = true")
    List<RatePlan> findActiveByType(RatePlanType planType);
}
