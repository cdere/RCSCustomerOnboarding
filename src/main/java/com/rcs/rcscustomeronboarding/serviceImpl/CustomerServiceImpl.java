package com.rcs.rcscustomeronboarding.serviceImpl;

import com.rcs.rcscustomeronboarding.entity.Customer;
import com.rcs.rcscustomeronboarding.repository.CustomerRepository;
import com.rcs.rcscustomeronboarding.service.CustomerService;
import com.rcs.rcscustomeronboarding.util.SubmissionStatus;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    public ResponseEntity<Customer> addCustomer(Customer customer, Principal principal) {
        log.info("addCustomer called for customerName={}", customer.getCustomerName());
        validateSubmission(customer);
        customer.setStatus(SubmissionStatus.SUBMITTED);
        customer.setCreatedAt(LocalDateTime.now());
        String createdBy = principal == null ? null : principal.getName();
        customer.setCreatedBy(createdBy);
        Customer saveCustomer = customerRepository.save(customer);
        log.info("Customer created with id={}", saveCustomer.getId());
        return ResponseEntity.ok(saveCustomer);
    }

    @Override
    public List<Customer> getCustomerDetails() {
        log.info("getCustomerDetails called");
        List<Customer> list = customerRepository.findByStatus(SubmissionStatus.SUBMITTED);
        log.debug("getCustomerDetails returned {} records", list == null ? 0 : list.size());
        return list;
    }

    private void validateSubmission(Customer form) {
        if (form.getCustomerName() == null || form.getCustomerName().isEmpty()) {
            log.warn("Validation failed for customer: missing name");
            throw new ValidationException("Customer is required");
        }
    }
}
