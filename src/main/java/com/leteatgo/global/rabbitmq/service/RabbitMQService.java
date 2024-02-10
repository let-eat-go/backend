package com.leteatgo.global.rabbitmq.service;

import com.leteatgo.domain.notification.dto.NotificationDto;
import com.leteatgo.global.rabbitmq.listener.MultiMessageListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RabbitMQService {

    private final RabbitAdmin rabbitAdmin;
    private final RabbitTemplate rabbitTemplate;
    private final TopicExchange topicExchange;
    private final DirectMessageListenerContainer container;
    private final MultiMessageListener messageListener;

    public String createQueue(String queue, String userId) {
        String queueName = queue + userId;

        Queue newQueue = new Queue(queueName, false);
        rabbitAdmin.declareQueue(newQueue);

        Binding binding = BindingBuilder.bind(newQueue).to(topicExchange).with(queueName);
        rabbitAdmin.declareBinding(binding);

        return queueName;
    }

    public void subscribe(String queueName) {
        container.addQueueNames(queueName);
        container.setMessageListener(messageListener);
        container.start();
    }

    public void removeSubscribe(String queue, String memberId) {
        String queueName = queue + memberId;
        container.removeQueueNames(queueName);
        rabbitAdmin.deleteQueue(queueName);
    }

    public void publish(String queue, String userId, NotificationDto notificationDto) {
        String routingKey = queue + userId;
        rabbitTemplate.convertAndSend(topicExchange.getName(), routingKey, notificationDto);
    }
}
