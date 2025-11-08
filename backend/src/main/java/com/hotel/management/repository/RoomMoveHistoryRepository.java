package com.hotel.management.repository;

import com.hotel.management.entity.RoomMoveHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomMoveHistoryRepository extends JpaRepository<RoomMoveHistory, Long> {
    
    List<RoomMoveHistory> findByReservationId(Long reservationId);
    
    List<RoomMoveHistory> findByFromRoomId(Long roomId);
    
    List<RoomMoveHistory> findByToRoomId(Long roomId);
}
