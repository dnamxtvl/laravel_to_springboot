package com.example.hello_sring_boot.security;

import com.example.hello_sring_boot.dto.response.UserResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import com.example.hello_sring_boot.service.UserService;
import com.example.hello_sring_boot.utils.JwtConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenManager jwtTokenManager;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        final String header = request.getHeader(JwtConstants.HEADER_STRING);

        // Debug logging (optional, but helpful for user)
        log.info("Processing request: {} {}", request.getMethod(), request.getRequestURI());

        String email = null;
        String authToken = null;
        if (Objects.nonNull(header) && header.startsWith(JwtConstants.TOKEN_PREFIX)) {

            authToken = header.replace(JwtConstants.TOKEN_PREFIX, Strings.EMPTY);

            try {
                email = jwtTokenManager.getEmailFromToken(authToken);
            } catch (Exception e) {
                log.error("Authentication Exception : {}", e.getMessage());
                chain.doFilter(request, response);
                return;
            }
        }

        final SecurityContext securityContext = SecurityContextHolder.getContext();

        final boolean canBeStartTokenValidation = Objects.nonNull(email)
                && Objects.isNull(securityContext.getAuthentication());

        if (!canBeStartTokenValidation) {
            chain.doFilter(request, response);
            return;
        }

        final UserResponse user = userService.getUserByEmail(email);
        List<SimpleGrantedAuthority> authorities = List
                .of(new SimpleGrantedAuthority("ROLE_" + user.getTypeUser().name()));
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(user.getEmail(),
                user.getPassword(), authorities);
        final boolean validToken = jwtTokenManager.validateToken(authToken, user.getEmail());

        if (!validToken) {
            chain.doFilter(request, response);
            return;
        }

        final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, user.getPassword(), userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        securityContext.setAuthentication(authentication);

        log.info("Authentication successful. Logged in username : {} ", email);

        chain.doFilter(request, response);
    }
}
