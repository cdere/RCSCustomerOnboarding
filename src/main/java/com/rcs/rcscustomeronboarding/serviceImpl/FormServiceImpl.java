package com.rcs.rcscustomeronboarding.serviceImpl;

import com.rcs.rcscustomeronboarding.entity.Customer;
import com.rcs.rcscustomeronboarding.exception.CustomerNotFoundException;
import com.rcs.rcscustomeronboarding.repository.CustomerRepository;
import com.rcs.rcscustomeronboarding.service.FormService;
import com.rcs.rcscustomeronboarding.util.SubmissionStatus;
import com.rcs.rcscustomeronboarding.util.UserRole;
import lombok.RequiredArgsConstructor;
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
public class FormServiceImpl implements FormService {

    private final CustomerRepository customerRepository;

    public Customer updateStatus(Long id, SubmissionStatus newStatus, String remarks, Authentication authentication, Principal principal) {
        Customer form = customerRepository.findById(id)
                .orElseThrow(CustomerNotFoundException::new);

        UserRole role = extractRole(authentication);
        validateTransition(role, newStatus);
        applyUpdate(form, newStatus, remarks, principal);

        return customerRepository.save(form);
    }

    private UserRole extractRole(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(UserRole::valueOf)
                .findFirst()
                .orElseThrow(() -> new AccessDeniedException("No valid role found"));
    }

    private void validateTransition(
            UserRole role,
            SubmissionStatus newStatus
    ) {
        Optional.ofNullable(ROLE_RULES.get(role))
                .filter(rule -> rule.test(newStatus))
                .orElseThrow(() -> new AccessDeniedException("Role " + role + " cannot transition to " + newStatus));
    }

    private void applyUpdate(
            Customer form,
            SubmissionStatus newStatus,
            String remarks,
            Principal principal
    ) {
        form.setStatus(newStatus);
        form.setRemarks(remarks);
        form.setUpdatedAt(LocalDateTime.now());
        form.setUpdatedBy(principal.getName());
    }

    private static final Map<UserRole, Predicate<SubmissionStatus>> ROLE_RULES =
            new EnumMap<>(UserRole.class);

    static {
        ROLE_RULES.put(UserRole.ROLE_SALES,
                status -> status == SubmissionStatus.APPROVED);

        ROLE_RULES.put(UserRole.ROLE_TPM,
                status -> true);

        ROLE_RULES.put(UserRole.ROLE_ADMIN,
                status -> true);
    }
}
