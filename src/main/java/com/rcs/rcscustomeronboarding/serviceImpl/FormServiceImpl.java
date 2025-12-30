package com.rcs.rcscustomeronboarding.serviceImpl;

import com.rcs.rcscustomeronboarding.entity.Customer;
import com.rcs.rcscustomeronboarding.exception.CustomerNotFoundException;
import com.rcs.rcscustomeronboarding.repository.CustomerRepository;
import com.rcs.rcscustomeronboarding.service.FormService;
import com.rcs.rcscustomeronboarding.util.SubmissionStatus;
import com.rcs.rcscustomeronboarding.util.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class FormServiceImpl implements FormService {

    private final CustomerRepository customerRepository;

    public Customer updateStatus(Long id, SubmissionStatus newStatus, String remarks, Authentication authentication, Principal principal) {
        log.info("updateStatus called for id={}, newStatus={}, updatedBy={}", id, newStatus, principal == null ? null : principal.getName());
        Customer form = customerRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Customer with id={} not found", id);
                    return new CustomerNotFoundException();
                });

        form.transitionTo(newStatus);
        UserRole role = extractRole(authentication);
        validateTransition(role, newStatus);
        applyUpdate(form, newStatus, remarks, principal);

        Customer saved = customerRepository.save(form);
        log.info("Customer id={} updated to status={}, saved successfully", id, newStatus);
        return saved;
    }

    private UserRole extractRole(Authentication authentication) {
        try {
            UserRole role = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .map(UserRole::valueOf)
                    .findFirst()
                    .orElseThrow(() -> new AccessDeniedException("No valid role found"));
            log.debug("extractRole found role={}", role);
            return role;
        } catch (Exception e) {
            log.error("Error extracting role from authentication", e);
            throw e;
        }
    }

    private void validateTransition(
            UserRole role,
            SubmissionStatus newStatus
    ) {
        Optional.ofNullable(ROLE_RULES.get(role))
                .filter(rule -> rule.test(newStatus))
                .orElseThrow(() -> {
                    log.warn("Role {} cannot transition to {}", role, newStatus);
                    return new AccessDeniedException("Role " + role + " cannot transition to " + newStatus);
                });
        log.debug("validateTransition passed for role={}, newStatus={}", role, newStatus);
    }

    private void applyUpdate(
            Customer form,
            SubmissionStatus newStatus,
            String remarks,
            Principal principal
    ) {
        String updatedBy = principal == null ? null : principal.getName();
        form.setStatus(newStatus);
        form.setRemarks(remarks);
        form.setUpdatedAt(LocalDateTime.now());
        form.setUpdatedBy(updatedBy);
        log.debug("applyUpdate set status={}, remarks={}, updatedBy={}", newStatus, remarks, updatedBy);
    }

    private static final Map<UserRole, Predicate<SubmissionStatus>> ROLE_RULES =
            new EnumMap<>(UserRole.class);

    static {
        ROLE_RULES.put(UserRole.ROLE_SALES,
                status -> status == SubmissionStatus.APPROVED);

        ROLE_RULES.put(UserRole.ROLE_TPM,
                status -> status == SubmissionStatus.IN_REVIEW);

        ROLE_RULES.put(UserRole.ROLE_ADMIN,
                status -> true);
    }
}
