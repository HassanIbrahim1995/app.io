package com.hotel.management.service;

import com.hotel.management.dto.request.EmployeeRequest;
import com.hotel.management.dto.response.EmployeeResponse;
import com.hotel.management.entity.enums.EmploymentStatus;

import java.util.List;

public interface EmployeeService {
    EmployeeResponse createEmployee(EmployeeRequest request);
    EmployeeResponse updateEmployee(Long id, EmployeeRequest request);
    EmployeeResponse getEmployeeById(Long id);
    EmployeeResponse getEmployeeByNumber(String employeeNumber);
    EmployeeResponse getEmployeeByUserId(Long userId);
    List<EmployeeResponse> getAllEmployees();
    List<EmployeeResponse> getActiveEmployees();
    List<EmployeeResponse> getEmployeesByDepartment(String department);
    List<EmployeeResponse> getEmployeesByStatus(EmploymentStatus status);
    void deleteEmployee(Long id);
    void terminateEmployee(Long id, String reason);
}
