package com.example.hello_sring_boot.configuration;

import com.example.hello_sring_boot.dto.response.UserWithPermsResponse;
import com.example.hello_sring_boot.security.JwtProperties;
import com.example.hello_sring_boot.security.JwtTokenManager;
import com.example.hello_sring_boot.service.UserService;
import com.example.hello_sring_boot.utils.JwtConstants;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class WebSocketSecurityConfig implements WebSocketMessageBrokerConfigurer {
    private final RedisTemplate<String, String> redisTemplate;
    private final JwtTokenManager jwtTokenManager;
    private final JwtProperties jwtProperties;
    private final UserService userService;
    private static final String REDIS_BLACKLIST_KEY = "auth:blacklist:tokens";

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(@Nonnull Message<?> message, @Nonnull MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                assert accessor != null;
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String authHeader = accessor.getFirstNativeHeader("Authorization");

                    if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        Object[] validateToken = validateToken(authHeader);
                        String userId = (String) validateToken[0];
                        String authToken = (String) validateToken[1];
                        checkTokenAuthentication(userId, authToken);

                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(userId, null, null);

                        accessor.setUser(authentication);
                    } else {
                        throw new MessageDeliveryException("Xác thực thất bại!");
                    }
                }

                return message;
            }
        });
    }

    private String[] validateToken(String header) {
        try {
            String authToken = header.replace(JwtConstants.TOKEN_PREFIX, Strings.EMPTY);

            Boolean isBlacklisted = redisTemplate.opsForSet().isMember(REDIS_BLACKLIST_KEY, authToken);
            if (Boolean.TRUE.equals(isBlacklisted)) {
                log.warn("Token is blacklisted: {}", authToken);
                throw new MessageDeliveryException("Xác thực thất bại!");
            }
            String userId = jwtTokenManager.getUserIdFromAccessToken(authToken);

            final SecurityContext securityContext = SecurityContextHolder.getContext();
            final boolean canBeStartTokenValidation = Objects.nonNull(userId)
                    && Objects.isNull(securityContext.getAuthentication());

            if (!canBeStartTokenValidation) {
                throw new MessageDeliveryException("Xác thực thất bại!");
            }

            return new String[]{ userId, authToken };
        } catch (Exception e) {
            throw new MessageDeliveryException("Xác thực thất bại!");
        }
    }

    private void checkTokenAuthentication(String userId, String authToken) {
        try {
            final boolean validToken = jwtTokenManager.validateAccessToken(authToken, userId);
            if (!validToken) {
                throw new MessageDeliveryException("Xác thực thất bại!");
            }

            UserWithPermsResponse userWithPerms = userService.findWithRolesAndPermissionsById(userId);
            if (userWithPerms.getStatusActive() == 0) {
                log.error("User is not active, blacklisting token");
                redisTemplate.opsForValue().set(REDIS_BLACKLIST_KEY + ":" + authToken, "", jwtProperties.getExpirationMinute() * 60L, TimeUnit.SECONDS);
                throw new MessageDeliveryException("Xác thực thất bại!");
            }
        } catch (Exception e) {
            throw new MessageDeliveryException("Xác thực thất bại!");
        }
    }
}
