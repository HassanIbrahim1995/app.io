package com.hotel.management.entity.enums;

public enum AuditAction {
    // Authentication
    LOGIN,
    LOGOUT,
    LOGIN_FAILED,
    PASSWORD_CHANGE,
    PASSWORD_RESET,
    
    // CRUD Operations
    CREATE,
    READ,
    UPDATE,
    DELETE,
    
    // Reservation Operations
    RESERVATION_CREATE,
    RESERVATION_UPDATE,
    RESERVATION_CANCEL,
    RESERVATION_CONFIRM,
    RESERVATION_CHECKIN,
    RESERVATION_CHECKOUT,
    RESERVATION_NOSHOW,
    
    // Payment Operations
    PAYMENT_CREATE,
    PAYMENT_PROCESS,
    PAYMENT_REFUND,
    PAYMENT_FAILED,
    
    // Room Operations
    ROOM_STATUS_CHANGE,
    ROOM_MOVE,
    ROOM_BLOCK,
    ROOM_UNBLOCK,
    
    // Housekeeping Operations
    TASK_CREATE,
    TASK_ASSIGN,
    TASK_START,
    TASK_COMPLETE,
    TASK_CANCEL,
    
    // Guest Operations
    GUEST_REGISTER,
    GUEST_UPDATE,
    GUEST_CHECKIN,
    GUEST_CHECKOUT,
    
    // Employee Operations
    EMPLOYEE_HIRE,
    EMPLOYEE_UPDATE,
    EMPLOYEE_TERMINATE,
    
    // Policy Operations
    POLICY_ACCEPT,
    POLICY_CREATE,
    POLICY_UPDATE,
    
    // Review Operations
    REVIEW_CREATE,
    REVIEW_UPDATE,
    REVIEW_DELETE,
    REVIEW_RESPOND,
    
    // Promotion Operations
    PROMOTION_CREATE,
    PROMOTION_UPDATE,
    PROMOTION_APPLY,
    PROMOTION_DEACTIVATE,
    
    // Product Operations
    PRODUCT_CREATE,
    PRODUCT_UPDATE,
    PRODUCT_PURCHASE,
    PRODUCT_RESTOCK,
    
    // Rate Plan Operations
    RATE_PLAN_CREATE,
    RATE_PLAN_UPDATE,
    RATE_PLAN_APPLY,
    
    // System Operations
    EXPORT_DATA,
    IMPORT_DATA,
    REPORT_GENERATE,
    SETTINGS_CHANGE,
    
    // Security
    ACCESS_DENIED,
    PERMISSION_CHANGE,
    ROLE_CHANGE
}
