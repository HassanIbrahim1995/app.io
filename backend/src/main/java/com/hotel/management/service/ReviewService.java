package com.hotel.management.service;

import com.hotel.management.dto.request.ReviewRequest;
import com.hotel.management.dto.response.ReviewResponse;
import com.hotel.management.entity.User;

import java.util.List;

public interface ReviewService {
    ReviewResponse createReview(ReviewRequest request, User user);
    ReviewResponse updateReview(Long id, ReviewRequest request, User user);
    ReviewResponse getReviewById(Long id);
    List<ReviewResponse> getAllReviews();
    List<ReviewResponse> getReviewsByRoom(Long roomId);
    List<ReviewResponse> getReviewsByGuest(Long guestId);
    List<ReviewResponse> getReviewsByRating(Integer rating);
    Double getAverageRatingByRoom(Long roomId);
    void deleteReview(Long id, User user);
    ReviewResponse addManagementResponse(Long id, String response, User user);
}
