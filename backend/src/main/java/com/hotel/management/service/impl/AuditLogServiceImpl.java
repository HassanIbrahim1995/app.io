package com.hotel.management.service.impl;

import com.hotel.management.entity.AuditLog;
import com.hotel.management.entity.enums.AuditAction;
import com.hotel.management.entity.enums.EntityType;
import com.hotel.management.repository.AuditLogRepository;
import com.hotel.management.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Override
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AuditLog createAuditLog(AuditLog auditLog) {
        try {
            auditLog.setTimestamp(LocalDateTime.now());
            return auditLogRepository.save(auditLog);
        } catch (Exception e) {
            log.error("Failed to save audit log: {}", e.getMessage());
            return null;
        }
    }

    @Override
    @Async
    public void logAction(Long userId, String username, String userRole, AuditAction action,
                         EntityType entityType, Long entityId, String entityName,
                         String description, String ipAddress, String userAgent) {
        AuditLog auditLog = AuditLog.builder()
                .userId(userId)
                .username(username)
                .userRole(userRole)
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .entityName(entityName)
                .description(description)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .success(true)
                .timestamp(LocalDateTime.now())
                .build();
        
        createAuditLog(auditLog);
    }

    @Override
    @Async
    public void logAction(Long userId, String username, String userRole, AuditAction action,
                         EntityType entityType, Long entityId, String description) {
        logAction(userId, username, userRole, action, entityType, entityId, null, description, null, null);
    }

    @Override
    @Async
    public void logFailedAction(Long userId, String username, AuditAction action,
                               String errorMessage, String ipAddress) {
        AuditLog auditLog = AuditLog.builder()
                .userId(userId)
                .username(username)
                .action(action)
                .description("Failed action: " + action)
                .ipAddress(ipAddress)
                .success(false)
                .errorMessage(errorMessage)
                .timestamp(LocalDateTime.now())
                .build();
        
        createAuditLog(auditLog);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLog> getAuditLogs(Pageable pageable) {
        return auditLogRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLog> getAuditLogsByUser(Long userId, Pageable pageable) {
        return auditLogRepository.findByUserId(userId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLog> getAuditLogsByAction(AuditAction action, Pageable pageable) {
        return auditLogRepository.findByAction(action, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLog> getAuditLogsByEntity(EntityType entityType, Long entityId, Pageable pageable) {
        return auditLogRepository.findByEntityTypeAndEntityId(entityType, entityId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLog> getAuditLogsByDateRange(LocalDateTime start, LocalDateTime end, Pageable pageable) {
        return auditLogRepository.findByTimestampBetween(start, end, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLog> getFailedActions(Pageable pageable) {
        return auditLogRepository.findFailedActions(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getActionStatistics(LocalDateTime start, LocalDateTime end) {
        List<Object[]> results = auditLogRepository.getActionStatistics(start, end);
        Map<String, Long> statistics = new HashMap<>();
        
        for (Object[] result : results) {
            AuditAction action = (AuditAction) result[0];
            Long count = (Long) result[1];
            statistics.put(action.name(), count);
        }
        
        return statistics;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getUserRoleStatistics(LocalDateTime start, LocalDateTime end) {
        List<Object[]> results = auditLogRepository.getUserRoleStatistics(start, end);
        Map<String, Long> statistics = new HashMap<>();
        
        for (Object[] result : results) {
            String role = (String) result[0];
            Long count = (Long) result[1];
            statistics.put(role != null ? role : "UNKNOWN", count);
        }
        
        return statistics;
    }
}
