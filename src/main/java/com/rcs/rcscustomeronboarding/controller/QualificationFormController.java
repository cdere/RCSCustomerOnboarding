package com.rcs.rcscustomeronboarding.controller;

import com.rcs.rcscustomeronboarding.entity.Customer;
import com.rcs.rcscustomeronboarding.service.FormService;
import com.rcs.rcscustomeronboarding.util.SubmissionStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/onboarding/customer/validateForm")
public class QualificationFormController {

    private final FormService formService;

    public QualificationFormController(FormService formService) {
        this.formService = formService;
    }

    @PatchMapping("/{id}/review")
    @PreAuthorize("hasAnyRole('TPM', 'SALES', 'ADMIN')")
    public ResponseEntity<Customer> review(
            @PathVariable Long id,
            @RequestParam SubmissionStatus status,
            @RequestParam String remarks,
            Principal principal,
            Authentication auth) {
        return ResponseEntity.ok(formService.updateStatus(id, status, remarks, auth, principal));
    }
}
