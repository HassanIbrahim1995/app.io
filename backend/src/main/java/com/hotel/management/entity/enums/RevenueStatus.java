package com.hotel.management.entity.enums;

public enum RevenueStatus {
    PENDING,        // Revenue recorded but payment pending
    COLLECTED,      // Payment collected successfully
    REFUNDED,       // Revenue refunded to customer
    DISPUTED,       // Customer disputed the charge
    CANCELLED,      // Charge cancelled before collection
    ADJUSTED,       // Revenue amount adjusted
    WRITTEN_OFF     // Written off as bad debt
}
