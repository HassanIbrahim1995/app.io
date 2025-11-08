package com.hotel.management.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "room_move_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomMoveHistory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_room_id", nullable = false)
    private Room fromRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_room_id", nullable = false)
    private Room toRoom;

    @Column(name = "moved_at", nullable = false)
    private LocalDateTime movedAt;

    @Column(name = "moved_by", nullable = false)
    private Long movedBy;

    @Column(name = "reason", length = 500)
    private String reason;

    @Column(name = "charges_waived", nullable = false)
    private Boolean chargesWaived = false;
}
