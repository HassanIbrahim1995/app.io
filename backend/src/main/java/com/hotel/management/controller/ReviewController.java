package com.hotel.management.controller;

import com.hotel.management.dto.request.ReviewRequest;
import com.hotel.management.dto.response.ReviewResponse;
import com.hotel.management.entity.User;
import com.hotel.management.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "Review management endpoints")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @Operation(summary = "Create a review")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('GUEST')")
    public ResponseEntity<ReviewResponse> createReview(
            @Valid @RequestBody ReviewRequest request,
            @AuthenticationPrincipal User user) {
        ReviewResponse response = reviewService.createReview(request, user);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a review")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('GUEST')")
    public ResponseEntity<ReviewResponse> updateReview(
            @PathVariable Long id,
            @Valid @RequestBody ReviewRequest request,
            @AuthenticationPrincipal User user) {
        ReviewResponse response = reviewService.updateReview(id, request, user);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get review by ID")
    public ResponseEntity<ReviewResponse> getReviewById(@PathVariable Long id) {
        ReviewResponse response = reviewService.getReviewById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all reviews")
    public ResponseEntity<List<ReviewResponse>> getAllReviews() {
        List<ReviewResponse> reviews = reviewService.getAllReviews();
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/room/{roomId}")
    @Operation(summary = "Get reviews by room")
    public ResponseEntity<List<ReviewResponse>> getReviewsByRoom(@PathVariable Long roomId) {
        List<ReviewResponse> reviews = reviewService.getReviewsByRoom(roomId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/guest/{guestId}")
    @Operation(summary = "Get reviews by guest")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<ReviewResponse>> getReviewsByGuest(@PathVariable Long guestId) {
        List<ReviewResponse> reviews = reviewService.getReviewsByGuest(guestId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/rating/{rating}")
    @Operation(summary = "Get reviews by rating")
    public ResponseEntity<List<ReviewResponse>> getReviewsByRating(@PathVariable Integer rating) {
        List<ReviewResponse> reviews = reviewService.getReviewsByRating(rating);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/room/{roomId}/average")
    @Operation(summary = "Get average rating for a room")
    public ResponseEntity<Double> getAverageRatingByRoom(@PathVariable Long roomId) {
        Double average = reviewService.getAverageRatingByRoom(roomId);
        return ResponseEntity.ok(average);
    }

    @PostMapping("/{id}/respond")
    @Operation(summary = "Add management response to review")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ReviewResponse> addManagementResponse(
            @PathVariable Long id,
            @RequestParam String response,
            @AuthenticationPrincipal User user) {
        ReviewResponse reviewResponse = reviewService.addManagementResponse(id, response, user);
        return ResponseEntity.ok(reviewResponse);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a review")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        reviewService.deleteReview(id, user);
        return ResponseEntity.noContent().build();
    }
}
