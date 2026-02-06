package com.example.hello_sring_boot.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Topic để Client lắng nghe (Subcribe)
        config.enableSimpleBroker("/topic", "/queue");
        config.enableSimpleBroker("/topic", "/chatroom");
        // Prefix để Client gửi dữ liệu lên Server (MessageMapping)
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // Cho phép React truy cập (CORS)
                .withSockJS(); // Hỗ trợ fallback cho trình duyệt cũ
    }
}