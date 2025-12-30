package com.rcs.rcscustomeronboarding.controller;

import com.rcs.rcscustomeronboarding.entity.Customer;
import com.rcs.rcscustomeronboarding.service.FormService;
import com.rcs.rcscustomeronboarding.util.SubmissionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.security.Principal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class QualificationFormControllerTest {

    @Mock
    private FormService formService;

    @Mock
    private Principal principal;

    @Mock
    private Authentication auth;

    private QualificationFormController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new QualificationFormController(formService);
    }

    @Test
    void review_delegatesToService_and_returnsOk() {
        Customer updated = new Customer();
        updated.setId(10L);
        when(formService.updateStatus(10L, SubmissionStatus.IN_REVIEW, "r", auth, principal)).thenReturn(updated);

        ResponseEntity<Customer> resp = controller.review(10L, SubmissionStatus.IN_REVIEW, "r", principal, auth);
        assertThat(resp.getBody().getId()).isEqualTo(10L);
        verify(formService).updateStatus(10L, SubmissionStatus.IN_REVIEW, "r", auth, principal);
    }
}

