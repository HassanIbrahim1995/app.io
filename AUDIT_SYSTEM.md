# Audit Logging System

## Overview
The hotel management system includes a comprehensive audit logging system that tracks all user and employee activities using Aspect-Oriented Programming (AOP). This provides complete traceability and accountability for all operations.

## Features

### 1. **Automatic Logging**
- All controller operations are automatically logged
- Authentication attempts (successful and failed) are tracked
- Custom methods can be marked for logging with `@Audited` annotation
- No manual logging code required in business logic

### 2. **Comprehensive Information Captured**
- **User Information**: User ID, username, role
- **Action Details**: Action type, entity type, entity ID
- **Request Information**: IP address, user agent, request URL, HTTP method
- **Execution Details**: Execution time, success/failure status
- **Data Changes**: Old and new values (configurable)
- **Timestamps**: Precise timing of all actions

### 3. **Action Types Tracked**
- Authentication (Login, Logout, Failed Login)
- CRUD Operations (Create, Read, Update, Delete)
- Reservation lifecycle (Create, Confirm, Check-in, Check-out, Cancel)
- Payment operations (Create, Process, Refund)
- Room management (Status changes, moves, blocks)
- Housekeeping tasks (Create, Assign, Complete)
- Employee operations (Hire, Update, Terminate)
- Policy acceptances
- Reviews and ratings
- Promotions and rate plans
- System operations (Exports, Reports, Settings)

## Implementation

### AOP Aspects

#### 1. **AuditAspect** - Annotation-Based Logging
Logs methods marked with `@Audited` annotation:

```java
@Audited(
    action = AuditAction.RESERVATION_CREATE,
    entityType = EntityType.RESERVATION,
    description = "Create new reservation",
    logParameters = true,
    logResult = false,
    logExecutionTime = true
)
public ReservationResponse createReservation(ReservationRequest request, User user) {
    // Business logic
}
```

#### 2. **ControllerAuditAspect** - Automatic Controller Logging
Automatically logs all REST controller operations:
- Logs all non-GET requests automatically
- Captures HTTP method, URL, and request details
- Determines action type based on HTTP method
- Identifies entity type from URL pattern

#### 3. **AuthenticationAuditAspect** - Authentication Logging
Specifically tracks authentication events:
- Successful logins
- Failed login attempts
- New user registrations
- Password changes

### Database Schema

```sql
CREATE TABLE audit_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    username VARCHAR(100),
    user_role VARCHAR(20),
    action VARCHAR(50) NOT NULL,
    entity_type VARCHAR(50),
    entity_id BIGINT,
    entity_name VARCHAR(200),
    description TEXT,
    old_value TEXT,
    new_value TEXT,
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    request_url VARCHAR(500),
    request_method VARCHAR(10),
    session_id VARCHAR(100),
    success BOOLEAN NOT NULL DEFAULT TRUE,
    error_message VARCHAR(1000),
    execution_time_ms BIGINT,
    timestamp TIMESTAMP NOT NULL,
    additional_data TEXT
);

-- Indexes for fast querying
CREATE INDEX idx_audit_user ON audit_logs(user_id);
CREATE INDEX idx_audit_action ON audit_logs(action);
CREATE INDEX idx_audit_entity ON audit_logs(entity_type, entity_id);
CREATE INDEX idx_audit_timestamp ON audit_logs(timestamp);
CREATE INDEX idx_audit_ip ON audit_logs(ip_address);
```

## Usage Examples

### 1. Using @Audited Annotation

```java
@Service
public class ReservationServiceImpl implements ReservationService {

    @Audited(
        action = AuditAction.RESERVATION_CREATE,
        entityType = EntityType.RESERVATION,
        description = "Create new reservation"
    )
    public ReservationResponse createReservation(ReservationRequest request, User user) {
        // Create reservation logic
    }

    @Audited(
        action = AuditAction.RESERVATION_CANCEL,
        entityType = EntityType.RESERVATION,
        description = "Cancel reservation with refund calculation"
    )
    public ReservationResponse cancelReservation(Long id, User user) {
        // Cancel logic
    }
}
```

### 2. Manual Logging

```java
@Service
@RequiredArgsConstructor
public class CustomService {
    
    private final AuditLogService auditLogService;
    
    public void performCustomAction(User user) {
        auditLogService.logAction(
            user.getId(),
            user.getEmail(),
            user.getRole().name(),
            AuditAction.CUSTOM_ACTION,
            EntityType.SYSTEM,
            null,
            "Custom action performed",
            "192.168.1.1",
            "Mozilla/5.0..."
        );
    }
}
```

## API Endpoints

### Get All Audit Logs
```http
GET /api/v1/audit-logs?page=0&size=50&sort=timestamp,desc
Authorization: Bearer {token}
```

### Get Logs by User
```http
GET /api/v1/audit-logs/user/{userId}?page=0&size=50
Authorization: Bearer {token}
```

### Get Logs by Action
```http
GET /api/v1/audit-logs/action/LOGIN?page=0&size=50
Authorization: Bearer {token}
```

### Get Logs by Entity
```http
GET /api/v1/audit-logs/entity/RESERVATION/123?page=0&size=50
Authorization: Bearer {token}
```

