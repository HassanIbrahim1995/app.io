package com.hotel.management.service;

import com.hotel.management.entity.HotelPolicy;
import com.hotel.management.entity.PolicyAcceptance;
import com.hotel.management.entity.User;
import com.hotel.management.entity.enums.PolicyCategory;

import java.util.List;

public interface HotelPolicyService {
    HotelPolicy createPolicy(HotelPolicy policy);
    HotelPolicy updatePolicy(Long id, HotelPolicy policy);
    HotelPolicy getPolicyById(Long id);
    HotelPolicy getPolicyByCode(String code);
    List<HotelPolicy> getAllPolicies();
    List<HotelPolicy> getActivePolicies();
    List<HotelPolicy> getPoliciesByCategory(PolicyCategory category);
    List<HotelPolicy> getPoliciesRequiringAcceptance(String appliesTo);
    void deletePolicy(Long id);
    PolicyAcceptance acceptPolicy(Long policyId, User user, String ipAddress, String userAgent);
    List<PolicyAcceptance> getUserAcceptedPolicies(User user);
    boolean hasUserAcceptedPolicy(Long userId, Long policyId);
}
