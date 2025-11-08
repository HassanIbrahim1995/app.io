package com.hotel.management.dto.request;

import com.hotel.management.entity.enums.EmploymentStatus;
import com.hotel.management.entity.enums.EmploymentType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class EmployeeRequest {
    
    @NotBlank(message = "Employee number is required")
    private String employeeNumber;
    
    @NotBlank(message = "Department is required")
    private String department;
    
    @NotBlank(message = "Position is required")
    private String position;
    
    @NotNull(message = "Employment type is required")
    private EmploymentType employmentType;
    
    @NotNull(message = "Employment status is required")
    private EmploymentStatus employmentStatus;
    
    @NotNull(message = "Hire date is required")
    @PastOrPresent(message = "Hire date cannot be in the future")
    private LocalDate hireDate;
    
    private LocalDate terminationDate;
    
    @DecimalMin(value = "0.0", message = "Salary must be positive")
    private BigDecimal salary;
    
    @DecimalMin(value = "0.0", message = "Hourly rate must be positive")
    private BigDecimal hourlyRate;
    
    private String supervisor;
    
    private String workSchedule;
    
    private String emergencyContactName;
    
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Invalid phone number")
    private String emergencyContactPhone;
    
    private String emergencyContactRelation;
    
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;
    
    private String ssnLast4;
    
    private String skills;
    
    private String certifications;
    
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "5.0")
    private BigDecimal performanceRating;
    
    @Min(0)
    private Integer vacationDays;
    
    @Min(0)
    private Integer sickDays;
    
    private String notes;
    
    // User information
    @NotNull(message = "User ID is required")
    private Long userId;
}
