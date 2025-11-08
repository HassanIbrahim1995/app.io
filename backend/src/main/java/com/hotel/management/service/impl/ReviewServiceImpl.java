package com.hotel.management.service.impl;

import com.hotel.management.dto.request.ReviewRequest;
import com.hotel.management.dto.response.ReviewResponse;
import com.hotel.management.entity.Guest;
import com.hotel.management.entity.Reservation;
import com.hotel.management.entity.Review;
import com.hotel.management.entity.User;
import com.hotel.management.entity.enums.ReservationStatus;
import com.hotel.management.entity.enums.UserRole;
import com.hotel.management.exception.BadRequestException;
import com.hotel.management.exception.ResourceNotFoundException;
import com.hotel.management.repository.GuestRepository;
import com.hotel.management.repository.ReservationRepository;
import com.hotel.management.repository.ReviewRepository;
import com.hotel.management.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;
    private final GuestRepository guestRepository;

    @Override
    @Transactional
    public ReviewResponse createReview(ReviewRequest request, User user) {
        // Get reservation
        Reservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", "id", request.getReservationId()));

        // Validate that the reservation belongs to the user
        Guest guest = guestRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Guest profile not found"));

        if (!reservation.getGuest().getId().equals(guest.getId())) {
            throw new BadRequestException("You can only review your own reservations");
        }

        // Validate that the reservation is completed or checked out
        if (reservation.getStatus() != ReservationStatus.CHECKED_OUT && 
            reservation.getStatus() != ReservationStatus.COMPLETED) {
            throw new BadRequestException("You can only review completed reservations");
        }

        // Check if review already exists
        if (reviewRepository.findByReservationId(request.getReservationId()).isPresent()) {
            throw new BadRequestException("You have already reviewed this reservation");
        }

        Review review = Review.builder()
                .reservation(reservation)
                .guest(guest)
                .room(reservation.getRoom())
                .overallRating(request.getOverallRating())
                .cleanlinessRating(request.getCleanlinessRating())
                .serviceRating(request.getServiceRating())
                .locationRating(request.getLocationRating())
                .valueRating(request.getValueRating())
                .comment(request.getComment())
                .title(request.getTitle())
                .wouldRecommend(request.getWouldRecommend())
                .isVerified(true) // Verified because linked to actual reservation
                .build();

        review = reviewRepository.save(review);
        return mapToResponse(review);
    }

    @Override
    @Transactional
    public ReviewResponse updateReview(Long id, ReviewRequest request, User user) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", id));

        // Validate ownership
        Guest guest = guestRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Guest profile not found"));

        if (!review.getGuest().getId().equals(guest.getId())) {
            throw new BadRequestException("You can only update your own reviews");
        }

        review.setOverallRating(request.getOverallRating());
        review.setCleanlinessRating(request.getCleanlinessRating());
        review.setServiceRating(request.getServiceRating());
        review.setLocationRating(request.getLocationRating());
        review.setValueRating(request.getValueRating());
        review.setComment(request.getComment());
        review.setTitle(request.getTitle());
        review.setWouldRecommend(request.getWouldRecommend());

        review = reviewRepository.save(review);
        return mapToResponse(review);
    }

    @Override
    public ReviewResponse getReviewById(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", id));
        return mapToResponse(review);
    }

    @Override
    public List<ReviewResponse> getAllReviews() {
        return reviewRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewResponse> getReviewsByRoom(Long roomId) {
        return reviewRepository.findByRoomId(roomId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewResponse> getReviewsByGuest(Long guestId) {
        return reviewRepository.findByGuestId(guestId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewResponse> getReviewsByRating(Integer rating) {
        return reviewRepository.findByOverallRating(rating).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Double getAverageRatingByRoom(Long roomId) {
        return reviewRepository.getAverageRatingByRoomId(roomId);
    }

    @Override
    @Transactional
    public void deleteReview(Long id, User user) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", id));

        // Only allow deletion by the review owner or admin
        if (user.getRole() != UserRole.ADMIN) {
            Guest guest = guestRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Guest profile not found"));

            if (!review.getGuest().getId().equals(guest.getId())) {
                throw new BadRequestException("You can only delete your own reviews");
            }
        }

        reviewRepository.deleteById(id);
    }

    @Override
    @Transactional
    public ReviewResponse addManagementResponse(Long id, String response, User user) {
        // Only managers and admins can respond
        if (user.getRole() != UserRole.ADMIN && user.getRole() != UserRole.MANAGER) {
            throw new BadRequestException("Only managers can respond to reviews");
        }

        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", id));

        review.setManagementResponse(response);
        review.setResponseDate(LocalDateTime.now());

        review = reviewRepository.save(review);
        return mapToResponse(review);
    }

    private ReviewResponse mapToResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .reservationId(review.getReservation().getId())
                .guestId(review.getGuest().getId())
                .guestName(review.getGuest().getUser().getFirstName() + " " + 
                          review.getGuest().getUser().getLastName())
                .roomId(review.getRoom().getId())
                .roomNumber(review.getRoom().getRoomNumber())
                .overallRating(review.getOverallRating())
                .cleanlinessRating(review.getCleanlinessRating())
                .serviceRating(review.getServiceRating())
                .locationRating(review.getLocationRating())
                .valueRating(review.getValueRating())
                .comment(review.getComment())
                .title(review.getTitle())
                .wouldRecommend(review.getWouldRecommend())
                .isVerified(review.getIsVerified())
                .managementResponse(review.getManagementResponse())
                .responseDate(review.getResponseDate())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