### Get Logs by Date Range
```http
GET /api/v1/audit-logs/date-range?start=2025-01-01T00:00:00&end=2025-01-31T23:59:59
Authorization: Bearer {token}
```

### Get Failed Actions
```http
GET /api/v1/audit-logs/failed?page=0&size=50
Authorization: Bearer {token}
```

### Get Action Statistics
```http
GET /api/v1/audit-logs/statistics/actions?start=2025-01-01T00:00:00&end=2025-01-31T23:59:59
Authorization: Bearer {token}

Response:
{
  "LOGIN": 1523,
  "RESERVATION_CREATE": 342,
  "RESERVATION_CANCEL": 45,
  "PAYMENT_PROCESS": 298,
  "ROOM_STATUS_CHANGE": 156
}
```

### Get User Role Statistics
```http
GET /api/v1/audit-logs/statistics/user-roles?start=2025-01-01T00:00:00&end=2025-01-31T23:59:59
Authorization: Bearer {token}

Response:
{
  "ADMIN": 450,
  "MANAGER": 320,
  "FRONT_DESK": 890,
  "HOUSEKEEPING": 234,
  "GUEST": 1500
}
```

## Security Features

### 1. **IP Address Tracking**
- Captures real client IP behind proxies/load balancers
- Checks multiple headers (X-Forwarded-For, Proxy-Client-IP, etc.)
- Helps identify suspicious activities

### 2. **Failed Action Monitoring**
- All failed operations are logged separately
- Easy to query failed login attempts
- Helps detect potential security threats

### 3. **Async Processing**
- Audit logging is asynchronous to not impact performance
- Uses separate transactions to ensure logs are saved even if main transaction fails
- Non-blocking logging operations

### 4. **Sensitive Data Protection**
- Passwords and sensitive fields are not logged
- Parameters can be sanitized before logging
- Configurable what gets logged (parameters, results)

## Use Cases

### 1. **Compliance & Audit**
- Track all data access and modifications
- Generate compliance reports
- Prove who did what and when

### 2. **Security Monitoring**
- Detect unusual patterns (multiple failed logins)
- Track suspicious IP addresses
- Monitor privilege escalation attempts

### 3. **Debugging & Support**
- Trace user actions leading to issues
- Understand sequence of events
- Reproduce problems

### 4. **Analytics**
- User behavior analysis
- Feature usage statistics
- Performance monitoring

### 5. **Dispute Resolution**
- Verify booking/cancellation times
- Track payment processing
- Prove policy acceptances

## Configuration

### Enable/Disable Audit Logging
```yaml
# application.yml
spring:
  aop:
    auto: true # Enable AOP
    proxy-target-class: true

# Custom audit configuration
audit:
  enabled: true
  log-read-operations: false # Don't log GET requests
  log-system-user: false # Don't log system/automated actions
  retention-days: 365 # Keep logs for 1 year
```

### Customize What Gets Logged

```java
@Audited(
    action = AuditAction.UPDATE,
    entityType = EntityType.ROOM,
    description = "Update room details",
    logParameters = true,      // Log method parameters
    logResult = false,          // Don't log return value
    logExecutionTime = true     // Log how long it took
)
```

## Best Practices

1. **Log Meaningful Actions**: Don't log every single read operation
2. **Protect Sensitive Data**: Never log passwords, credit card numbers, etc.
3. **Regular Cleanup**: Archive or delete old logs based on retention policy
4. **Monitor Failed Actions**: Set up alerts for multiple failed attempts
5. **Use Appropriate Log Levels**: Critical actions vs. informational
6. **Index Strategy**: Proper indexes for fast querying of large log tables
7. **Async Logging**: Always log asynchronously to avoid performance impact

## Performance Considerations

- Audit logging is asynchronous and non-blocking
- Uses separate transaction to prevent rollback impact
- Efficient indexing for fast queries
- Consider partitioning for very large datasets
- Regular archival of old logs

## Compliance

This audit system helps meet compliance requirements for:
- **GDPR**: Data access tracking and proof of consent
- **PCI DSS**: Payment card data access logging
- **HIPAA**: Healthcare data access tracking
- **SOX**: Financial data access control
- **ISO 27001**: Information security management

## Example Audit Log Entry

```json
{
  "id": 12345,
  "userId": 789,
  "username": "john.doe@hotel.com",
  "userRole": "FRONT_DESK",
  "action": "RESERVATION_CREATE",
  "entityType": "RESERVATION",
  "entityId": 456,
  "entityName": "RES-20250115-ABC",
  "description": "Create new reservation",
  "oldValue": null,
  "newValue": "{\"roomId\":101,\"checkIn\":\"2025-01-15\",\"checkOut\":\"2025-01-18\"}",
  "ipAddress": "192.168.1.100",
  "userAgent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64)",
  "requestUrl": "/api/v1/reservations",
  "requestMethod": "POST",
  "sessionId": "sess_abc123",
  "success": true,
  "errorMessage": null,
  "executionTimeMs": 234,
  "timestamp": "2025-01-08T14:30:15",
  "additionalData": null
}
```
