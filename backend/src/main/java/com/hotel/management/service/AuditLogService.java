package com.hotel.management.service;

import com.hotel.management.entity.AuditLog;
import com.hotel.management.entity.enums.AuditAction;
import com.hotel.management.entity.enums.EntityType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Map;

public interface AuditLogService {
    AuditLog createAuditLog(AuditLog auditLog);
    
    void logAction(Long userId, String username, String userRole, AuditAction action, 
                   EntityType entityType, Long entityId, String entityName, 
                   String description, String ipAddress, String userAgent);
    
    void logAction(Long userId, String username, String userRole, AuditAction action,
                   EntityType entityType, Long entityId, String description);
    
    void logFailedAction(Long userId, String username, AuditAction action, 
                        String errorMessage, String ipAddress);
    
    Page<AuditLog> getAuditLogs(Pageable pageable);
    
    Page<AuditLog> getAuditLogsByUser(Long userId, Pageable pageable);
    
    Page<AuditLog> getAuditLogsByAction(AuditAction action, Pageable pageable);
    
    Page<AuditLog> getAuditLogsByEntity(EntityType entityType, Long entityId, Pageable pageable);
    
    Page<AuditLog> getAuditLogsByDateRange(LocalDateTime start, LocalDateTime end, Pageable pageable);
    
    Page<AuditLog> getFailedActions(Pageable pageable);
    
    Map<String, Long> getActionStatistics(LocalDateTime start, LocalDateTime end);
    
    Map<String, Long> getUserRoleStatistics(LocalDateTime start, LocalDateTime end);
}
