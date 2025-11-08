package com.hotel.management.repository;

import com.hotel.management.entity.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomTypeRepository extends JpaRepository<RoomType, Long> {
    
    Optional<RoomType> findByName(String name);
    
    boolean existsByName(String name);
}
