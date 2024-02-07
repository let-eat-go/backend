package com.leteatgo.domain.notification.service;

import com.leteatgo.domain.notification.dto.NotificationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
public class RabbitMQService {

    private static final String NOTIFICATION_QUEUE = "notification.queue";

    private final SimpleMessageListenerContainer container;
    private final SseEmitterService sseEmitterService;
    private final RabbitAdmin rabbitAdmin;
    private final RabbitTemplate rabbitTemplate;
    private final FanoutExchange fanoutExchange;

    public void subscribe(String userId, SseEmitter sseEmitter) {
        String queueName = createQueue(userId);
        container.setQueueNames(queueName);
        container.setMessageListener((message) -> {
            String data = new String(message.getBody());
            sseEmitterService.send(data, userId, sseEmitter);
        });

        container.start();
    }

    private String createQueue(String userId) {
        String queueName = NOTIFICATION_QUEUE + userId;

        Queue queue = new Queue(queueName, false);
        rabbitAdmin.declareQueue(queue);

        Binding binding = BindingBuilder.bind(queue).to(fanoutExchange);
        rabbitAdmin.declareBinding(binding);

        return queueName;
    }

    public void removeSubscribe(String memberId) {
        container.removeQueueNames(NOTIFICATION_QUEUE + memberId);
    }

    public void publish(String userId, NotificationDto notificationDto) {
        String queueName = NOTIFICATION_QUEUE + userId;
        rabbitTemplate.convertAndSend(queueName, notificationDto);
    }
}
