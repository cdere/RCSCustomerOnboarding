package com.rcs.rcscustomeronboarding.service;

import com.rcs.rcscustomeronboarding.entity.Customer;
import com.rcs.rcscustomeronboarding.exception.CustomerNotFoundException;
import com.rcs.rcscustomeronboarding.repository.CustomerRepository;
import com.rcs.rcscustomeronboarding.serviceImpl.FormServiceImpl;
import com.rcs.rcscustomeronboarding.util.SubmissionStatus;
import com.rcs.rcscustomeronboarding.util.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.security.Principal;
import java.util.Optional;
import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class FormServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private Principal principal;

    private FormServiceImpl formService;

    private AutoCloseable mocksCloseable;

    @BeforeEach
    void setUp() {
        mocksCloseable = MockitoAnnotations.openMocks(this);
        formService = new FormServiceImpl(customerRepository);
        // Ensure repository.save returns the passed entity (prevents null returns from mock)
        when(customerRepository.save(any(Customer.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @AfterEach
    void tearDown() throws Exception {
        if (mocksCloseable != null) mocksCloseable.close();
    }



    @Test
    void updateStatus_missingCustomer_throwsNotFound() {
        when(customerRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class,
                () -> formService.updateStatus(2L, SubmissionStatus.IN_REVIEW, "r", authentication, principal));
    }


    @Test
    void updateStatus_invalidTransition_throwsIllegalState() {
        Customer existing = new Customer();
        existing.setId(4L);
        existing.setStatus(SubmissionStatus.DRAFT);

        when(customerRepository.findById(4L)).thenReturn(Optional.of(existing));
        when(authentication.getAuthorities()).thenReturn(
                (Collection) Arrays.asList((GrantedAuthority) () -> UserRole.ROLE_ADMIN.name())
        );

        // Admin can do anything role-wise, but DRAFT -> APPROVED is invalid by workflow
        assertThrows(IllegalStateException.class,
                () -> formService.updateStatus(4L, SubmissionStatus.APPROVED, "y", authentication, principal));
    }

    @Test
    void updateStatus_happyPath_tpmInReview() {
        Customer existing = new Customer();
        existing.setId(5L);
        existing.setStatus(SubmissionStatus.SUBMITTED);

        when(customerRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(authentication.getAuthorities()).thenReturn(
                (Collection) Arrays.asList((GrantedAuthority) () -> UserRole.ROLE_TPM.name())
        );
        when(principal.getName()).thenReturn("tpmUser");

        Customer result = formService.updateStatus(5L, SubmissionStatus.IN_REVIEW, "reviewing", authentication, principal);

        assertThat(result.getStatus()).isEqualTo(SubmissionStatus.IN_REVIEW);
        assertThat(result.getRemarks()).isEqualTo("reviewing");
        assertThat(result.getUpdatedBy()).isEqualTo("tpmUser");
        verify(customerRepository).save(result);
    }

    @Test
    void updateStatus_noAuthorities_throwsAccessDenied() {
        Customer existing = new Customer();
        existing.setId(6L);
        existing.setStatus(SubmissionStatus.SUBMITTED);

        when(customerRepository.findById(6L)).thenReturn(Optional.of(existing));
        when(authentication.getAuthorities()).thenReturn(java.util.Collections.emptyList());

        assertThrows(AccessDeniedException.class,
                () -> formService.updateStatus(6L, SubmissionStatus.IN_REVIEW, "r", authentication, principal));
    }
}
