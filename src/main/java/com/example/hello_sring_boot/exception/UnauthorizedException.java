package com.example.hello_sring_boot.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Getter
public class UnauthorizedException extends ResponseStatusException {
    private final String errorCode;

    public UnauthorizedException(String errorCode) {
        super(HttpStatus.UNAUTHORIZED, "You are not authorized to access this resource");
        this.errorCode = errorCode;
    }

    public HttpStatus getStatus() {
        return HttpStatus.UNAUTHORIZED;
    }
}
