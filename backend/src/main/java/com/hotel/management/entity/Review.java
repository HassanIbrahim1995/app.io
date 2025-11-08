package com.hotel.management.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reviews", indexes = {
    @Index(name = "idx_review_guest", columnList = "guest_id"),
    @Index(name = "idx_review_room", columnList = "room_id"),
    @Index(name = "idx_review_rating", columnList = "rating")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guest_id", nullable = false)
    private Guest guest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @Column(nullable = false)
    private Integer rating; // 1-5

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 2000)
    private String comment;

    @Column(name = "cleanliness_rating")
    private Integer cleanlinessRating;

    @Column(name = "service_rating")
    private Integer serviceRating;

    @Column(name = "location_rating")
    private Integer locationRating;

    @Column(name = "value_rating")
    private Integer valueRating;

    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified = false;

    @Column(name = "is_published", nullable = false)
    private Boolean isPublished = false;

    @Column(name = "staff_response", length = 1000)
    private String staffResponse;
}
