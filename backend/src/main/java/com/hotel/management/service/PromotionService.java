package com.hotel.management.service;

import com.hotel.management.dto.request.PromotionRequest;
import com.hotel.management.dto.response.PromotionResponse;

import java.time.LocalDate;
import java.util.List;

public interface PromotionService {
    PromotionResponse createPromotion(PromotionRequest request);
    PromotionResponse updatePromotion(Long id, PromotionRequest request);
    PromotionResponse getPromotionById(Long id);
    PromotionResponse getPromotionByCode(String code);
    List<PromotionResponse> getAllPromotions();
    List<PromotionResponse> getActivePromotions();
    List<PromotionResponse> getPromotionsByDateRange(LocalDate startDate, LocalDate endDate);
    void deletePromotion(Long id);
    void deactivatePromotion(Long id);
    boolean validatePromotionCode(String code, Long userId);
}
