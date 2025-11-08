package com.hotel.management.repository;

import com.hotel.management.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    
    Optional<Promotion> findByCode(String code);
    
    List<Promotion> findByIsActiveTrue();
    
    @Query("SELECT p FROM Promotion p WHERE p.code = :code AND p.isActive = true AND " +
           "p.startDate <= :currentDate AND p.endDate >= :currentDate")
    Optional<Promotion> findValidPromotionByCode(String code, LocalDate currentDate);
    
    @Query("SELECT p FROM Promotion p WHERE p.isActive = true AND " +
           "p.startDate <= :currentDate AND p.endDate >= :currentDate")
    List<Promotion> findActivePromotions(LocalDate currentDate);
}
