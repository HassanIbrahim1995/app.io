package com.hotel.management.annotation;

import com.hotel.management.entity.enums.AuditAction;
import com.hotel.management.entity.enums.EntityType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark methods for automatic audit logging
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Audited {
    
    /**
     * The audit action type
     */
    AuditAction action();
    
    /**
     * The entity type being audited
     */
    EntityType entityType() default EntityType.SYSTEM;
    
    /**
     * Description of the action
     */
    String description() default "";
    
    /**
     * Whether to log the method parameters
     */
    boolean logParameters() default true;
    
    /**
     * Whether to log the return value
     */
    boolean logResult() default false;
    
    /**
     * Whether to log execution time
     */
    boolean logExecutionTime() default true;
}
