package com.hotel.management.controller;

import com.hotel.management.dto.request.EmployeeRequest;
import com.hotel.management.dto.response.EmployeeResponse;
import com.hotel.management.entity.enums.EmploymentStatus;
import com.hotel.management.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
@Tag(name = "Employee Management", description = "Employee management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    @Operation(summary = "Create new employee")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<EmployeeResponse> createEmployee(@Valid @RequestBody EmployeeRequest request) {
        EmployeeResponse response = employeeService.createEmployee(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update employee")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<EmployeeResponse> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeRequest request) {
        EmployeeResponse response = employeeService.updateEmployee(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get employee by ID")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable Long id) {
        EmployeeResponse response = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/number/{employeeNumber}")
    @Operation(summary = "Get employee by employee number")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<EmployeeResponse> getEmployeeByNumber(@PathVariable String employeeNumber) {
        EmployeeResponse response = employeeService.getEmployeeByNumber(employeeNumber);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get employee by user ID")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<EmployeeResponse> getEmployeeByUserId(@PathVariable Long userId) {
        EmployeeResponse response = employeeService.getEmployeeByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all employees")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<EmployeeResponse>> getAllEmployees() {
        List<EmployeeResponse> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/active")
    @Operation(summary = "Get all active employees")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'FRONT_DESK')")
    public ResponseEntity<List<EmployeeResponse>> getActiveEmployees() {
        List<EmployeeResponse> employees = employeeService.getActiveEmployees();
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/department/{department}")
    @Operation(summary = "Get employees by department")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<EmployeeResponse>> getEmployeesByDepartment(@PathVariable String department) {
        List<EmployeeResponse> employees = employeeService.getEmployeesByDepartment(department);
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get employees by status")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<EmployeeResponse>> getEmployeesByStatus(@PathVariable EmploymentStatus status) {
        List<EmployeeResponse> employees = employeeService.getEmployeesByStatus(status);
        return ResponseEntity.ok(employees);
    }

    @PostMapping("/{id}/terminate")
    @Operation(summary = "Terminate employee")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> terminateEmployee(
            @PathVariable Long id,
            @RequestParam String reason) {
        employeeService.terminateEmployee(id, reason);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete employee")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}
