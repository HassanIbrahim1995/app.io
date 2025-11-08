package com.hotel.management.repository;

import com.hotel.management.entity.Employee;
import com.hotel.management.entity.enums.EmploymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    
    Optional<Employee> findByEmployeeNumber(String employeeNumber);
    
    Optional<Employee> findByUserId(Long userId);
    
    List<Employee> findByDepartment(String department);
    
    List<Employee> findByEmploymentStatus(EmploymentStatus status);
    
    @Query("SELECT e FROM Employee e WHERE e.employmentStatus = 'ACTIVE'")
    List<Employee> findAllActive();
    
    @Query("SELECT e FROM Employee e WHERE e.supervisor = :supervisorName AND e.employmentStatus = 'ACTIVE'")
    List<Employee> findBySupervisor(String supervisorName);
}
