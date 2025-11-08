package com.hotel.management.repository;

import com.hotel.management.entity.Reservation;
import com.hotel.management.entity.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long>, JpaSpecificationExecutor<Reservation> {
    
    Optional<Reservation> findByReservationNumber(String reservationNumber);
    
    List<Reservation> findByGuestId(Long guestId);
    
    List<Reservation> findByRoomId(Long roomId);
    
    List<Reservation> findByStatus(ReservationStatus status);
    
    @Query("SELECT r FROM Reservation r WHERE r.guest.id = :guestId AND r.status = :status")
    List<Reservation> findByGuestIdAndStatus(Long guestId, ReservationStatus status);
    
    @Query("SELECT r FROM Reservation r WHERE r.checkInDate = :date AND r.status IN ('CONFIRMED', 'PENDING')")
    List<Reservation> findTodayCheckIns(@Param("date") LocalDate date);
    
    @Query("SELECT r FROM Reservation r WHERE r.checkOutDate = :date AND r.status = 'CHECKED_IN'")
    List<Reservation> findTodayCheckOuts(@Param("date") LocalDate date);
    
    @Query("SELECT r FROM Reservation r WHERE r.checkInDate <= :endDate AND r.checkOutDate >= :startDate")
    List<Reservation> findByDateRange(@Param("startDate") LocalDate startDate, 
                                      @Param("endDate") LocalDate endDate);
    
    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.checkInDate = :date")
    Long countByCheckInDate(LocalDate date);
    
    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.status = :status")
    Long countByStatus(ReservationStatus status);
}
