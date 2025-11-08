package com.hotel.management.repository;

import com.hotel.management.entity.AuditLog;
import com.hotel.management.entity.enums.AuditAction;
import com.hotel.management.entity.enums.EntityType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    
    Page<AuditLog> findByUserId(Long userId, Pageable pageable);
    
    Page<AuditLog> findByAction(AuditAction action, Pageable pageable);
    
    Page<AuditLog> findByEntityTypeAndEntityId(EntityType entityType, Long entityId, Pageable pageable);
    
    Page<AuditLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
    
    @Query("SELECT a FROM AuditLog a WHERE a.userId = :userId AND a.timestamp BETWEEN :start AND :end")
    Page<AuditLog> findByUserIdAndTimestampBetween(Long userId, LocalDateTime start, LocalDateTime end, Pageable pageable);
    
    @Query("SELECT a FROM AuditLog a WHERE a.action = :action AND a.timestamp BETWEEN :start AND :end")
    Page<AuditLog> findByActionAndTimestampBetween(AuditAction action, LocalDateTime start, LocalDateTime end, Pageable pageable);
    
    @Query("SELECT a FROM AuditLog a WHERE a.success = false")
    Page<AuditLog> findFailedActions(Pageable pageable);
    
    @Query("SELECT a FROM AuditLog a WHERE a.ipAddress = :ipAddress")
    Page<AuditLog> findByIpAddress(String ipAddress, Pageable pageable);
    
    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.userId = :userId AND a.action = :action AND a.timestamp > :since")
    Long countByUserIdAndActionSince(Long userId, AuditAction action, LocalDateTime since);
    
    @Query("SELECT a.action, COUNT(a) FROM AuditLog a WHERE a.timestamp BETWEEN :start AND :end GROUP BY a.action")
    List<Object[]> getActionStatistics(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT a.userRole, COUNT(a) FROM AuditLog a WHERE a.timestamp BETWEEN :start AND :end GROUP BY a.userRole")
    List<Object[]> getUserRoleStatistics(LocalDateTime start, LocalDateTime end);
}
