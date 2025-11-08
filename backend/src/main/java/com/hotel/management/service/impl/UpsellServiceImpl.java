package com.hotel.management.service.impl;

import com.hotel.management.entity.Reservation;
import com.hotel.management.entity.UpsellOffer;
import com.hotel.management.exception.BadRequestException;
import com.hotel.management.exception.ResourceNotFoundException;
import com.hotel.management.repository.ReservationRepository;
import com.hotel.management.repository.UpsellOfferRepository;
import com.hotel.management.service.UpsellService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UpsellServiceImpl implements UpsellService {

    private final UpsellOfferRepository upsellOfferRepository;
    private final ReservationRepository reservationRepository;

    @Override
    public List<UpsellOffer> getActiveOffers() {
        return upsellOfferRepository.findActiveOffers(LocalDate.now());
    }

    @Override
    public List<UpsellOffer> getRoomUpgradeOffers(Long roomTypeId) {
        return upsellOfferRepository.findRoomUpgradeOffers(roomTypeId);
    }

    @Override
    public List<UpsellOffer> getRecommendationsForReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", "id", reservationId));

        List<UpsellOffer> recommendations = new ArrayList<>();

        // Room upgrade offers
        Long currentRoomTypeId = reservation.getRoom().getRoomType().getId();
        recommendations.addAll(getRoomUpgradeOffers(currentRoomTypeId));

        // Product and service offers
        recommendations.addAll(
            upsellOfferRepository.findByOfferType("PRODUCT").stream()
                    .limit(5)
                    .collect(Collectors.toList())
        );

        recommendations.addAll(
            upsellOfferRepository.findByOfferType("SERVICE").stream()
                    .limit(3)
                    .collect(Collectors.toList())
        );

        return recommendations;
    }

    @Override
    @Transactional
    public UpsellOffer acceptOffer(Long offerId, Long reservationId) {
        UpsellOffer offer = upsellOfferRepository.findById(offerId)
                .orElseThrow(() -> new ResourceNotFoundException("UpsellOffer", "id", offerId));

        if (!offer.getIsActive()) {
            throw new BadRequestException("This offer is no longer active");
        }

        if (offer.getMaxUses() != null && offer.getUsedCount() >= offer.getMaxUses()) {
            throw new BadRequestException("This offer has reached its maximum usage limit");
        }

        // Increment used count
        offer.setUsedCount(offer.getUsedCount() + 1);
        
        return upsellOfferRepository.save(offer);
    }
}
