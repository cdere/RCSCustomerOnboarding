package com.rcs.rcscustomeronboarding.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Form not found")
public class CustomerNotFoundException extends RuntimeException {
}
