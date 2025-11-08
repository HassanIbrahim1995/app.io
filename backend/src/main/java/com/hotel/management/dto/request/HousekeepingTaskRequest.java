package com.hotel.management.dto.request;

import com.hotel.management.entity.enums.TaskPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HousekeepingTaskRequest {
    
    @NotNull(message = "Room ID is required")
    private Long roomId;
    
    private Long assignedToId;
    
    @NotBlank(message = "Task type is required")
    private String taskType; // CLEANING, INSPECTION, MAINTENANCE, TURNDOWN
    
    @NotBlank(message = "Title is required")
    private String title;
    
    private String description;
    
    @NotNull(message = "Priority is required")
    private TaskPriority priority;
    
    @NotNull(message = "Scheduled date is required")
    private LocalDateTime scheduledDate;
    
    private Integer estimatedDuration; // in minutes
    
    private String notes;
}
