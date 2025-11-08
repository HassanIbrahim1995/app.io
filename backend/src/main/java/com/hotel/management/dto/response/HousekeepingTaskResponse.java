package com.hotel.management.dto.response;

import com.hotel.management.entity.enums.TaskPriority;
import com.hotel.management.entity.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HousekeepingTaskResponse {
    private Long id;
    private Long roomId;
    private String roomNumber;
    private Long assignedToId;
    private String assignedToName;
    private String taskType;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDateTime scheduledDate;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private Integer estimatedDuration;
    private Integer actualDuration;
    private String notes;
    private String completionNotes;
    private LocalDateTime createdAt;
}
