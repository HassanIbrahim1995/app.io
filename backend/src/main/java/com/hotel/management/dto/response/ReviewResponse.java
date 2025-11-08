package com.hotel.management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    private Long id;
    private Long reservationId;
    private Long guestId;
    private String guestName;
    private Long roomId;
    private String roomNumber;
    private Integer overallRating;
    private Integer cleanlinessRating;
    private Integer serviceRating;
    private Integer locationRating;
    private Integer valueRating;
    private String comment;
    private String title;
    private Boolean wouldRecommend;
    private Boolean isVerified;
    private String managementResponse;
    private LocalDateTime responseDate;
    private LocalDateTime createdAt;
}
