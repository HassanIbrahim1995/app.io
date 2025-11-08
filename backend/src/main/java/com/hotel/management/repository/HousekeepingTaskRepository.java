package com.hotel.management.repository;

import com.hotel.management.entity.HousekeepingTask;
import com.hotel.management.entity.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HousekeepingTaskRepository extends JpaRepository<HousekeepingTask, Long> {
    
    List<HousekeepingTask> findByRoomId(Long roomId);
    
    List<HousekeepingTask> findByAssignedToId(Long userId);
    
    List<HousekeepingTask> findByStatus(TaskStatus status);
    
    @Query("SELECT t FROM HousekeepingTask t WHERE t.scheduledDate BETWEEN :startDate AND :endDate")
    List<HousekeepingTask> findByScheduledDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT t FROM HousekeepingTask t WHERE t.assignedTo.id = :userId AND t.status = :status")
    List<HousekeepingTask> findByAssignedToAndStatus(Long userId, TaskStatus status);
    
    @Query("SELECT t FROM HousekeepingTask t WHERE t.status = 'PENDING' AND t.scheduledDate < :date")
    List<HousekeepingTask> findOverdueTasks(LocalDateTime date);
}
