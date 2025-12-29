package com.rcs.rcscustomeronboarding.controller;

import com.rcs.rcscustomeronboarding.entity.Customer;
import com.rcs.rcscustomeronboarding.repository.CustomerRepository;
import org.springframework.data.history.Revision;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/forms/audit")
public class AuditController {

    private final CustomerRepository customerRepository;

    public AuditController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @GetMapping("/{id}/history")
    @PreAuthorize("hasAnyRole('SALES', 'TPM')")
    public List<Revision<Integer, Customer>> getCustomerHistory(@PathVariable Long id) {
        // Returns all historical states of the customer including who changed it
        return customerRepository.findRevisions(id).getContent();
    }
}
