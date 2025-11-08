package com.hotel.management.repository;

import com.hotel.management.entity.Amenity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AmenityRepository extends JpaRepository<Amenity, Long> {
    
    Optional<Amenity> findByName(String name);
    
    List<Amenity> findByIsPremium(Boolean isPremium);
}
