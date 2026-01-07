package com.example.hello_sring_boot.controller;

import com.example.hello_sring_boot.dto.request.LoginRequest;
import com.example.hello_sring_boot.dto.response.LoginResponse;
import com.example.hello_sring_boot.service.JwtTokenService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final JwtTokenService jwtTokenService;

    public AuthController(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest loginRequest) {
        log.error("Login request: {}", loginRequest);
        return jwtTokenService.getLoginResponse(loginRequest);
    }
}
