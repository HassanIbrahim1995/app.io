package com.hotel.management.repository;

import com.hotel.management.entity.PolicyAcceptance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PolicyAcceptanceRepository extends JpaRepository<PolicyAcceptance, Long> {
    
    List<PolicyAcceptance> findByUserId(Long userId);
    
    @Query("SELECT pa FROM PolicyAcceptance pa WHERE pa.user.id = :userId AND pa.policy.id = :policyId")
    PolicyAcceptance findByUserAndPolicy(Long userId, Long policyId);
    
    @Query("SELECT pa FROM PolicyAcceptance pa WHERE pa.policy.id = :policyId")
    List<PolicyAcceptance> findByPolicyId(Long policyId);
    
    @Query("SELECT COUNT(pa) FROM PolicyAcceptance pa WHERE pa.user.id = :userId")
    Long countByUserId(Long userId);
}
