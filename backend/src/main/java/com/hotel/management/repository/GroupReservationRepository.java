package com.hotel.management.repository;

import com.hotel.management.entity.GroupReservation;
import com.hotel.management.entity.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupReservationRepository extends JpaRepository<GroupReservation, Long> {
    
    Optional<GroupReservation> findByGroupNumber(String groupNumber);
    
    List<GroupReservation> findByStatus(ReservationStatus status);
    
    List<GroupReservation> findByContactEmail(String email);
}
