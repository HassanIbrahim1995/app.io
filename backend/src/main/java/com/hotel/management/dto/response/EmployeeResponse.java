package com.hotel.management.dto.response;

import com.hotel.management.entity.enums.EmploymentStatus;
import com.hotel.management.entity.enums.EmploymentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponse {
    private Long id;
    private String employeeNumber;
    private String department;
    private String position;
    private EmploymentType employmentType;
    private EmploymentStatus employmentStatus;
    private LocalDate hireDate;
    private LocalDate terminationDate;
    private BigDecimal salary;
    private BigDecimal hourlyRate;
    private String supervisor;
    private String workSchedule;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String emergencyContactRelation;
    private LocalDate dateOfBirth;
    private String skills;
    private String certifications;
    private BigDecimal performanceRating;
    private Integer vacationDays;
    private Integer sickDays;
    private String notes;
    
    // User information
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
}
