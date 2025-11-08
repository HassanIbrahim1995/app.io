package com.hotel.management.service.impl;

import com.hotel.management.dto.request.PromotionRequest;
import com.hotel.management.dto.response.PromotionResponse;
import com.hotel.management.entity.Promotion;
import com.hotel.management.exception.BadRequestException;
import com.hotel.management.exception.ResourceNotFoundException;
import com.hotel.management.repository.PromotionRepository;
import com.hotel.management.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;

    @Override
    @Transactional
    public PromotionResponse createPromotion(PromotionRequest request) {
        // Validate dates
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BadRequestException("End date must be after start date");
        }

        // Check if promo code already exists
        if (promotionRepository.findByCode(request.getCode()).isPresent()) {
            throw new BadRequestException("Promotion code already exists");
        }

        Promotion promotion = Promotion.builder()
                .code(request.getCode().toUpperCase())
                .name(request.getName())
                .description(request.getDescription())
                .discountType(request.getDiscountType())
                .discountValue(request.getDiscountValue())
                .maxDiscount(request.getMaxDiscount())
                .minBookingAmount(request.getMinBookingAmount())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .usageLimit(request.getUsageLimit())
                .usedCount(0)
                .perUserLimit(request.getPerUserLimit())
                .isActive(request.getIsActive())
                .requiresAuthentication(request.getRequiresAuthentication())
                .minNights(request.getMinNights())
                .applicableRoomTypeId(request.getApplicableRoomTypeId())
                .applicableDays(request.getApplicableDays())
                .build();

        promotion = promotionRepository.save(promotion);
        return mapToResponse(promotion);
    }

    @Override
    @Transactional
    public PromotionResponse updatePromotion(Long id, PromotionRequest request) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion", "id", id));

        // Validate dates
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BadRequestException("End date must be after start date");
        }

        promotion.setName(request.getName());
        promotion.setDescription(request.getDescription());
        promotion.setDiscountType(request.getDiscountType());
        promotion.setDiscountValue(request.getDiscountValue());
        promotion.setMaxDiscount(request.getMaxDiscount());
        promotion.setMinBookingAmount(request.getMinBookingAmount());
        promotion.setStartDate(request.getStartDate());
        promotion.setEndDate(request.getEndDate());
        promotion.setUsageLimit(request.getUsageLimit());
        promotion.setPerUserLimit(request.getPerUserLimit());
        promotion.setIsActive(request.getIsActive());
        promotion.setRequiresAuthentication(request.getRequiresAuthentication());
        promotion.setMinNights(request.getMinNights());
        promotion.setApplicableRoomTypeId(request.getApplicableRoomTypeId());
        promotion.setApplicableDays(request.getApplicableDays());

        promotion = promotionRepository.save(promotion);
        return mapToResponse(promotion);
    }

    @Override
    public PromotionResponse getPromotionById(Long id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion", "id", id));
        return mapToResponse(promotion);
    }

    @Override
    public PromotionResponse getPromotionByCode(String code) {
        Promotion promotion = promotionRepository.findByCode(code.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Promotion", "code", code));
        return mapToResponse(promotion);
    }

    @Override
    public List<PromotionResponse> getAllPromotions() {
        return promotionRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PromotionResponse> getActivePromotions() {
        LocalDate today = LocalDate.now();
        return promotionRepository.findActivePromotions(today).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PromotionResponse> getPromotionsByDateRange(LocalDate startDate, LocalDate endDate) {
        return promotionRepository.findByDateRange(startDate, endDate).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deletePromotion(Long id) {
        if (!promotionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Promotion", "id", id);
        }
        promotionRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deactivatePromotion(Long id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion", "id", id));
        promotion.setIsActive(false);
        promotionRepository.save(promotion);
    }

    @Override
    public boolean validatePromotionCode(String code, Long userId) {
        Promotion promotion = promotionRepository.findByCode(code.toUpperCase())
                .orElse(null);

        if (promotion == null) {
            return false;
        }

        LocalDate today = LocalDate.now();

        // Check if active
        if (!promotion.getIsActive()) {
            return false;
        }

        // Check date range
        if (today.isBefore(promotion.getStartDate()) || today.isAfter(promotion.getEndDate())) {
            return false;
        }

        // Check usage limit
        if (promotion.getUsageLimit() != null && 
            promotion.getUsedCount() >= promotion.getUsageLimit()) {
            return false;
        }

        // Check authentication requirement
        if (promotion.getRequiresAuthentication() && userId == null) {
            return false;
        }

        return true;
    }

    private PromotionResponse mapToResponse(Promotion promotion) {
        return PromotionResponse.builder()
                .id(promotion.getId())
                .code(promotion.getCode())
                .name(promotion.getName())
                .description(promotion.getDescription())
                .discountType(promotion.getDiscountType())
                .discountValue(promotion.getDiscountValue())
                .maxDiscount(promotion.getMaxDiscount())
                .minBookingAmount(promotion.getMinBookingAmount())
                .startDate(promotion.getStartDate())
                .endDate(promotion.getEndDate())
                .usageLimit(promotion.getUsageLimit())
                .usedCount(promotion.getUsedCount())
                .perUserLimit(promotion.getPerUserLimit())
                .isActive(promotion.getIsActive())
                .requiresAuthentication(promotion.getRequiresAuthentication())
                .minNights(promotion.getMinNights())
                .applicableRoomTypeId(promotion.getApplicableRoomTypeId())
                .applicableDays(promotion.getApplicableDays())
                .build();
    }
}
