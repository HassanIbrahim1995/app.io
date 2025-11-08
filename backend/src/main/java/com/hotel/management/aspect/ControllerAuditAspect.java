package com.hotel.management.aspect;

import com.hotel.management.entity.AuditLog;
import com.hotel.management.entity.User;
import com.hotel.management.entity.enums.AuditAction;
import com.hotel.management.entity.enums.EntityType;
import com.hotel.management.service.AuditLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * Aspect for automatic audit logging of all controller operations
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class ControllerAuditAspect {

    private final AuditLogService auditLogService;

    @Around("within(@org.springframework.web.bind.annotation.RestController *)")
    public Object auditControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        // Get current user
        User currentUser = getCurrentUser();
        
        // Get request details
        HttpServletRequest request = getCurrentRequest();
        String ipAddress = getClientIpAddress(request);
        String userAgent = request != null ? request.getHeader("User-Agent") : null;
        String requestUrl = request != null ? request.getRequestURI() : null;
        String requestMethod = request != null ? request.getMethod() : null;
        
        // Determine action based on HTTP method
        AuditAction action = determineAction(requestMethod);
        
        // Get entity type from URL
        EntityType entityType = determineEntityType(requestUrl);
        
        Object result = null;
        boolean success = true;
        String errorMessage = null;
        
        try {
            result = joinPoint.proceed();
            return result;
        } catch (Exception e) {
            success = false;
            errorMessage = e.getMessage();
            throw e;
        } finally {
            // Only log if we have a user and it's not a GET request (to avoid too many logs)
            if (currentUser != null && !"GET".equals(requestMethod)) {
                long executionTime = System.currentTimeMillis() - startTime;
                
                AuditLog auditLog = AuditLog.builder()
                        .userId(currentUser.getId())
                        .username(currentUser.getEmail())
                        .userRole(currentUser.getRole().name())
                        .action(action)
                        .entityType(entityType)
                        .description(requestMethod + " " + requestUrl)
                        .ipAddress(ipAddress)
                        .userAgent(userAgent)
                        .requestUrl(requestUrl)
                        .requestMethod(requestMethod)
                        .success(success)
                        .errorMessage(errorMessage)
                        .executionTimeMs(executionTime)
                        .timestamp(LocalDateTime.now())
                        .build();
                
                auditLogService.createAuditLog(auditLog);
            }
        }
    }
    
    private AuditAction determineAction(String httpMethod) {
        if (httpMethod == null) {
            return AuditAction.READ;
        }
        
        return switch (httpMethod.toUpperCase()) {
            case "POST" -> AuditAction.CREATE;
            case "PUT", "PATCH" -> AuditAction.UPDATE;
            case "DELETE" -> AuditAction.DELETE;
            default -> AuditAction.READ;
        };
    }
    
    private EntityType determineEntityType(String url) {
        if (url == null) {
            return EntityType.SYSTEM;
        }
        
        if (url.contains("/reservations")) return EntityType.RESERVATION;
        if (url.contains("/rooms")) return EntityType.ROOM;
        if (url.contains("/guests")) return EntityType.GUEST;
        if (url.contains("/employees")) return EntityType.EMPLOYEE;
        if (url.contains("/payments")) return EntityType.PAYMENT;
        if (url.contains("/reviews")) return EntityType.REVIEW;
        if (url.contains("/promotions")) return EntityType.PROMOTION;
        if (url.contains("/products")) return EntityType.PRODUCT;
        if (url.contains("/rate-plans")) return EntityType.RATE_PLAN;
        if (url.contains("/housekeeping")) return EntityType.HOUSEKEEPING_TASK;
        if (url.contains("/policies")) return EntityType.HOTEL_POLICY;
        
        return EntityType.SYSTEM;
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
            "HTTP_X_FORWARDED_FOR"
        };
        
        for (String header : headerNames) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                return ip.split(",")[0].trim();
            }
        }
        
        return request.getRemoteAddr();
    }
}
