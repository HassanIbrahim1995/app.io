package com.hotel.management.service.impl;

import com.hotel.management.dto.response.PricingBreakdown;
import com.hotel.management.entity.*;
import com.hotel.management.exception.ResourceNotFoundException;
import com.hotel.management.repository.*;
import com.hotel.management.service.PricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PricingServiceImpl implements PricingService {

    private final RoomRepository roomRepository;
    private final PromotionRepository promotionRepository;
    private final CityTaxRepository cityTaxRepository;
    private final ExtraFeeRepository extraFeeRepository;
    private final RoomTypeRepository roomTypeRepository;

    @Override
    public PricingBreakdown calculateReservationPricing(
            Long roomId,
            LocalDate checkIn,
            LocalDate checkOut,
            Integer numberOfGuests,
            String city,
            String promoCode) {

        // Get room
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room", "id", roomId));

        // Calculate number of nights
        long numberOfNights = ChronoUnit.DAYS.between(checkIn, checkOut);
        
        // Calculate base price
        BigDecimal roomBasePrice = room.getBasePrice();
        BigDecimal subtotal = roomBasePrice.multiply(BigDecimal.valueOf(numberOfNights));

        // Apply discount if promo code provided
        BigDecimal discountAmount = BigDecimal.ZERO;
        String discountDescription = null;
        if (promoCode != null && !promoCode.isEmpty()) {
            Promotion promotion = promotionRepository
                    .findValidPromotionByCode(promoCode, LocalDate.now())
                    .orElse(null);
            
            if (promotion != null) {
                discountAmount = calculateDiscount(subtotal, promotion);
                discountDescription = promotion.getName();
            }
        }

        BigDecimal discountedSubtotal = subtotal.subtract(discountAmount);

        // Calculate standard tax (10%)
        BigDecimal standardTax = discountedSubtotal.multiply(BigDecimal.valueOf(0.10))
                .setScale(2, RoundingMode.HALF_UP);

        // Calculate city tax
        List<PricingBreakdown.TaxDetail> taxDetails = new ArrayList<>();
        BigDecimal cityTaxAmount = BigDecimal.ZERO;

        if (city != null) {
            List<CityTax> cityTaxes = cityTaxRepository.findActiveTaxesByCity(city, LocalDate.now());
            for (CityTax tax : cityTaxes) {
                BigDecimal taxAmount = calculateCityTax(tax, discountedSubtotal, numberOfNights, numberOfGuests);
                cityTaxAmount = cityTaxAmount.add(taxAmount);
                
                taxDetails.add(PricingBreakdown.TaxDetail.builder()
                        .taxName(tax.getTaxName())
                        .rate(tax.getTaxRate())
                        .amount(taxAmount)
                        .calculationType(tax.getCalculationType())
                        .build());
            }
        }

        BigDecimal totalTax = standardTax.add(cityTaxAmount);

        // Get applicable extra fees
        List<PricingBreakdown.FeeDetail> feeDetails = new ArrayList<>();
        BigDecimal totalFees = BigDecimal.ZERO;

        List<ExtraFee> extraFees = extraFeeRepository.findMandatoryFees();
        for (ExtraFee fee : extraFees) {
            if (isApplicableToRoom(fee, room.getRoomType().getId())) {
                BigDecimal feeAmount = calculateFee(fee, discountedSubtotal, numberOfNights, numberOfGuests);
                totalFees = totalFees.add(feeAmount);

                feeDetails.add(PricingBreakdown.FeeDetail.builder()
                        .feeName(fee.getFeeName())
                        .feeType(fee.getFeeType().name())
                        .amount(feeAmount)
                        .isMandatory(fee.getIsMandatory())
                        .isRefundable(fee.getIsRefundable())
                        .description(fee.getDescription())
                        .build());
            }
        }

        // Calculate grand total
        BigDecimal grandTotal = discountedSubtotal.add(totalTax).add(totalFees);

        return PricingBreakdown.builder()
                .roomBasePrice(roomBasePrice)
                .numberOfNights((int) numberOfNights)
                .subtotal(subtotal)
                .discountAmount(discountAmount)
                .discountDescription(discountDescription)
                .taxAmount(standardTax)
                .cityTaxAmount(cityTaxAmount)
                .taxes(taxDetails)
                .extraFees(feeDetails)
                .totalFees(totalFees)
                .grandTotal(grandTotal)
                .build();
    }

    @Override
    public PricingBreakdown recalculateReservationPricing(Reservation reservation) {
        return calculateReservationPricing(
                reservation.getRoom().getId(),
                reservation.getCheckInDate(),
                reservation.getCheckOutDate(),
                reservation.getNumberOfGuests(),
                reservation.getRoom().getRoomNumber(), // You might want to store city in reservation
                reservation.getPromoCode()
        );
    }

    private BigDecimal calculateDiscount(BigDecimal amount, Promotion promotion) {
        BigDecimal discount;
        switch (promotion.getDiscountType()) {
            case PERCENTAGE:
                discount = amount.multiply(promotion.getDiscountValue().divide(BigDecimal.valueOf(100)))
                        .setScale(2, RoundingMode.HALF_UP);
                break;
            case FIXED_AMOUNT:
                discount = promotion.getDiscountValue();
                break;
            default:
                discount = BigDecimal.ZERO;
        }

        if (promotion.getMaxDiscountAmount() != null && 
            discount.compareTo(promotion.getMaxDiscountAmount()) > 0) {
            discount = promotion.getMaxDiscountAmount();
        }

        return discount;
    }

    private BigDecimal calculateCityTax(CityTax tax, BigDecimal subtotal, long numberOfNights, int numberOfGuests) {
        BigDecimal taxAmount;

        switch (tax.getCalculationType()) {
            case "PERCENTAGE":
                taxAmount = subtotal.multiply(tax.getTaxRate().divide(BigDecimal.valueOf(100)))
                        .setScale(2, RoundingMode.HALF_UP);
                break;
            case "FIXED":
                taxAmount = tax.getFixedAmount() != null ? tax.getFixedAmount() : BigDecimal.ZERO;
                break;
            case "PER_NIGHT":
                taxAmount = (tax.getFixedAmount() != null ? tax.getFixedAmount() : BigDecimal.ZERO)
                        .multiply(BigDecimal.valueOf(numberOfNights));
                break;
            case "PER_PERSON":
                taxAmount = (tax.getFixedAmount() != null ? tax.getFixedAmount() : BigDecimal.ZERO)
                        .multiply(BigDecimal.valueOf(numberOfGuests))
                        .multiply(BigDecimal.valueOf(numberOfNights));
                break;
            default:
                taxAmount = BigDecimal.ZERO;
        }

        return taxAmount.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateFee(ExtraFee fee, BigDecimal subtotal, long numberOfNights, int numberOfGuests) {
        BigDecimal feeAmount;

        switch (fee.getCalculationBasis()) {
            case "PER_STAY":
                feeAmount = fee.getAmount();
                break;
            case "PER_NIGHT":
                feeAmount = fee.getAmount().multiply(BigDecimal.valueOf(numberOfNights));
                break;
            case "PER_PERSON":
                feeAmount = fee.getAmount().multiply(BigDecimal.valueOf(numberOfGuests));
                break;
            case "PERCENTAGE":
                feeAmount = subtotal.multiply(fee.getAmount().divide(BigDecimal.valueOf(100)))
                        .setScale(2, RoundingMode.HALF_UP);
                break;
            default:
                feeAmount = fee.getAmount();
        }

        return feeAmount.setScale(2, RoundingMode.HALF_UP);
    }

    private boolean isApplicableToRoom(ExtraFee fee, Long roomTypeId) {
        if (fee.getAppliesToRoomTypes() == null || fee.getAppliesToRoomTypes().isEmpty()) {
            return true;
        }

        String[] applicableTypes = fee.getAppliesToRoomTypes().split(",");
        for (String typeId : applicableTypes) {
            if (typeId.trim().equals(roomTypeId.toString())) {
                return true;
            }
        }

        return false;
    }
}
