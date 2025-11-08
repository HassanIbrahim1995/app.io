package com.hotel.management.aspect;

import com.hotel.management.entity.enums.AuditAction;
import com.hotel.management.service.AuditLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Aspect for auditing authentication operations
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationAuditAspect {

    private final AuditLogService auditLogService;

    @AfterReturning(
        pointcut = "execution(* com.hotel.management.service.impl.AuthServiceImpl.login(..))",
        returning = "result"
    )
    public void auditSuccessfulLogin(JoinPoint joinPoint, Object result) {
        try {
            Object[] args = joinPoint.getArgs();
            String email = args.length > 0 ? (String) args[0] : "unknown";
            
            HttpServletRequest request = getCurrentRequest();
            String ipAddress = getClientIpAddress(request);
            
            auditLogService.logAction(
                null,
                email,
                null,
                AuditAction.LOGIN,
                null,
                null,
                "Successful login",
                ipAddress,
                request != null ? request.getHeader("User-Agent") : null
            );
            
            log.info("Login successful for user: {} from IP: {}", email, ipAddress);
        } catch (Exception e) {
            log.error("Failed to audit login: {}", e.getMessage());
        }
    }

    @AfterThrowing(
        pointcut = "execution(* com.hotel.management.service.impl.AuthServiceImpl.login(..))",
        throwing = "error"
    )
    public void auditFailedLogin(JoinPoint joinPoint, Throwable error) {
        try {
            Object[] args = joinPoint.getArgs();
            String email = args.length > 0 ? (String) args[0] : "unknown";
            
            HttpServletRequest request = getCurrentRequest();
            String ipAddress = getClientIpAddress(request);
            
            auditLogService.logFailedAction(
                null,
                email,
                AuditAction.LOGIN_FAILED,
                error.getMessage(),
                ipAddress
            );
            
            log.warn("Login failed for user: {} from IP: {} - {}", email, ipAddress, error.getMessage());
        } catch (Exception e) {
            log.error("Failed to audit failed login: {}", e.getMessage());
        }
    }

    @AfterReturning("execution(* com.hotel.management.service.impl.GuestProfileServiceImpl.registerGuest(..))")
    public void auditGuestRegistration(JoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs();
            // Extract email from registration request
            String email = "new_guest";
            
            HttpServletRequest request = getCurrentRequest();
            String ipAddress = getClientIpAddress(request);
            
            auditLogService.logAction(
                null,
                email,
                "GUEST",
                AuditAction.GUEST_REGISTER,
                null,
                null,
                "New guest registration",
                ipAddress,
                request != null ? request.getHeader("User-Agent") : null
            );
            
            log.info("New guest registered from IP: {}", ipAddress);
        } catch (Exception e) {
            log.error("Failed to audit guest registration: {}", e.getMessage());
        }
    }
    
    private HttpServletRequest getCurrentRequest() {
        try {
            ServletRequestAttributes attributes = 
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            return attributes != null ? attributes.getRequest() : null;
        } catch (Exception e) {
            return null;
        }
    }
    
    private String getClientIpAddress(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }
        
        String[] headerNames = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP"
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
