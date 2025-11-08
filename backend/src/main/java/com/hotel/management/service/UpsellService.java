package com.hotel.management.service;

import com.hotel.management.entity.UpsellOffer;

import java.util.List;

public interface UpsellService {
    List<UpsellOffer> getActiveOffers();
    List<UpsellOffer> getRoomUpgradeOffers(Long roomTypeId);
    List<UpsellOffer> getRecommendationsForReservation(Long reservationId);
    UpsellOffer acceptOffer(Long offerId, Long reservationId);
}
