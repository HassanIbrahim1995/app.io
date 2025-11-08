package com.hotel.management.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "amenities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Amenity extends BaseEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String name; // WiFi, TV, Mini Bar, Air Conditioning, etc.

    @Column(length = 500)
    private String description;

    @Column(name = "icon_name", length = 50)
    private String iconName;

    @Column(nullable = false)
    private Boolean isPremium = false;

    @ManyToMany(mappedBy = "amenities")
    private List<Room> rooms = new ArrayList<>();
}
