package com.rcs.rcscustomeronboarding.service;

import com.rcs.rcscustomeronboarding.entity.Customer;
import com.rcs.rcscustomeronboarding.repository.CustomerRepository;
import com.rcs.rcscustomeronboarding.serviceImpl.CustomerServiceImpl;
import com.rcs.rcscustomeronboarding.util.SubmissionStatus;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private Principal principal;

    private CustomerServiceImpl customerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        customerService = new CustomerServiceImpl(customerRepository);
    }

    @Test
    void addCustomer_happyPath_setsFieldsAndSaves() {
        Customer input = new Customer();
        input.setCustomerName("Test Co");
        input.setRcsProvider("provider");
        input.setExpectedMonthlyVolume(2000);

        when(principal.getName()).thenReturn("user1");

        Customer saved = new Customer();
        saved.setId(1L);
        when(customerRepository.save(any(Customer.class))).thenReturn(saved);

        ResponseEntity<Customer> response = customerService.addCustomer(input, principal);

        assertThat(response.getBody()).isEqualTo(saved);

        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository).save(captor.capture());
        Customer toSave = captor.getValue();

        assertThat(toSave.getStatus()).isEqualTo(SubmissionStatus.SUBMITTED);
        assertThat(toSave.getCreatedBy()).isEqualTo("user1");
        assertThat(toSave.getCreatedAt()).isNotNull();
    }

    @Test
    void addCustomer_missingCustomerName_throwsValidationException() {
        Customer input = new Customer();
        input.setCustomerName(null);

        assertThrows(ValidationException.class, () -> customerService.addCustomer(input, principal));

        verify(customerRepository, never()).save(any());
    }

    @Test
    void getCustomerDetails_returnsSubmittedOnly() {
        Customer one = new Customer();
        one.setStatus(SubmissionStatus.SUBMITTED);
        Customer two = new Customer();
        two.setStatus(SubmissionStatus.DRAFT);

        when(customerRepository.findByStatus(SubmissionStatus.SUBMITTED)).thenReturn(List.of(one));

        List<Customer> result = customerService.getCustomerDetails();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(SubmissionStatus.SUBMITTED);
    }
}

