package com.example.hello_sring_boot.rabbitmq.consumer;

import com.example.hello_sring_boot.configuration.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RabbitMQConsumer {
    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void handle(String message) {
        log.error("handle message listener topic {}", message);
    }
}
