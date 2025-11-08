package com.hotel.management.entity.enums;

public enum CancellationPolicyType {
    FLEXIBLE,               // Full refund up to 24 hours before check-in
    MODERATE,               // Full refund up to 5 days before check-in
    STRICT,                 // 50% refund up to 7 days before check-in
    NON_REFUNDABLE,         // No refund under any circumstances
    SUPER_FLEXIBLE,         // Full refund up to check-in time
    LONG_TERM_FLEXIBLE      // Full refund up to 30 days before check-in
}
