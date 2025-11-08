package com.hotel.management.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "room_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomType extends BaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String name; // Single, Double, Suite, Deluxe, etc.

    @Column(length = 1000)
    private String description;

    @Column(name = "base_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Column(name = "max_occupancy", nullable = false)
    private Integer maxOccupancy;

    @Column(name = "bed_type", length = 50)
    private String bedType; // King, Queen, Twin, etc.

    @Column(name = "room_size", precision = 8, scale = 2)
    private BigDecimal roomSize; // in square meters

    @Column(name = "image_url")
    private String imageUrl;

    @OneToMany(mappedBy = "roomType", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Room> rooms = new ArrayList<>();
}
