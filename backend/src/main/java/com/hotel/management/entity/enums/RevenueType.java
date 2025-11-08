package com.hotel.management.entity.enums;

public enum RevenueType {
    // Room Revenue
    ROOM_BOOKING,           // Standard room booking revenue
    ROOM_UPGRADE,           // Revenue from room upgrades
    EARLY_CHECKIN,          // Early check-in fee
    LATE_CHECKOUT,          // Late check-out fee
    
    // Service Revenue
    MINIBAR,                // Minibar purchases
    ROOM_SERVICE,           // Room service orders
    LAUNDRY,                // Laundry services
    SPA,                    // Spa services
    RESTAURANT,             // Restaurant charges
    BAR,                    // Bar charges
    
    // Amenity Revenue
    PARKING,                // Parking fees
    WIFI,                   // Premium WiFi
    GYM,                    // Gym access fee
    POOL,                   // Pool access fee
    BUSINESS_CENTER,        // Business center usage
    
    // Extra Fees
    RESORT_FEE,             // Resort fees
    CITY_TAX,               // City/tourism tax
    SERVICE_CHARGE,         // Service charges
    CLEANING_FEE,           // Cleaning fees
    DAMAGE_FEE,             // Damage charges
    CANCELLATION_FEE,       // Cancellation penalties
    NO_SHOW_FEE,            // No-show charges
    
    // Event Revenue
    CONFERENCE_ROOM,        // Conference room rental
    BANQUET,                // Banquet services
    CATERING,               // Catering services
    EVENT_SERVICES,         // Other event services
    
    // Package Revenue
    PACKAGE_DEAL,           // Package deal revenue
    ALL_INCLUSIVE,          // All-inclusive packages
    
    // Upsell Revenue
    UPSELL_PRODUCT,         // Product upsells
    UPSELL_SERVICE,         // Service upsells
    
    // Other Revenue
    GIFT_SHOP,              // Gift shop sales
    VENDING_MACHINE,        // Vending machine revenue
    PET_FEE,                // Pet accommodation fee
    CRIB_FEE,               // Crib rental
    EXTRA_BED,              // Extra bed charge
    TIPS,                   // Tips/Gratuities
    MISCELLANEOUS,          // Other miscellaneous revenue
    
    // Refunds (Negative Revenue)
    REFUND,                 // Customer refunds
    DISCOUNT,               // Discounts applied
    COMPENSATION            // Compensation/goodwill
}
