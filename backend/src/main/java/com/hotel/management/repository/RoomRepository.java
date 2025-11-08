package com.hotel.management.repository;

import com.hotel.management.entity.Room;
import com.hotel.management.entity.enums.RoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long>, JpaSpecificationExecutor<Room> {
    
    Optional<Room> findByRoomNumber(String roomNumber);
    
    List<Room> findByStatus(RoomStatus status);
    
    List<Room> findByFloor(Integer floor);
    
    List<Room> findByRoomTypeId(Long roomTypeId);
    
    @Query("SELECT r FROM Room r WHERE r.status = :status AND r.roomType.id = :roomTypeId")
    List<Room> findByStatusAndRoomType(RoomStatus status, Long roomTypeId);
    
    @Query("SELECT r FROM Room r WHERE r.id NOT IN " +
           "(SELECT res.room.id FROM Reservation res WHERE " +
           "res.status IN ('CONFIRMED', 'CHECKED_IN') AND " +
           "((res.checkInDate <= :checkOut AND res.checkOutDate >= :checkIn)))")
    List<Room> findAvailableRooms(@Param("checkIn") LocalDate checkIn, 
                                   @Param("checkOut") LocalDate checkOut);
    
    @Query("SELECT COUNT(r) FROM Room r WHERE r.status = :status")
    Long countByStatus(RoomStatus status);
}
