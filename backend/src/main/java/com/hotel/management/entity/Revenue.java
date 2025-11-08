package com.hotel.management.entity;

import com.hotel.management.entity.enums.RevenueStatus;
import com.hotel.management.entity.enums.RevenueType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "revenues", indexes = {
    @Index(name = "idx_revenue_date", columnList = "revenue_date"),
    @Index(name = "idx_revenue_type", columnList = "revenue_type"),
    @Index(name = "idx_revenue_status", columnList = "status"),
    @Index(name = "idx_revenue_reservation", columnList = "reservation_id"),
    @Index(name = "idx_revenue_department", columnList = "department")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Revenue extends BaseEntity {

    @Column(name = "revenue_number", unique = true, length = 50)
    private String revenueNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "revenue_type", nullable = false, length = 30)
    private RevenueType revenueType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RevenueStatus status = RevenueStatus.PENDING;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "tax_amount", precision = 10, scale = 2)
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(name = "net_amount", precision = 12, scale = 2)
    private BigDecimal netAmount;

    @Column(name = "revenue_date", nullable = false)
    private LocalDate revenueDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guest_id")
    private Guest guest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(length = 50)
    private String department; // ROOMS, F&B, SPA, etc.

    @Column(name = "cost_center", length = 50)
    private String costCenter;

    @Column(name = "gl_code", length = 20)
    private String glCode; // General Ledger code for accounting

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "quantity", precision = 10, scale = 2)
    private BigDecimal quantity = BigDecimal.ONE;

    @Column(name = "unit_price", precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "original_amount", precision = 12, scale = 2)
    private BigDecimal originalAmount;

    @Column(name = "currency", length = 3)
    private String currency = "USD";

    @Column(name = "exchange_rate", precision = 10, scale = 6)
    private BigDecimal exchangeRate = BigDecimal.ONE;

    @Column(name = "source_reference", length = 100)
    private String sourceReference; // Reference to source document/transaction

    @Column(name = "posted_to_gl")
    private Boolean postedToGl = false;

    @Column(name = "posting_date")
    private LocalDate postingDate;

    @Column(name = "recognized_date")
    private LocalDate recognizedDate; // For revenue recognition

    @Column(length = 500)
    private String notes;
}
