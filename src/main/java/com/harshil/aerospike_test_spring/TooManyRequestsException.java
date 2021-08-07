package com.harshil.aerospike_test_spring;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus( value = HttpStatus.TOO_MANY_REQUESTS)
public class TooManyRequestsException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public TooManyRequestsException(String message) {
        super(message);
    }
}
