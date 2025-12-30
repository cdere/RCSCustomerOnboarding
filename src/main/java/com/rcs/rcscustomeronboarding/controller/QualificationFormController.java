package com.rcs.rcscustomeronboarding.controller;

import com.rcs.rcscustomeronboarding.entity.Customer;
import com.rcs.rcscustomeronboarding.service.FormService;
import com.rcs.rcscustomeronboarding.util.SubmissionStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/onboarding/customer/validateForm")
@Slf4j
public class QualificationFormController {

    private final FormService formService;

    public QualificationFormController(FormService formService) {
        this.formService = formService;
        log.debug("QualificationFormController initialized");
    }

    @PatchMapping("/{id}/review")
    @PreAuthorize("hasAnyRole('TPM', 'SALES', 'ADMIN')")
    public ResponseEntity<Customer> review(
            @PathVariable Long id,
            @RequestParam SubmissionStatus status,
            @RequestParam String remarks,
            Principal principal,
            Authentication auth) {
        log.info("Review called for id={}, status={}, remarks={}", id, status, remarks);
        Customer updated = formService.updateStatus(id, status, remarks, auth, principal);
        log.info("Review updated customerId={} to status={}", id, updated.getStatus());
        return ResponseEntity.ok(updated);
    }
}
