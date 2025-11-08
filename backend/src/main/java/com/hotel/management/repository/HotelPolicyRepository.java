package com.hotel.management.repository;

import com.hotel.management.entity.HotelPolicy;
import com.hotel.management.entity.enums.PolicyCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface HotelPolicyRepository extends JpaRepository<HotelPolicy, Long> {
    
    Optional<HotelPolicy> findByPolicyCode(String policyCode);
    
    List<HotelPolicy> findByCategory(PolicyCategory category);
    
    @Query("SELECT p FROM HotelPolicy p WHERE p.isActive = true ORDER BY p.displayOrder, p.category")
    List<HotelPolicy> findAllActive();
    
    @Query("SELECT p FROM HotelPolicy p WHERE p.isActive = true AND " +
           "(p.effectiveDate IS NULL OR p.effectiveDate <= :date) AND " +
           "(p.expiryDate IS NULL OR p.expiryDate >= :date) " +
           "ORDER BY p.displayOrder")
    List<HotelPolicy> findEffectivePolicies(LocalDate date);
    
    @Query("SELECT p FROM HotelPolicy p WHERE p.requiresAcceptance = true AND p.isActive = true " +
           "AND (p.appliesTo = 'ALL' OR p.appliesTo = :appliesTo)")
    List<HotelPolicy> findPoliciesRequiringAcceptance(String appliesTo);
    
    List<HotelPolicy> findByLanguageCode(String languageCode);
}
