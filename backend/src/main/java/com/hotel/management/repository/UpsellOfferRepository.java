package com.hotel.management.repository;

import com.hotel.management.entity.UpsellOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface UpsellOfferRepository extends JpaRepository<UpsellOffer, Long> {
    
    @Query("SELECT uo FROM UpsellOffer uo WHERE uo.isActive = true AND " +
           "(uo.validFrom IS NULL OR uo.validFrom <= :date) AND " +
           "(uo.validTo IS NULL OR uo.validTo >= :date) AND " +
           "(uo.maxUses IS NULL OR uo.usedCount < uo.maxUses) " +
           "ORDER BY uo.displayPriority DESC")
    List<UpsellOffer> findActiveOffers(LocalDate date);
    
    @Query("SELECT uo FROM UpsellOffer uo WHERE uo.sourceRoomType.id = :roomTypeId AND uo.isActive = true " +
           "AND uo.offerType = 'ROOM_UPGRADE' " +
           "ORDER BY uo.displayPriority DESC")
    List<UpsellOffer> findRoomUpgradeOffers(Long roomTypeId);
    
    @Query("SELECT uo FROM UpsellOffer uo WHERE uo.offerType = :offerType AND uo.isActive = true " +
           "ORDER BY uo.displayPriority DESC")
    List<UpsellOffer> findByOfferType(String offerType);
}
