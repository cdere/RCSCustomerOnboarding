package com.rcs.rcscustomeronboarding.service;

import com.rcs.rcscustomeronboarding.entity.Customer;
import com.rcs.rcscustomeronboarding.util.SubmissionStatus;
import org.springframework.security.core.Authentication;

import java.security.Principal;

public interface FormService {

    Customer updateStatus(Long id, SubmissionStatus status, String remarks, Authentication firstAuthorityOpt, Principal principal);
}
