package com.hotel.management.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.management.annotation.Audited;
import com.hotel.management.entity.AuditLog;
import com.hotel.management.entity.User;
import com.hotel.management.service.AuditLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

/**
 * Aspect for automatic audit logging using @Audited annotation
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditAspect {

    private final AuditLogService auditLogService;
    private final ObjectMapper objectMapper;

    @Around("@annotation(audited)")
    public Object auditMethod(ProceedingJoinPoint joinPoint, Audited audited) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        // Get current user
        User currentUser = getCurrentUser();
        
        // Get request details
        HttpServletRequest request = getCurrentRequest();
        String ipAddress = getClientIpAddress(request);
        String userAgent = request != null ? request.getHeader("User-Agent") : null;
        String requestUrl = request != null ? request.getRequestURI() : null;
        String requestMethod = request != null ? request.getMethod() : null;
        
        // Get method details
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getMethod().getName();
        
        // Build audit log
        AuditLog.AuditLogBuilder auditLogBuilder = AuditLog.builder()
                .action(audited.action())
                .entityType(audited.entityType())
                .description(audited.description().isEmpty() ? 
                    "Executed method: " + methodName : audited.description())
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .requestUrl(requestUrl)
                .requestMethod(requestMethod)
                .timestamp(LocalDateTime.now());
        
        // Add user information
        if (currentUser != null) {
            auditLogBuilder
                    .userId(currentUser.getId())
                    .username(currentUser.getEmail())
                    .userRole(currentUser.getRole().name());
        }
        
        // Log parameters if enabled
        if (audited.logParameters()) {
            try {
                Object[] args = joinPoint.getArgs();
                if (args.length > 0) {
                    // Try to extract entity ID from first parameter
                    Object firstArg = args[0];
                    if (firstArg instanceof Long) {
                        auditLogBuilder.entityId((Long) firstArg);
                    }
                    
                    // Log parameters as JSON (excluding sensitive data)
                    String paramsJson = objectMapper.writeValueAsString(sanitizeParameters(args));
                    auditLogBuilder.additionalData(paramsJson);
                }
            } catch (Exception e) {
                log.warn("Failed to serialize parameters: {}", e.getMessage());
            }
        }
        
        Object result = null;
        boolean success = true;
        String errorMessage = null;
        
        try {
            // Execute the method
            result = joinPoint.proceed();
            
            // Log result if enabled
            if (audited.logResult() && result != null) {
                try {
                    String resultJson = objectMapper.writeValueAsString(sanitizeSensitiveData(result));
                    auditLogBuilder.newValue(resultJson);
                } catch (Exception e) {
                    log.warn("Failed to serialize result: {}", e.getMessage());
                }
            }
            
            return result;
        } catch (Exception e) {
            success = false;
            errorMessage = e.getMessage();
            throw e;
        } finally {
            // Calculate execution time
            long executionTime = System.currentTimeMillis() - startTime;
            
            // Build and save audit log
            AuditLog auditLog = auditLogBuilder
                    .success(success)
                    .errorMessage(errorMessage)
                    .executionTimeMs(audited.logExecutionTime() ? executionTime : null)
                    .build();
            
            auditLogService.createAuditLog(auditLog);
        }
    }
    
    private User getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof User) {
                return (User) authentication.getPrincipal();
            }
        } catch (Exception e) {
            log.debug("Could not get current user: {}", e.getMessage());
        }
        return null;
    }
    
    private HttpServletRequest getCurrentRequest() {
        try {
            ServletRequestAttributes attributes = 
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            return attributes != null ? attributes.getRequest() : null;
        } catch (Exception e) {
            log.debug("Could not get current request: {}", e.getMessage());
            return null;
        }
    }
    
    private String getClientIpAddress(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        
        String[] headerNames = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
        };
        
        for (String header : headerNames) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                return ip.split(",")[0].trim();
            }
        }
        
        return request.getRemoteAddr();
    }
    
    private Object[] sanitizeParameters(Object[] args) {
        // Remove sensitive information from parameters
        // This is a simple implementation - enhance based on your needs
        Object[] sanitized = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof User) {
                // Don't log user objects directly
                sanitized[i] = "User[id=" + ((User) args[i]).getId() + "]";
            } else {
                sanitized[i] = args[i];
            }
        }
        return sanitized;
    }
    
    private Object sanitizeSensitiveData(Object obj) {
        // Remove sensitive fields like passwords
        // This is a placeholder - implement based on your security requirements
        return obj;
    }
}
