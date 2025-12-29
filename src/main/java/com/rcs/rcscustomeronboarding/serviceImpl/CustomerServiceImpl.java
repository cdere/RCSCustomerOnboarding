package com.rcs.rcscustomeronboarding.serviceImpl;

import com.rcs.rcscustomeronboarding.entity.Customer;
import com.rcs.rcscustomeronboarding.repository.CustomerRepository;
import com.rcs.rcscustomeronboarding.service.CustomerService;
import com.rcs.rcscustomeronboarding.util.SubmissionStatus;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    public ResponseEntity<Customer> addCustomer(Customer customer, Principal principal) {
        validateSubmission(customer);
        customer.setStatus(SubmissionStatus.SUBMITTED);
        customer.setCreatedAt(LocalDateTime.now());
        customer.setCreatedBy(principal.getName());
        Customer saveCustomer = customerRepository.save(customer);
        return ResponseEntity.ok(saveCustomer);
    }

    @Override
    public List<Customer> getCustomerDetails() {
        return customerRepository.findByStatus(SubmissionStatus.SUBMITTED);
    }

    private void validateSubmission(Customer form) {
        if (form.getCustomerName() == null || form.getCustomerName().isEmpty()) {
            throw new ValidationException("Customer is required");
        }
    }
}
