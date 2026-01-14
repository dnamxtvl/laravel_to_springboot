package com.example.hello_sring_boot.service;

import com.example.hello_sring_boot.dto.request.LoginRequest;
import com.example.hello_sring_boot.dto.response.LoginResponse;
import com.example.hello_sring_boot.entity.User;
import com.example.hello_sring_boot.entity.UserRefreshToken;
import com.example.hello_sring_boot.repository.UserRefreshTokenRepository;
import com.example.hello_sring_boot.repository.UserRepository;
import com.example.hello_sring_boot.security.JwtProperties;
import com.example.hello_sring_boot.security.JwtTokenManager;
import com.example.hello_sring_boot.utils.Helper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class JwtTokenService {
    private final JwtTokenManager jwtTokenManager;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final UserRefreshTokenRepository userRefreshTokenRepository;
    private final Helper helper;
    private final JwtProperties jwtProperties;

    @Transactional
    public LoginResponse getLoginResponse(LoginRequest loginRequest) {
        final String email = loginRequest.getEmail();
        final String password = loginRequest.getPassword();
        final UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                email, password);
        authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        final User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        LoginResponse loginResponse = jwtTokenManager.generateToken(user);

        UserRefreshToken userRefreshToken = UserRefreshToken.builder().userId(user.getId())
                .token(helper.encryptThisString(loginResponse.getRefreshToken()))
                .expiredAt(LocalDateTime.now().plusMinutes(jwtProperties.getExpirationMinuteRefresh()))
                .build();

        userRefreshTokenRepository.save(userRefreshToken);

        return loginResponse;
    }
}
