package com.rcs.rcscustomeronboarding.service;

import com.rcs.rcscustomeronboarding.entity.Customer;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.List;

public interface CustomerService {
    ResponseEntity<Customer> addCustomer(Customer form, Principal principal);

    //List<Customer> getAllVersions(Long id);

    List<Customer> getCustomerDetails();
}
