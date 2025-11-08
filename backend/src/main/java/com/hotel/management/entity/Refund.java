package com.hotel.management.entity;

import com.hotel.management.entity.enums.RefundStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "refunds")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Refund extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RefundStatus status = RefundStatus.PENDING;

    @Column(nullable = false, length = 500)
    private String reason;

    @Column(name = "refund_date")
    private LocalDateTime refundDate;

    @Column(name = "stripe_refund_id", length = 100)
    private String stripeRefundId;

    @Column(name = "processed_by")
    private Long processedBy;

    @Column(length = 500)
    private String notes;
}
