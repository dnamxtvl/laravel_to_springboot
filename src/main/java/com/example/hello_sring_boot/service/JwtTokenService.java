package com.example.hello_sring_boot.service;

import com.example.hello_sring_boot.dto.request.LoginRequest;
import com.example.hello_sring_boot.dto.response.LoginResponse;
import com.example.hello_sring_boot.entity.User;
import com.example.hello_sring_boot.repository.UserRepository;
import com.example.hello_sring_boot.security.JwtTokenManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class JwtTokenService {
    private final JwtTokenManager jwtTokenManager;
    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    public LoginResponse getLoginResponse(LoginRequest loginRequest) {

        final String email = loginRequest.getEmail();
        final String password = loginRequest.getPassword();
        final UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                email, password);
        authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        final User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        final String token = jwtTokenManager.generateToken(user);

        log.info("{} has successfully logged in!", email);

        return new LoginResponse(token);
    }
}
