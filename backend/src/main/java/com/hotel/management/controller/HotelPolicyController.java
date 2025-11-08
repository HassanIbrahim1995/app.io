package com.hotel.management.controller;

import com.hotel.management.entity.HotelPolicy;
import com.hotel.management.entity.PolicyAcceptance;
import com.hotel.management.entity.User;
import com.hotel.management.entity.enums.PolicyCategory;
import com.hotel.management.service.HotelPolicyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/policies")
@RequiredArgsConstructor
@Tag(name = "Hotel Policies", description = "Hotel policy management endpoints")
public class HotelPolicyController {

    private final HotelPolicyService policyService;

    @PostMapping
    @Operation(summary = "Create hotel policy")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<HotelPolicy> createPolicy(@Valid @RequestBody HotelPolicy policy) {
        HotelPolicy created = policyService.createPolicy(policy);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update hotel policy")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<HotelPolicy> updatePolicy(
            @PathVariable Long id,
            @Valid @RequestBody HotelPolicy policy) {
        HotelPolicy updated = policyService.updatePolicy(id, policy);
        return ResponseEntity.ok(updated);
    }

    @GetMapping
    @Operation(summary = "Get all active policies")
    public ResponseEntity<List<HotelPolicy>> getActivePolicies() {
        List<HotelPolicy> policies = policyService.getActivePolicies();
        return ResponseEntity.ok(policies);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get policy by ID")
    public ResponseEntity<HotelPolicy> getPolicyById(@PathVariable Long id) {
        HotelPolicy policy = policyService.getPolicyById(id);
        return ResponseEntity.ok(policy);
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get policy by code")
    public ResponseEntity<HotelPolicy> getPolicyByCode(@PathVariable String code) {
        HotelPolicy policy = policyService.getPolicyByCode(code);
        return ResponseEntity.ok(policy);
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Get policies by category")
    public ResponseEntity<List<HotelPolicy>> getPoliciesByCategory(@PathVariable PolicyCategory category) {
        List<HotelPolicy> policies = policyService.getPoliciesByCategory(category);
        return ResponseEntity.ok(policies);
    }

    @GetMapping("/requiring-acceptance")
    @Operation(summary = "Get policies requiring acceptance")
    public ResponseEntity<List<HotelPolicy>> getPoliciesRequiringAcceptance(
            @RequestParam(defaultValue = "GUESTS") String appliesTo) {
        List<HotelPolicy> policies = policyService.getPoliciesRequiringAcceptance(appliesTo);
        return ResponseEntity.ok(policies);
    }

    @PostMapping("/{policyId}/accept")
    @Operation(summary = "Accept a policy")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<PolicyAcceptance> acceptPolicy(
            @PathVariable Long policyId,
            @AuthenticationPrincipal User user,
            HttpServletRequest request) {
        
        String ipAddress = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        
        PolicyAcceptance acceptance = policyService.acceptPolicy(policyId, user, ipAddress, userAgent);
        return ResponseEntity.ok(acceptance);
    }

    @GetMapping("/my-acceptances")
    @Operation(summary = "Get my accepted policies")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<PolicyAcceptance>> getMyAcceptances(@AuthenticationPrincipal User user) {
        List<PolicyAcceptance> acceptances = policyService.getUserAcceptedPolicies(user);
        return ResponseEntity.ok(acceptances);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete policy")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePolicy(@PathVariable Long id) {
        policyService.deletePolicy(id);
        return ResponseEntity.noContent().build();
    }
}
