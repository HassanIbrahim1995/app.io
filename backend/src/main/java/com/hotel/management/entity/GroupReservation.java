package com.hotel.management.entity;

import com.hotel.management.entity.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "group_reservations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupReservation extends BaseEntity {

    @Column(name = "group_number", nullable = false, unique = true, length = 20)
    private String groupNumber;

    @Column(name = "group_name", nullable = false, length = 200)
    private String groupName;

    @Column(name = "contact_person_name", nullable = false, length = 100)
    private String contactPersonName;

    @Column(name = "contact_email", nullable = false, length = 100)
    private String contactEmail;

    @Column(name = "contact_phone", length = 20)
    private String contactPhone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReservationStatus status = ReservationStatus.PENDING;

    @Column(name = "total_rooms", nullable = false)
    private Integer totalRooms;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "paid_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal paidAmount = BigDecimal.ZERO;

    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "special_requests", length = 1000)
    private String specialRequests;

    @Column(name = "promo_code", length = 50)
    private String promoCode;

    @OneToMany(mappedBy = "groupReservation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reservation> reservations = new ArrayList<>();
}
