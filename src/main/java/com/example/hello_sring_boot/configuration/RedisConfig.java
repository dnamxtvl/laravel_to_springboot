package com.example.hello_sring_boot.configuration;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import java.time.Duration;
import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
        @Value("${spring.redis.host}")
        private String redisHost;

        @Value("${spring.redis.port}")
        private int redisPort;

        private RedisClient redisClient() {
                return RedisClient.create(RedisURI.builder()
                        .withHost(redisHost)
                        .withPort(redisPort)
                        .withSsl(false)
                        .build());
        }

        @Bean
        public ProxyManager<String> lettuceBasedProxyManager() {
                RedisClient redisClient = redisClient();
                StatefulRedisConnection<String, byte[]> redisConnection = redisClient
                        .connect(RedisCodec.of(StringCodec.UTF8, ByteArrayCodec.INSTANCE));

                return LettuceBasedProxyManager.builderFor(redisConnection)
                        .withExpirationStrategy(ExpirationAfterWriteStrategy.basedOnTimeForRefillingBucketUpToMax(Duration.ofMinutes(1L)))
                        .build();
        }

        @Bean
        public Supplier<BucketConfiguration> bucketConfiguration() {
                return () -> BucketConfiguration.builder()
                        .addLimit(Bandwidth.simple(200L, Duration.ofMinutes(1L)))
                        .addLimit(Bandwidth.simple(10L, Duration.ofSeconds(1L)))
                        .build();
        }

        @Bean
        public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
                RedisTemplate<String, Object> template = new RedisTemplate<>();
                template.setConnectionFactory(connectionFactory);
                template.setKeySerializer(new StringRedisSerializer());
                template.setValueSerializer(new StringRedisSerializer());
                return template;
        }
}
