package com.hotel.management.repository;

import com.hotel.management.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    List<Review> findByRoomId(Long roomId);
    
    List<Review> findByGuestId(Long guestId);
    
    List<Review> findByIsPublishedTrue();
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.room.id = :roomId AND r.isPublished = true")
    Double findAverageRatingByRoomId(Long roomId);
    
    @Query("SELECT r FROM Review r WHERE r.isPublished = true ORDER BY r.createdAt DESC")
    List<Review> findLatestPublishedReviews();
}
