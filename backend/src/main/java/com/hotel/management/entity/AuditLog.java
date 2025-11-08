package com.hotel.management.entity;

import com.hotel.management.entity.enums.AuditAction;
import com.hotel.management.entity.enums.EntityType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs", indexes = {
    @Index(name = "idx_audit_user", columnList = "user_id"),
    @Index(name = "idx_audit_action", columnList = "action"),
    @Index(name = "idx_audit_entity", columnList = "entity_type, entity_id"),
    @Index(name = "idx_audit_timestamp", columnList = "timestamp"),
    @Index(name = "idx_audit_ip", columnList = "ip_address")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username", length = 100)
    private String username;

    @Column(name = "user_role", length = 20)
    private String userRole;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private AuditAction action;

    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type", length = 50)
    private EntityType entityType;

    @Column(name = "entity_id")
    private Long entityId;

    @Column(name = "entity_name", length = 200)
    private String entityName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "old_value", columnDefinition = "TEXT")
    private String oldValue;

    @Column(name = "new_value", columnDefinition = "TEXT")
    private String newValue;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "request_url", length = 500)
    private String requestUrl;

    @Column(name = "request_method", length = 10)
    private String requestMethod;

    @Column(name = "session_id", length = 100)
    private String sessionId;

    @Column(name = "success", nullable = false)
    private Boolean success = true;

    @Column(name = "error_message", length = 1000)
    private String errorMessage;

    @Column(name = "execution_time_ms")
    private Long executionTimeMs;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;

    @Column(name = "additional_data", columnDefinition = "TEXT")
    private String additionalData;
}
