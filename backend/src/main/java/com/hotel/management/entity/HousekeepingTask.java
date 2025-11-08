package com.hotel.management.entity;

import com.hotel.management.entity.enums.TaskPriority;
import com.hotel.management.entity.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "housekeeping_tasks", indexes = {
    @Index(name = "idx_task_room", columnList = "room_id"),
    @Index(name = "idx_task_assigned", columnList = "assigned_to"),
    @Index(name = "idx_task_status", columnList = "status"),
    @Index(name = "idx_task_date", columnList = "scheduled_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HousekeepingTask extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to")
    private User assignedTo;

    @Column(name = "task_type", nullable = false, length = 50)
    private String taskType; // CLEANING, INSPECTION, MAINTENANCE, TURNDOWN

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TaskStatus status = TaskStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TaskPriority priority = TaskPriority.MEDIUM;

    @Column(name = "scheduled_date", nullable = false)
    private LocalDateTime scheduledDate;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "estimated_duration")
    private Integer estimatedDuration; // in minutes

    @Column(name = "actual_duration")
    private Integer actualDuration; // in minutes

    @Column(length = 1000)
    private String notes;

    @Column(name = "completion_notes", length = 1000)
    private String completionNotes;
}
