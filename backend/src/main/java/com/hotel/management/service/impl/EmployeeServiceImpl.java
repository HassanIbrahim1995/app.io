package com.hotel.management.service.impl;

import com.hotel.management.dto.request.EmployeeRequest;
import com.hotel.management.dto.response.EmployeeResponse;
import com.hotel.management.entity.Employee;
import com.hotel.management.entity.User;
import com.hotel.management.entity.enums.EmploymentStatus;
import com.hotel.management.exception.BadRequestException;
import com.hotel.management.exception.ResourceNotFoundException;
import com.hotel.management.repository.EmployeeRepository;
import com.hotel.management.repository.UserRepository;
import com.hotel.management.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public EmployeeResponse createEmployee(EmployeeRequest request) {
        // Check if employee number already exists
        if (employeeRepository.findByEmployeeNumber(request.getEmployeeNumber()).isPresent()) {
            throw new BadRequestException("Employee number already exists");
        }

        // Get user
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));

        // Check if user already has an employee profile
        if (employeeRepository.findByUserId(request.getUserId()).isPresent()) {
            throw new BadRequestException("User already has an employee profile");
        }

        // Validate termination date
        if (request.getTerminationDate() != null && 
            request.getTerminationDate().isBefore(request.getHireDate())) {
            throw new BadRequestException("Termination date cannot be before hire date");
        }

        Employee employee = Employee.builder()
                .user(user)
                .employeeNumber(request.getEmployeeNumber())
                .department(request.getDepartment())
                .position(request.getPosition())
                .employmentType(request.getEmploymentType())
                .employmentStatus(request.getEmploymentStatus())
                .hireDate(request.getHireDate())
                .terminationDate(request.getTerminationDate())
                .salary(request.getSalary())
                .hourlyRate(request.getHourlyRate())
                .supervisor(request.getSupervisor())
                .workSchedule(request.getWorkSchedule())
                .emergencyContactName(request.getEmergencyContactName())
                .emergencyContactPhone(request.getEmergencyContactPhone())
                .emergencyContactRelation(request.getEmergencyContactRelation())
                .dateOfBirth(request.getDateOfBirth())
                .ssnLast4(request.getSsnLast4())
                .skills(request.getSkills())
                .certifications(request.getCertifications())
                .performanceRating(request.getPerformanceRating())
                .vacationDays(request.getVacationDays())
                .sickDays(request.getSickDays())
                .notes(request.getNotes())
                .build();

        employee = employeeRepository.save(employee);
        return mapToResponse(employee);
    }

    @Override
    @Transactional
    public EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));

        // Validate termination date
        if (request.getTerminationDate() != null && 
            request.getTerminationDate().isBefore(employee.getHireDate())) {
            throw new BadRequestException("Termination date cannot be before hire date");
        }

        employee.setDepartment(request.getDepartment());
        employee.setPosition(request.getPosition());
        employee.setEmploymentType(request.getEmploymentType());
        employee.setEmploymentStatus(request.getEmploymentStatus());
        employee.setTerminationDate(request.getTerminationDate());
        employee.setSalary(request.getSalary());
        employee.setHourlyRate(request.getHourlyRate());
        employee.setSupervisor(request.getSupervisor());
        employee.setWorkSchedule(request.getWorkSchedule());
        employee.setEmergencyContactName(request.getEmergencyContactName());
        employee.setEmergencyContactPhone(request.getEmergencyContactPhone());
        employee.setEmergencyContactRelation(request.getEmergencyContactRelation());
        employee.setSkills(request.getSkills());
        employee.setCertifications(request.getCertifications());
        employee.setPerformanceRating(request.getPerformanceRating());
        employee.setVacationDays(request.getVacationDays());
        employee.setSickDays(request.getSickDays());
        employee.setNotes(request.getNotes());

        employee = employeeRepository.save(employee);
        return mapToResponse(employee);
    }

    @Override
    public EmployeeResponse getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
        return mapToResponse(employee);
    }

    @Override
    public EmployeeResponse getEmployeeByNumber(String employeeNumber) {
        Employee employee = employeeRepository.findByEmployeeNumber(employeeNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "employeeNumber", employeeNumber));
        return mapToResponse(employee);
    }

    @Override
    public EmployeeResponse getEmployeeByUserId(Long userId) {
        Employee employee = employeeRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "userId", userId));
        return mapToResponse(employee);
    }

    @Override
    public List<EmployeeResponse> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeResponse> getActiveEmployees() {
        return employeeRepository.findAllActive().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeResponse> getEmployeesByDepartment(String department) {
        return employeeRepository.findByDepartment(department).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeResponse> getEmployeesByStatus(EmploymentStatus status) {
        return employeeRepository.findByEmploymentStatus(status).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Employee", "id", id);
        }
        employeeRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void terminateEmployee(Long id, String reason) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));

        employee.setEmploymentStatus(EmploymentStatus.TERMINATED);
        employee.setTerminationDate(LocalDate.now());
        employee.setNotes(employee.getNotes() + "\nTermination reason: " + reason);

        employeeRepository.save(employee);
    }

    private EmployeeResponse mapToResponse(Employee employee) {
        User user = employee.getUser();
        return EmployeeResponse.builder()
                .id(employee.getId())
                .employeeNumber(employee.getEmployeeNumber())
                .department(employee.getDepartment())
                .position(employee.getPosition())
                .employmentType(employee.getEmploymentType())
                .employmentStatus(employee.getEmploymentStatus())
                .hireDate(employee.getHireDate())
                .terminationDate(employee.getTerminationDate())
                .salary(employee.getSalary())
                .hourlyRate(employee.getHourlyRate())
                .supervisor(employee.getSupervisor())
                .workSchedule(employee.getWorkSchedule())
                .emergencyContactName(employee.getEmergencyContactName())
                .emergencyContactPhone(employee.getEmergencyContactPhone())
                .emergencyContactRelation(employee.getEmergencyContactRelation())
                .dateOfBirth(employee.getDateOfBirth())
                .skills(employee.getSkills())
                .certifications(employee.getCertifications())
                .performanceRating(employee.getPerformanceRating())
                .vacationDays(employee.getVacationDays())
                .sickDays(employee.getSickDays())
                .notes(employee.getNotes())
                .userId(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .build();
    }
}
