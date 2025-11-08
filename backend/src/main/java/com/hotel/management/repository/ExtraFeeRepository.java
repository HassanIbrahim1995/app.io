package com.hotel.management.repository;

import com.hotel.management.entity.ExtraFee;
import com.hotel.management.entity.enums.FeeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExtraFeeRepository extends JpaRepository<ExtraFee, Long> {
    
    Optional<ExtraFee> findByFeeCode(String feeCode);
    
    List<ExtraFee> findByFeeType(FeeType feeType);
    
    @Query("SELECT ef FROM ExtraFee ef WHERE ef.isActive = true ORDER BY ef.displayOrder")
    List<ExtraFee> findAllActive();
    
    @Query("SELECT ef FROM ExtraFee ef WHERE ef.isMandatory = true AND ef.isActive = true")
    List<ExtraFee> findMandatoryFees();
}
