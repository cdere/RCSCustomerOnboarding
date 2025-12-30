package com.rcs.rcscustomeronboarding.controller;

import com.rcs.rcscustomeronboarding.entity.Customer;
import com.rcs.rcscustomeronboarding.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.history.Revision;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/forms/audit")
@Slf4j
public class AuditController {

    private final CustomerRepository customerRepository;

    public AuditController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
        log.debug("AuditController initialized");
    }

    @GetMapping("/{id}/history")
    @PreAuthorize("hasAnyRole('SALES', 'ADMIN')")
    public List<Revision<Integer, Customer>> getCustomerHistory(@PathVariable Long id) {
        log.info("Fetching audit history for customer id={}", id);
        List<Revision<Integer, Customer>> content = customerRepository.findRevisions(id).getContent();
        log.debug("Found {} revisions for customer id={}", content.size(), id);
        return content;
    }
}
