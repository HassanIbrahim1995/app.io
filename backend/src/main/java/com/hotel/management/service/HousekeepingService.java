package com.hotel.management.service;

import com.hotel.management.dto.request.HousekeepingTaskRequest;
import com.hotel.management.dto.response.HousekeepingTaskResponse;
import com.hotel.management.entity.enums.TaskStatus;

import java.time.LocalDate;
import java.util.List;

public interface HousekeepingService {
    HousekeepingTaskResponse createTask(HousekeepingTaskRequest request);
    HousekeepingTaskResponse getTaskById(Long id);
    List<HousekeepingTaskResponse> getAllTasks();
    List<HousekeepingTaskResponse> getTasksByStatus(TaskStatus status);
    List<HousekeepingTaskResponse> getTasksByRoomId(Long roomId);
    List<HousekeepingTaskResponse> getTasksByAssignedUser(Long userId);
    List<HousekeepingTaskResponse> getTasksForDate(LocalDate date);
    HousekeepingTaskResponse assignTask(Long taskId, Long userId);
    HousekeepingTaskResponse startTask(Long taskId);
    HousekeepingTaskResponse completeTask(Long taskId, String completionNotes);
    HousekeepingTaskResponse cancelTask(Long taskId, String reason);
    void deleteTask(Long id);
}
