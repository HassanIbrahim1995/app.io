package com.hotel.management.repository;

import com.hotel.management.entity.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GuestRepository extends JpaRepository<Guest, Long>, JpaSpecificationExecutor<Guest> {
    
    Optional<Guest> findByUserId(Long userId);
    
    @Query("SELECT g FROM Guest g WHERE g.loyaltyTier = :tier")
    List<Guest> findByLoyaltyTier(String tier);
    
    @Query("SELECT g FROM Guest g WHERE g.loyaltyPoints >= :minPoints ORDER BY g.loyaltyPoints DESC")
    List<Guest> findTopLoyaltyGuests(Integer minPoints);
}
