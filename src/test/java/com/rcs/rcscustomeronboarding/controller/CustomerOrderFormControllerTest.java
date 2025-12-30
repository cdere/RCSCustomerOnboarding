package com.rcs.rcscustomeronboarding.controller;

import com.rcs.rcscustomeronboarding.entity.Customer;
import com.rcs.rcscustomeronboarding.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CustomerOrderFormControllerTest {

    @Mock
    private CustomerService customerService;

    @Mock
    private Principal principal;

    private CustomerOrderFormController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new CustomerOrderFormController(customerService);
    }

    @Test
    void submitForm_delegatesToService() {
        Customer c = new Customer();
        c.setCustomerName("A");

        Customer saved = new Customer();
        saved.setId(5L);
        when(customerService.addCustomer(c, principal)).thenReturn(ResponseEntity.ok(saved));

        ResponseEntity<Customer> resp = controller.submitForm(c, principal);
        assertThat(resp.getBody().getId()).isEqualTo(5L);
        verify(customerService).addCustomer(c, principal);
    }

    @Test
    void getCustomer_delegatesToService() {
        Customer one = new Customer();
        one.setCustomerName("A");
        when(customerService.getCustomerDetails()).thenReturn(List.of(one));

        List<Customer> list = controller.getCustomer();
        assertThat(list).hasSize(1);
        verify(customerService).getCustomerDetails();
    }
}

