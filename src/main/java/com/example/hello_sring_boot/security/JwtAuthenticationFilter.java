package com.example.hello_sring_boot.security;

import com.example.hello_sring_boot.dto.response.UserWithPermsResponse;
import com.example.hello_sring_boot.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import com.example.hello_sring_boot.service.UserService;
import com.example.hello_sring_boot.utils.JwtConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenManager jwtTokenManager;
    private final JwtProperties jwtProperties;
    private final UserService userService;
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final RedisTemplate<String, String> redisTemplate;
    private static final String REDIS_BLACKLIST_KEY = "auth:blacklist:tokens";

    public JwtAuthenticationFilter(JwtTokenManager jwtTokenManager, JwtProperties jwtProperties, UserService userService, @Qualifier("handlerExceptionResolver") HandlerExceptionResolver handlerExceptionResolver, RedisTemplate<String, String> redisTemplate) {
        this.jwtTokenManager = jwtTokenManager;
        this.jwtProperties = jwtProperties;
        this.userService = userService;
        this.handlerExceptionResolver = handlerExceptionResolver;
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getServletPath().equals("/api/auth/refresh-token");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) {
        final String header = request.getHeader(JwtConstants.HEADER_STRING);
        try {
            log.info("Processing request: {} {}", request.getMethod(), request.getRequestURI());
            if (Objects.nonNull(header) && header.startsWith(JwtConstants.TOKEN_PREFIX)) {
                Object[] validateToken = validateToken(header);
                String userId = (String) validateToken[0];
                String authToken = (String) validateToken[1];

                processTokenAuthentication(userId, authToken, request, response, chain);
            }
            chain.doFilter(request, response);
        } catch (Exception e) {
            handlerExceptionResolver.resolveException(request, response, null, e);
        }
    }

    private Object[] validateToken(String header) {
        try {
            String authToken = header.replace(JwtConstants.TOKEN_PREFIX, Strings.EMPTY);

            Boolean isBlacklisted = redisTemplate.opsForSet().isMember(REDIS_BLACKLIST_KEY, authToken);
            if (Boolean.TRUE.equals(isBlacklisted)) {
                log.warn("Token is blacklisted: {}", authToken);
                throw new UnauthorizedException("access_token.revoked");
            }
            String userId = jwtTokenManager.getUserIdFromAccessToken(authToken);

            final SecurityContext securityContext = SecurityContextHolder.getContext();
            final boolean canBeStartTokenValidation = Objects.nonNull(userId)
                    && Objects.isNull(securityContext.getAuthentication());

            if (!canBeStartTokenValidation) {
                throw new UnauthorizedException("UnAuthorized");
            }

            return new String[]{ userId, authToken };
        } catch (Exception e) {
            throw new UnauthorizedException("UnAuthorized");
        }
    }

    private void processTokenAuthentication(String userId, String authToken, HttpServletRequest request, HttpServletResponse response, FilterChain chain) {
        try {
            final boolean validToken = jwtTokenManager.validateAccessToken(authToken, userId);
            if (!validToken) {
                throw new UnauthorizedException("access_token.invalid");
            }

            UserWithPermsResponse userWithPerms = userService.findWithRolesAndPermissionsById(userId);
            if (userWithPerms.getPermissions().isEmpty()) {
                log.error("User has no permissions");
                throw new UnauthorizedException("user.has_no_permissions");
            }

            if (userWithPerms.getStatusActive() == 0) {
                log.error("User is not active, blacklisting token");
                redisTemplate.opsForValue().set(REDIS_BLACKLIST_KEY + ":" + authToken, "", jwtProperties.getExpirationMinute() * 60L, TimeUnit.SECONDS);
                throw new UnauthorizedException("user.not_active");
            }

            List<SimpleGrantedAuthority> authorities = userWithPerms.getPermissions().stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            UserDetails userDetails = new User(userWithPerms.getEmail(), userWithPerms.getPassword(), authorities);

            final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("Authentication successful. Logged in username : {} ", userWithPerms.getEmail());
        } catch (Exception e) {
            throw new UnauthorizedException("UnAuthorized");
        }
    }
}
