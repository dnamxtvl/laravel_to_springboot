package com.example.hello_sring_boot.controller;

import com.example.hello_sring_boot.dto.authenication.RefreshTokenDTO;
import com.example.hello_sring_boot.dto.request.ChangePasswordRequest;
import com.example.hello_sring_boot.dto.request.ForgotPasswordRequest;
import com.example.hello_sring_boot.dto.request.LoginRequest;
import com.example.hello_sring_boot.dto.response.ApiResponse;
import com.example.hello_sring_boot.dto.response.LoginResponse;
import com.example.hello_sring_boot.security.JwtProperties;
import com.example.hello_sring_boot.service.AuthService;
import com.example.hello_sring_boot.service.JwtTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final JwtTokenService jwtTokenService;
    private final AuthService authService;
    private final JwtProperties jwtProperties;

    public AuthController(JwtTokenService jwtTokenService, AuthService authService, JwtProperties jwtProperties) {
        this.jwtTokenService = jwtTokenService;
        this.authService = authService;
        this.jwtProperties = jwtProperties;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        log.error("Login request: {}", loginRequest);
        LoginResponse token = jwtTokenService.getLoginResponse(loginRequest);
        Cookie cookie = new Cookie("refreshToken", token.getRefreshToken());
        cookie.setMaxAge((int) (jwtProperties.getExpirationMinuteRefresh() * 60));
        cookie.setHttpOnly(true);
        cookie.setPath("/");

        boolean isProduction = "production".equals(System.getenv("ENVIRONMENT"));
        cookie.setSecure(isProduction);
        response.addCookie(cookie);

        ApiResponse<LoginResponse> result = ApiResponse.<LoginResponse>builder().data(token).build();

        return ResponseEntity.ok(result);
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

    @PostMapping("/refresh-token")
    @ResponseBody
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(Authentication authentication, HttpServletResponse response) {
        RefreshTokenDTO tokenDTO = (RefreshTokenDTO) authentication.getDetails();
        LoginResponse token = authService.refreshToken(tokenDTO.getToken(), tokenDTO.getUserId());
        Cookie cookie = new Cookie("refreshToken", token.getRefreshToken());
        cookie.setMaxAge((int) (jwtProperties.getExpirationMinuteRefresh() * 60));
        cookie.setHttpOnly(true);
        cookie.setPath("/");

        boolean isProduction = "production".equals(System.getenv("ENVIRONMENT"));
        cookie.setSecure(isProduction);
        response.addCookie(cookie);
        ApiResponse<LoginResponse> result = ApiResponse.<LoginResponse>builder().data(token).build();

        return ResponseEntity.ok(result);
    }
}
