package com.hotel.management.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "upsell_offers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpsellOffer extends BaseEntity {

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(name = "offer_type", nullable = false, length = 50)
    private String offerType; // ROOM_UPGRADE, PRODUCT, SERVICE, PACKAGE

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_room_type_id")
    private RoomType sourceRoomType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_room_type_id")
    private RoomType targetRoomType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "original_price", precision = 10, scale = 2)
    private BigDecimal originalPrice;

    @Column(name = "offer_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal offerPrice;

    @Column(name = "discount_percentage", precision = 5, scale = 2)
    private BigDecimal discountPercentage;

    @Column(name = "valid_from")
    private LocalDate validFrom;

    @Column(name = "valid_to")
    private LocalDate validTo;

    @Column(name = "max_uses")
    private Integer maxUses;

    @Column(name = "used_count", nullable = false)
    private Integer usedCount = 0;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "display_priority")
    private Integer displayPriority = 0;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "terms_conditions", length = 2000)
    private String termsConditions;
}
