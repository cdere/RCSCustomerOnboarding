package com.rcs.rcscustomeronboarding.controller;

import com.rcs.rcscustomeronboarding.entity.Customer;
import com.rcs.rcscustomeronboarding.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/onboarding/customer")
public class CustomerOrderFormController {

    private final CustomerService customerService;

    @Autowired
    public CustomerOrderFormController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/addCustomer")
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    public ResponseEntity<Customer> submitForm(@Valid @RequestBody Customer form, Principal principal) {
        return customerService.addCustomer(form, principal);
    }

    @GetMapping("/getCustomer")
    @PreAuthorize("hasAnyRole('TPM', 'SALES')")
    public List<Customer> getCustomer() {
        return customerService.getCustomerDetails();
    }
}
