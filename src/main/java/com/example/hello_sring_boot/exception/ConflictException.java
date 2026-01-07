package com.example.hello_sring_boot.exception;

import org.springframework.http.HttpStatus;

public class ConflictException extends BaseException {
    public ConflictException(String message) {
        super(HttpStatus.CONFLICT, "CONFLICT", message);
    }

    public ConflictException(String resourceName, String fieldName, Object fieldValue) {
        super(HttpStatus.CONFLICT, "CONFLICT",
                String.format("%s already exists with %s: '%s'", resourceName, fieldName, fieldValue));
    }
}
