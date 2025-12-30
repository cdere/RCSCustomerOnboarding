package com.rcs.rcscustomeronboarding.controller;

import com.rcs.rcscustomeronboarding.entity.Customer;
import com.rcs.rcscustomeronboarding.service.CustomerService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/onboarding/customer")
@Slf4j
public class CustomerOrderFormController {

    private final CustomerService customerService;

    @Autowired
    public CustomerOrderFormController(CustomerService customerService) {
        this.customerService = customerService;
        log.debug("CustomerOrderFormController initialized");
    }

    @PostMapping("/addCustomer")
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    public ResponseEntity<Customer> submitForm(@Valid @RequestBody Customer form, Principal principal) {
        log.info("Submit form request received for customerName={}", form.getCustomerName());
        ResponseEntity<Customer> resp = customerService.addCustomer(form, principal);
        log.info("Submit form processed for customerId={}", resp.getBody() != null ? resp.getBody().getId() : null);
        return resp;
    }

    @GetMapping("/getCustomer")
    @PreAuthorize("hasAnyRole('TPM', 'SALES', 'ADMIN')")
    public List<Customer> getCustomer() {
        log.info("getCustomer called to fetch submitted customers");
        List<Customer> list = customerService.getCustomerDetails();
        log.debug("getCustomer returned {} records", list == null ? 0 : list.size());
        return list;
    }
}
