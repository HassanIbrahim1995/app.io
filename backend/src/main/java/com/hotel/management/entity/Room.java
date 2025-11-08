package com.hotel.management.entity;

import com.hotel.management.entity.enums.RoomStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rooms", indexes = {
    @Index(name = "idx_room_number", columnList = "room_number"),
    @Index(name = "idx_room_type", columnList = "room_type_id"),
    @Index(name = "idx_room_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room extends BaseEntity {

    @Column(name = "room_number", nullable = false, unique = true, length = 10)
    private String roomNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "room_type_id", nullable = false)
    private RoomType roomType;

    @Column(nullable = false)
    private Integer floor;

    @Column(name = "base_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RoomStatus status = RoomStatus.AVAILABLE;

    @Column(name = "max_occupancy", nullable = false)
    private Integer maxOccupancy;

    @Column(name = "is_accessible", nullable = false)
    private Boolean isAccessible = false;

    @Column(name = "is_smoking_allowed", nullable = false)
    private Boolean isSmokingAllowed = false;

    @Column(length = 1000)
    private String description;

    @Column(name = "image_urls", length = 2000)
    private String imageUrls; // Comma-separated URLs

    @Column(length = 500)
    private String notes;

    @ManyToMany
    @JoinTable(
        name = "room_amenities",
        joinColumns = @JoinColumn(name = "room_id"),
        inverseJoinColumns = @JoinColumn(name = "amenity_id")
    )
    private List<Amenity> amenities = new ArrayList<>();

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HousekeepingTask> housekeepingTasks = new ArrayList<>();
}
