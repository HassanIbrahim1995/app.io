package com.hotel.management.service.impl;

import com.hotel.management.entity.HotelPolicy;
import com.hotel.management.entity.PolicyAcceptance;
import com.hotel.management.entity.User;
import com.hotel.management.entity.enums.PolicyCategory;
import com.hotel.management.exception.BadRequestException;
import com.hotel.management.exception.ResourceNotFoundException;
import com.hotel.management.repository.HotelPolicyRepository;
import com.hotel.management.repository.PolicyAcceptanceRepository;
import com.hotel.management.service.HotelPolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HotelPolicyServiceImpl implements HotelPolicyService {

    private final HotelPolicyRepository policyRepository;
    private final PolicyAcceptanceRepository acceptanceRepository;

    @Override
    @Transactional
    public HotelPolicy createPolicy(HotelPolicy policy) {
        if (policyRepository.findByPolicyCode(policy.getPolicyCode()).isPresent()) {
            throw new BadRequestException("Policy with code " + policy.getPolicyCode() + " already exists");
        }
        return policyRepository.save(policy);
    }

    @Override
    @Transactional
    public HotelPolicy updatePolicy(Long id, HotelPolicy policy) {
        HotelPolicy existing = getPolicyById(id);

        existing.setTitle(policy.getTitle());
        existing.setCategory(policy.getCategory());
        existing.setContent(policy.getContent());
        existing.setShortDescription(policy.getShortDescription());
        existing.setEffectiveDate(policy.getEffectiveDate());
        existing.setExpiryDate(policy.getExpiryDate());
        existing.setIsMandatory(policy.getIsMandatory());
        existing.setRequiresAcceptance(policy.getRequiresAcceptance());
        existing.setIsActive(policy.getIsActive());
        existing.setDisplayOrder(policy.getDisplayOrder());
        existing.setAppliesTo(policy.getAppliesTo());

        return policyRepository.save(existing);
    }

    @Override
    public HotelPolicy getPolicyById(Long id) {
        return policyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("HotelPolicy", "id", id));
    }

    @Override
    public HotelPolicy getPolicyByCode(String code) {
        return policyRepository.findByPolicyCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("HotelPolicy", "code", code));
    }

    @Override
    public List<HotelPolicy> getAllPolicies() {
        return policyRepository.findAll();
    }

    @Override
    public List<HotelPolicy> getActivePolicies() {
        return policyRepository.findEffectivePolicies(LocalDate.now());
    }

    @Override
    public List<HotelPolicy> getPoliciesByCategory(PolicyCategory category) {
        return policyRepository.findByCategory(category);
    }

    @Override
    public List<HotelPolicy> getPoliciesRequiringAcceptance(String appliesTo) {
        return policyRepository.findPoliciesRequiringAcceptance(appliesTo);
    }

    @Override
    @Transactional
    public void deletePolicy(Long id) {
        if (!policyRepository.existsById(id)) {
            throw new ResourceNotFoundException("HotelPolicy", "id", id);
        }
        policyRepository.deleteById(id);
    }

    @Override
    @Transactional
    public PolicyAcceptance acceptPolicy(Long policyId, User user, String ipAddress, String userAgent) {
        HotelPolicy policy = getPolicyById(policyId);

        // Check if already accepted
        PolicyAcceptance existing = acceptanceRepository.findByUserAndPolicy(user.getId(), policyId);
        if (existing != null) {
            throw new BadRequestException("Policy already accepted by user");
        }

        PolicyAcceptance acceptance = PolicyAcceptance.builder()
                .user(user)
                .policy(policy)
                .acceptedAt(LocalDateTime.now())
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .policyVersion(policy.getVersion())
                .build();

        return acceptanceRepository.save(acceptance);
    }

    @Override
    public List<PolicyAcceptance> getUserAcceptedPolicies(User user) {
        return acceptanceRepository.findByUserId(user.getId());
    }

    @Override
    public boolean hasUserAcceptedPolicy(Long userId, Long policyId) {
        return acceptanceRepository.findByUserAndPolicy(userId, policyId) != null;
    }
}
