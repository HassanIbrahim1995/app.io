package com.hotel.management.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "guest_preferences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GuestPreference extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guest_id", nullable = false)
    private Guest guest;

    @Column(name = "preference_type", nullable = false, length = 50)
    private String preferenceType; // ROOM_TYPE, BED_TYPE, FLOOR_LEVEL, AMENITIES

    @Column(name = "preference_value", nullable = false, length = 200)
    private String preferenceValue;

    @Column(length = 500)
    private String notes;
}
