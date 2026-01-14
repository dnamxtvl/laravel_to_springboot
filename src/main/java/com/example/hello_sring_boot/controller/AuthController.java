package com.example.hello_sring_boot.controller;

import com.example.hello_sring_boot.dto.request.ChangePasswordRequest;
import com.example.hello_sring_boot.dto.request.ForgotPasswordRequest;
import com.example.hello_sring_boot.dto.request.LoginRequest;
import com.example.hello_sring_boot.dto.response.ApiResponse;
import com.example.hello_sring_boot.dto.response.LoginResponse;
import com.example.hello_sring_boot.dto.response.UserResponse;
import com.example.hello_sring_boot.service.AuthService;
import com.example.hello_sring_boot.service.JwtTokenService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final JwtTokenService jwtTokenService;
    private final AuthService authService;

    public AuthController(JwtTokenService jwtTokenService, AuthService authService) {
        this.jwtTokenService = jwtTokenService;
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.error("Login request: {}", loginRequest);
        LoginResponse token = jwtTokenService.getLoginResponse(loginRequest);
        ApiResponse<LoginResponse> response = ApiResponse.<LoginResponse>builder().data(token).build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest body) {
        authService.forgotPassword(body.getEmail());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/change-password/{token}")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest body, @PathVariable String token) throws BadRequestException {
        authService.changePassword(token, body.getPassword());
        return ResponseEntity.ok().build();
    }
}
