package com.hotel.management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmenityResponse {
    private Long id;
    private String name;
    private String description;
    private String iconName;
    private Boolean isPremium;
}
