package com.rcs.rcscustomeronboarding.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Role not permitted for this transition")
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String s) {
    }
}
