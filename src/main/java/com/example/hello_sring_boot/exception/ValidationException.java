package com.example.hello_sring_boot.exception;

import org.springframework.http.HttpStatus;

public class ValidationException extends BaseException {
    public ValidationException(String message) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "VALIDATION_FAILED", message);
    }
}
