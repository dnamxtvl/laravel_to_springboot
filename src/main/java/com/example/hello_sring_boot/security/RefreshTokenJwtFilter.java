package com.example.hello_sring_boot.security;

import com.example.hello_sring_boot.dto.authenication.RefreshTokenDTO;
import com.example.hello_sring_boot.exception.UnauthorizedException;
import com.example.hello_sring_boot.service.AuthService;
import com.example.hello_sring_boot.utils.Helper;
import com.example.hello_sring_boot.utils.JwtConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class RefreshTokenJwtFilter extends OncePerRequestFilter {
    private final JwtTokenManager jwtTokenManager;
    private final AuthService authService;
    private final Helper helper;
    private final HandlerExceptionResolver handlerExceptionResolver;

    public RefreshTokenJwtFilter(JwtTokenManager jwtTokenManager, AuthService authService, Helper helper, @Qualifier("handlerExceptionResolver") HandlerExceptionResolver handlerExceptionResolver) {
        this.jwtTokenManager = jwtTokenManager;
        this.authService = authService;
        this.helper = helper;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return !request.getServletPath().equals("/api/auth/refresh-token");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        log.error("Processing request: {} {}", request.getMethod(), request.getRequestURI());

        try {
            // Debug logging (optional, but helpful for user)
            log.info("Processing request: {} {}", request.getMethod(), request.getRequestURI());

            String userId;
            Cookie[] cookies = request.getCookies();
            String refreshToken = null;

            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("refreshToken".equals(cookie.getName())) {
                        refreshToken = cookie.getValue();
                        break;
                    }
                }
            }

            if (refreshToken == null) {
                log.error("Invalid or refresh token is null");
                throw new UnauthorizedException("UNAUTHORIZED");
            }

            userId = jwtTokenManager.getUserIdFromRefreshToken(refreshToken);
            log.error("User ID: {}", userId);

            final boolean validToken = jwtTokenManager.validateRefreshToken(refreshToken, userId);
            if (!validToken) {
                log.error("Invalid or expired refresh token");
                throw new UnauthorizedException("UNAUTHORIZED");
            }

            Optional<RefreshTokenDTO> userRefreshToken = authService.findByToken(helper.encryptThisString(refreshToken));
            if (userRefreshToken.isEmpty() || userRefreshToken.get().getExpiredAt().isBefore(LocalDateTime.now()) || !userRefreshToken.get().getUserId().equals(userId)) {
                log.error("Invalid or expired refresh token");
                throw new UnauthorizedException("UNAUTHORIZED");
            }

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userId, null, new ArrayList<>());
            authentication.setDetails(userRefreshToken.get());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            chain.doFilter(request, response);
        } catch (Exception e) {
            handlerExceptionResolver.resolveException(request, response, null, e);
        }
    }
}
