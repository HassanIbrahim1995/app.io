package com.hotel.management.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ReviewRequest {
    
    @NotNull(message = "Reservation ID is required")
    private Long reservationId;
    
    @NotNull(message = "Overall rating is required")
    @Min(value = 1, message = "Rating must be between 1 and 5")
    @Max(value = 5, message = "Rating must be between 1 and 5")
    private Integer overallRating;
    
    @Min(value = 1, message = "Rating must be between 1 and 5")
    @Max(value = 5, message = "Rating must be between 1 and 5")
    private Integer cleanlinessRating;
    
    @Min(value = 1, message = "Rating must be between 1 and 5")
    @Max(value = 5, message = "Rating must be between 1 and 5")
    private Integer serviceRating;
    
    @Min(value = 1, message = "Rating must be between 1 and 5")
    @Max(value = 5, message = "Rating must be between 1 and 5")
    private Integer locationRating;
    
    @Min(value = 1, message = "Rating must be between 1 and 5")
    @Max(value = 5, message = "Rating must be between 1 and 5")
    private Integer valueRating;
    
    @Size(min = 10, max = 2000, message = "Comment must be between 10 and 2000 characters")
    private String comment;
    
    private String title;
    
    private Boolean wouldRecommend = true;
}
