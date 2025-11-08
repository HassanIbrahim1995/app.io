package com.hotel.management.repository;

import com.hotel.management.entity.CityTax;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CityTaxRepository extends JpaRepository<CityTax, Long> {
    
    List<CityTax> findByCity(String city);
    
    @Query("SELECT ct FROM CityTax ct WHERE ct.city = :city AND ct.isActive = true AND " +
           "(ct.effectiveFrom IS NULL OR ct.effectiveFrom <= :date) AND " +
           "(ct.effectiveTo IS NULL OR ct.effectiveTo >= :date)")
    List<CityTax> findActiveTaxesByCity(String city, LocalDate date);
    
    List<CityTax> findByIsActiveTrue();
}
