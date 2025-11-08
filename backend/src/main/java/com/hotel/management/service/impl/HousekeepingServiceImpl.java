package com.hotel.management.service.impl;

import com.hotel.management.dto.request.HousekeepingTaskRequest;
import com.hotel.management.dto.response.HousekeepingTaskResponse;
import com.hotel.management.entity.HousekeepingTask;
import com.hotel.management.entity.Room;
import com.hotel.management.entity.User;
import com.hotel.management.entity.enums.RoomStatus;
import com.hotel.management.entity.enums.TaskStatus;
import com.hotel.management.exception.BadRequestException;
import com.hotel.management.exception.ResourceNotFoundException;
import com.hotel.management.repository.HousekeepingTaskRepository;
import com.hotel.management.repository.RoomRepository;
import com.hotel.management.repository.UserRepository;
import com.hotel.management.service.HousekeepingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HousekeepingServiceImpl implements HousekeepingService {

    private final HousekeepingTaskRepository taskRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public HousekeepingTaskResponse createTask(HousekeepingTaskRequest request) {
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room", "id", request.getRoomId()));

        User assignedTo = null;
        if (request.getAssignedToId() != null) {
            assignedTo = userRepository.findById(request.getAssignedToId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getAssignedToId()));
        }

        HousekeepingTask task = HousekeepingTask.builder()
                .room(room)
                .assignedTo(assignedTo)
                .taskType(request.getTaskType())
                .title(request.getTitle())
                .description(request.getDescription())
                .status(TaskStatus.PENDING)
                .priority(request.getPriority())
                .scheduledDate(request.getScheduledDate())
                .estimatedDuration(request.getEstimatedDuration())
                .notes(request.getNotes())
                .build();

        task = taskRepository.save(task);

        // Update room status if it's a cleaning task
        if ("CLEANING".equals(request.getTaskType()) && room.getStatus() != RoomStatus.CLEANING) {
            room.setStatus(RoomStatus.CLEANING);
            roomRepository.save(room);
        }

        return mapToResponse(task);
    }

    @Override
    public HousekeepingTaskResponse getTaskById(Long id) {
        HousekeepingTask task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("HousekeepingTask", "id", id));
        return mapToResponse(task);
    }

    @Override
    public List<HousekeepingTaskResponse> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<HousekeepingTaskResponse> getTasksByStatus(TaskStatus status) {
        return taskRepository.findByStatus(status).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<HousekeepingTaskResponse> getTasksByRoomId(Long roomId) {
        return taskRepository.findByRoomId(roomId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<HousekeepingTaskResponse> getTasksByAssignedUser(Long userId) {
        return taskRepository.findByAssignedToId(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<HousekeepingTaskResponse> getTasksForDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
        
        return taskRepository.findByScheduledDateBetween(startOfDay, endOfDay).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public HousekeepingTaskResponse assignTask(Long taskId, Long userId) {
        HousekeepingTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("HousekeepingTask", "id", taskId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        task.setAssignedTo(user);
        taskRepository.save(task);

        return mapToResponse(task);
    }

    @Override
    @Transactional
    public HousekeepingTaskResponse startTask(Long taskId) {
        HousekeepingTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("HousekeepingTask", "id", taskId));

        if (task.getStatus() != TaskStatus.PENDING) {
            throw new BadRequestException("Only pending tasks can be started");
        }

        task.setStatus(TaskStatus.IN_PROGRESS);
        task.setStartedAt(LocalDateTime.now());
        taskRepository.save(task);

        return mapToResponse(task);
    }

    @Override
    @Transactional
    public HousekeepingTaskResponse completeTask(Long taskId, String completionNotes) {
        HousekeepingTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("HousekeepingTask", "id", taskId));

        if (task.getStatus() != TaskStatus.IN_PROGRESS) {
            throw new BadRequestException("Only in-progress tasks can be completed");
        }

        LocalDateTime completedAt = LocalDateTime.now();
        task.setStatus(TaskStatus.COMPLETED);
        task.setCompletedAt(completedAt);
        task.setCompletionNotes(completionNotes);

        // Calculate actual duration
        if (task.getStartedAt() != null) {
            Duration duration = Duration.between(task.getStartedAt(), completedAt);
            task.setActualDuration((int) duration.toMinutes());
        }

        taskRepository.save(task);

        // Update room status if it's a cleaning task
        if ("CLEANING".equals(task.getTaskType())) {
            Room room = task.getRoom();
            room.setStatus(RoomStatus.AVAILABLE);
            roomRepository.save(room);
        }

        return mapToResponse(task);
    }

    @Override
    @Transactional
    public HousekeepingTaskResponse cancelTask(Long taskId, String reason) {
        HousekeepingTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("HousekeepingTask", "id", taskId));

        task.setStatus(TaskStatus.CANCELLED);
        task.setNotes(task.getNotes() + "\nCancellation reason: " + reason);
        taskRepository.save(task);

        return mapToResponse(task);
    }

    @Override
    @Transactional
    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new ResourceNotFoundException("HousekeepingTask", "id", id);
        }
        taskRepository.deleteById(id);
    }

    private HousekeepingTaskResponse mapToResponse(HousekeepingTask task) {
        return HousekeepingTaskResponse.builder()
                .id(task.getId())
                .roomId(task.getRoom().getId())
                .roomNumber(task.getRoom().getRoomNumber())
                .assignedToId(task.getAssignedTo() != null ? task.getAssignedTo().getId() : null)
                .assignedToName(task.getAssignedTo() != null ? task.getAssignedTo().getFullName() : null)
                .taskType(task.getTaskType())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .scheduledDate(task.getScheduledDate())
                .startedAt(task.getStartedAt())
                .completedAt(task.getCompletedAt())
                .estimatedDuration(task.getEstimatedDuration())
                .actualDuration(task.getActualDuration())
                .notes(task.getNotes())
                .completionNotes(task.getCompletionNotes())
                .createdAt(task.getCreatedAt())
                .build();
    }
}
